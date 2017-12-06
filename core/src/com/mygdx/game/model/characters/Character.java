package com.mygdx.game.model.characters;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.mygdx.game.constants.InputConverter.DirectionalInput;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.actions.Attack;
import com.mygdx.game.model.actions.ActionSegment.ActionState;
import com.mygdx.game.model.characters.CollisionCheck.CollisionType;
import com.mygdx.game.model.characters.player.Player.PlayerModel;
import com.mygdx.game.model.actions.ActionSequence.StaggerType;
import com.mygdx.game.model.effects.EntityEffect;
import com.mygdx.game.model.effects.XMovementEffect;
import com.mygdx.game.model.effects.XMovementEffectSettings;
import com.mygdx.game.model.effects.YMovementEffect;
import com.mygdx.game.model.effects.YMovementEffectSettings;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.AssaultInterceptor;
import com.mygdx.game.model.events.ObjectListener;
import com.mygdx.game.model.projectiles.Explosion;
import com.mygdx.game.model.projectiles.Projectile;
import com.mygdx.game.model.weapons.Weapon;
import com.mygdx.game.wrappers.StringWrapper;

public abstract class Character implements ModelListener {
	
	private EntityUIModel characterUIData;
	private CharacterModel characterData;
	
	public enum Direction {
		LEFT, RIGHT, NaN;
	}
	
	public enum TurnAngle {
	   ZERO, FOURTYFIVE, NEGFOURTYFIVE, NINETY, NEGNINETY;
	}
	
	public Character(String characterName) {
		setCharacterUIData(new EntityUIModel(characterName, EntityUIDataType.CHARACTER));
	}
	

	float stateTime = 0f;
	public void act(float delta, TiledMapTileLayer collisionLayer) {
		characterData.update(delta, collisionLayer);
		characterUIData.setCurrentFrame(characterData, delta);
		stateTime += delta;
		if (stateTime > 60f) {
//			System.exit(0);
		}
	}
	
	public void setPatrolInfo(Array <Float> patrolWaypoints, float patrolDuration, float breakDuration) {
		this.getCharacterData().setPatrolInfo(patrolWaypoints, patrolDuration, breakDuration);
	}
	
	public void actionStagger() {
		this.characterUIData.stagger();
	}
	
	public void endActionStagger() {
		this.characterUIData.endStagger();
	}
	
	//=============================================================//
	//----------------------------MODEL----------------------------//
	//=============================================================//

	public abstract class CharacterModel extends EntityModel{
		
		final float slowingAccel = 3000;
		
		String state;
		float currentHealth, maxHealth, currentWill, maxWill, attack, currentStability, maxStability, currentTension, maxTension;
		boolean isImmuneToInjury, attacking;
		protected boolean isInAir;
		protected boolean walking;
		float walkingTime;
		boolean sprinting;
		boolean didChangeState;
		boolean actionStaggering;
		boolean facingLeft;
		boolean actionLocked;
		boolean alreadyDead;
		boolean isSlowingDown;
		boolean injuredStaggering;
		boolean queuedJump;
	    float actionStaggerTime;
	    float tempVelocityX;
	    float tempVelocityY;
		String name, uuid; 
	    protected boolean movementConditionActivated;
	    float movementConditionActivatedTime;
	    int currentJumpTokens, maxJumpTokens;
	    TurnAngle turnAngle;
		
		
		ActionListener actionListener;
		ObjectListener objectListener;
		ModelListener modelListener;
		
		EntityUIModel uiModel;
		CharacterProperties properties;
		ArrayList <ActionSequence> processingActionSequences;
		ArrayDeque <ActionSequence> nextActiveActionSequences;
		Array <Weapon> weapons;
		Weapon currentWeapon;
		

		
		//Debug
		float stateTime;
		
		public CharacterModel(String characterName, EntityUIModel uiModel, ModelListener modelListener) {
			this.nextActiveActionSequences = new ArrayDeque<ActionSequence>();
			this.processingActionSequences = new ArrayList <ActionSequence>();
			this.turnAngle = TurnAngle.ZERO;
			setState(CharacterConstants.idleState);
			isImmuneToInjury = false;
			attacking = false;
			isInAir = true;
			facingLeft = false;
			actionLocked = false;
			walking = false;
			alreadyDead = false;
			isSlowingDown = false;
			sprinting = false;
			injuredStaggering = false;
			queuedJump = false;
			this.movementConditionActivated = false;
			this.movementConditionActivatedTime = 0f;
			stateTime = 0f;
			actionStaggerTime = 0f;
			tempVelocityX = 0f;
			tempVelocityY = 0f;
			walkingTime = 0f;
			this.modelListener = modelListener;

			UUID id = UUID.randomUUID();
			this.uuid = id.toString();
			this.name = characterName;
			this.uiModel = uiModel;
			this.properties = JSONController.loadCharacterProperties(characterName);
			this.properties.setSource(this);
			this.widthCoefficient = this.properties.getWidthCoefficient();
			this.heightCoefficient = this.properties.getHeightCoefficient();
			this.xOffsetModifier = this.properties.xCollisionOffsetModifier;
			this.yOffsetModifier = this.properties.yCollisionOffsetModifier;
			this.shouldRespectEntityCollision = this.properties.shouldRespectEntityCollisions;
			this.shouldRespectObjectCollision = this.properties.shouldRespectObjectCollisions;
			this.shouldRespectTileCollision = this.properties.shouldRespectTileCollisions;
			this.isRespectingEntityCollision = this.shouldRespectEntityCollision;
			this.isRespectingObjectCollision = this.shouldRespectObjectCollision;
			this.isRespectingTileCollision = this.shouldRespectTileCollision;
			setMaxHealth(properties.getMaxHealth());
			setCurrentHealth(properties.getMaxHealth());
			setMaxWill(properties.getMaxWill());
			setCurrentWill(properties.getMaxWill());
			setAttack(properties.getAttack());
			setMaxStability(properties.getMaxStability());
			setCurrentStability(properties.getMaxStability(), null);
			setMaxTension(properties.getMaxTension());
			setCurrentTension(0);
			setMaxJumpTokens(properties.getMaxJumpTokens());
			setCurrentJumpTokens(properties.getMaxJumpTokens());
			acceleration.y = -properties.getGravity();
			weapons = new Array <Weapon> ();
			for (StringWrapper key : this.getCharacterProperties().getWeaponKeys()) {
				weapons.add(new Weapon(key.value, characterName));
			}
			if (weapons.size > 0) {
				currentWeapon = weapons.get(0);
			}
//			this.fixedHurtBoxProperties = this.getCharacterProperties().defaultHurtboxProperties;
			this.updateHurtBoxProperties(this.getCharacterProperties().defaultHurtboxProperties);
					
		}
		
		public void update(float delta, TiledMapTileLayer collisionLayer) {
			this.handleCollisionRespectChecks();
			this.setGameplaySize(delta, collisionLayer);
			this.movementWithCollisionDetection(delta, collisionLayer);
			this.manageAutomaticStates(delta, collisionLayer);
			this.handleEffects(delta);
			this.handleActionSequences(delta);
			if (this.actionStaggering) {
				this.actionStaggerTime += delta;
				if (this.actionStaggerTime > EntityUIModel.standardStaggerDuration) {
					this.endActionStagger();
				}
			}
			
			if (this.movementConditionActivated) {
				this.movementConditionActivatedTime += delta;
				if (movementConditionActivatedTime > .5f && this.walkingTime > .5f && !this.sprinting) {
					sprinting = true;
				}
			}
			else {
				this.movementConditionActivatedTime = 0f;
				sprinting = false;
			}
			
			if (this.timeToDisrespectOneWayCollision > 0f) {
				this.timeToDisrespectOneWayCollision -= delta;
				if (this.timeToDisrespectOneWayCollision <= 0f) {
					this.timeToDisrespectOneWayCollision = 0f;
					this.isRespectingOneWayCollision = this.shouldRespectTileCollision;
				}
			}
			
//			if (this.timeToDisrespectSlopeCollision > 0f) {
//				this.timeToDisrespectSlopeCollision -= delta;
//				if (this.timeToDisrespectSlopeCollision <= 0f) {
//					this.timeToDisrespectSlopeCollision = 0f;
//					this.isRespectingSlopeCollision = this.shouldRespectTileCollision;
//				}
//			}

			//Debug
			this.stateTime += delta;
			this.deathCheck();
		}
		
		public void setGameplaySize(float delta, TiledMapTileLayer collisionLayer) {

			
			super.setGameplayCollisionSize(delta);
			//clamp velocity
			if (this.getVelocity().y > this.properties.jumpSpeed)
				this.getVelocity().y = this.properties.jumpSpeed;
			else if (this.getVelocity().y < -this.properties.jumpSpeed) {
				this.getVelocity().y = -this.properties.jumpSpeed; 
			}

			if (this.getXMove() != null)
			{
				float xVelocityMax = this.getXMove().getMaxVelocity();
				if (this.velocity.x > xVelocityMax) {
					this.velocity.x = xVelocityMax;
				}
				else if (this.velocity.x < -xVelocityMax) {
					this.velocity.x = -xVelocityMax;
				}
			}
				 
		}
		
		protected void manageAutomaticStates(float delta, TiledMapTileLayer collisionLayer) {
			if (this.didChangeState) {
				this.getUiModel().setAnimationTime(0f);
				this.didChangeState = false; 
			}
			if (!isInAir && !walking && !this.isProcessingActiveSequences()) {
				setState(CharacterConstants.idleState);
			}
			else if (isInAir && this.velocity.y < -3f && (this.getCurrentActiveActionSeq() == null)) {
				setState(CharacterConstants.fallState);
			}
		}
		
		private void handleActionSequences(float delta) {
			if (this.processingActionSequences != null) {
				Iterator <ActionSequence> iterator = processingActionSequences.iterator();
				while (iterator.hasNext()) {
					ActionSequence actionSequence = iterator.next();
					actionSequence.process(delta, actionListener);
					if (actionSequence.isFinished()) {
						if (actionSequence.isStaggered()) {
							this.modelListener.endActionStagger();
						}
						else {
							this.queuedJump = false; //Attack missed, so end the queued jump
						}
						iterator.remove();
						if (this instanceof PlayerModel){
							PlayerModel model = (PlayerModel) this;
							if (model.getCurrentlyHeldDirection().equals(DirectionalInput.LEFT) || model.getCurrentlyHeldDirection().equals(DirectionalInput.RIGHT) && !iterator.hasNext())
								horizontalMove(model.getCurrentlyHeldDirection().equals(DirectionalInput.LEFT));
						}
//						this.fixedHurtBoxProperties = this.getCharacterProperties().defaultHurtboxProperties;
						this.updateHurtBoxProperties(this.getCharacterProperties().defaultHurtboxProperties);
					}
				}
			}
			ActionSequence nextActiveAction = nextActiveActionSequences.peek();
			if (nextActiveAction != null 
					&& (!isProcessingActiveSequences() 
					|| ((this.getCurrentActiveActionSeq().isActionChainableWithThis(nextActiveAction)) 
					&& !this.getCurrentActiveActionSeq().cannotBeOverriden()))) {
				if (this.isProcessingActiveSequences()) {
					this.forceEndForActiveAction();
				}
				this.endActionStagger();
				this.stopHorizontalMovement(true);
				ActionSequence nextSequence = nextActiveActionSequences.poll();
				this.processingActionSequences.add(nextSequence);
			}
		}
		
		public boolean isProcessingActiveSequences() {
			return this.getCurrentActiveActionSeq() != null;
		}
		
		public ActionSequence getCurrentActiveActionSeq() {
			for (ActionSequence actionSequence : this.processingActionSequences) {
				if (actionSequence.isActive()) {
					return actionSequence;
				}
			}
			return null;
		}
		
		public void addActionSequence (ActionSequence sequence) {
			if (sequence.cannotBeOverriden()) {
				if (sequence.shouldOverridePrevAction()) {
					forceEndForActiveAction(); 
					processingActionSequences.add(sequence);
				}
				else {
					nextActiveActionSequences.pollFirst();
					nextActiveActionSequences.addFirst(sequence);
				}
			}
			else if (sequence.isActive() && (!actionLocked || isAbleToEnqueueAction())) {
				ActionSequence nextAction = nextActiveActionSequences.peek();
				if (nextAction == null || !nextAction.cannotBeOverriden()) {
					nextActiveActionSequences.pollFirst();
					nextActiveActionSequences.addFirst(sequence);
				}
			}
			else if (!sequence.isActive()){
				processingActionSequences.add(sequence);
			}
		}
		
		public boolean isAbleToEnqueueAction() {
	    	return this.isProcessingActiveSequences();
	    }
		

		
		public void lockControls() {
			this.actionLocked = true;
		}
		
		public void shouldUnlockControls(ActionSequence action) {
	    	if (action.getAction().isFinished())
	    	{
	    		this.setActionLock(false);
	    	}
		}
		


		
		public void setMovementStatesIfNeeded() {
			this.setMovementStatesIfNeeded(false);
		}
		
		public void setMovementStatesIfNeeded(boolean overrideDuplicateState) {
			if (this.isInAir) {
				if (this.velocity.y > 0) {
					this.setState(CharacterConstants.jumpState, overrideDuplicateState);
				}
				else {
					this.setState(CharacterConstants.fallState);
				}
			}
			else if (this.sprinting) {
				this.setState(CharacterConstants.sprintState);
			}
			else if (this.walking) {
				this.setState(CharacterConstants.walkState);
			}
		}
			
		public boolean isTargetToLeft(CharacterModel target) {
			return this.gameplayCollisionBox.x > target.gameplayCollisionBox.x; 
		}
		
		public void jump() {
			if (!actionLocked || (this.getCurrentActiveActionSeq() != null && this.getCurrentActiveActionSeq().chainsWithJump())) {
				if (!this.actionStaggering && this.getCurrentActiveActionSeq() == null) {
					jumpCode();
				}
				else if (!this.actionStaggering && this.getCurrentActiveActionSeq() != null) {
					if (this.getCurrentActiveActionSeq().getAction().getActionState().equals(ActionState.COOLDOWN))
					{
						jumpCode();
					}
					else {
						this.queuedJump = true;
					}
				}
				else if (this.actionStaggering){
//					this.forceEndForActiveAction();
					this.queuedJump = true;
				}
			}
	    }
		
		public void downJump() {
			this.isRespectingOneWayCollision = false;
			this.timeToDisrespectOneWayCollision = 0.2f;
			
			this.onSlope = false;
		}
		
		private void jumpCode() {
			if (this.currentJumpTokens > 0) {
				this.forceEndForActiveAction();
				if (!isInAir)
					isInAir = true;
	            this.setWalking(false);
	            this.getVelocity().y = getJumpSpeed();
		    	this.setMovementStatesIfNeeded(true);
		    	this.currentJumpTokens -= 1;
			}

		}
	    
		public void horizontalMove(boolean left) {
			if (!this.actionLocked) {
				if (!isInAir) {
					this.setFacingLeft(left);
					this.setWalking(true);
					this.walkingTime = 0f;
				}

				setHorizontalSpeedForMovement(left);
				this.setMovementStatesIfNeeded();
				
			}
		}
		
		private void setHorizontalSpeedForMovement(boolean movingLeft) {
			if (this.sprinting) {
				this.velocity.x = movingLeft ? -this.properties.getSprintSpeed() : this.properties.getSprintSpeed();
			}
			else if (this.walking) {
				this.velocity.x = movingLeft ? -this.properties.getHorizontalSpeed() : this.properties.getHorizontalSpeed();
			}
			else if (this.isInAir) {
				this.acceleration.x = movingLeft ? -this.properties.getHorizontalAcceleration() : this.properties.getHorizontalAcceleration();
			}
		}
		
		public abstract void patrolWalk(boolean left);
		
		public void stopHorizontalMovement(boolean shouldSlow) {
			if (this.walking) {
				if (shouldSlow) {
					this.isSlowingDown = true;
					if (this.velocity.x > 0) {
						this.acceleration.x = -this.slowingAccel;
					}
					else {
						this.acceleration.x = this.slowingAccel;
					}
				}
				else {
					this.velocity.x = 0;
				}

				setState(CharacterConstants.idleState);
			}
			else if (this.isInAir) {
				this.acceleration.x = 0;
			}
			this.setWalking(false);
		}
		
		public float getJumpSpeed() {
			return this.properties.jumpSpeed;
		}
		
		public float getWalkSpeed() {
			return this.properties.horizontalSpeed;
		}
		
		public abstract Direction isTryingToMoveHorizontally();

		protected void movementWithCollisionDetection(float delta, TiledMapTileLayer collisionLayer) {
		//logic for collision detection
			if (this.actionStaggering) {
				return;
			}
			if (this.properties.boltedDown) {
				this.velocity.x = 0f;
				this.velocity.y = 0f;
				this.acceleration.x = 0f;
				this.acceleration.y = 0f;
				return;
			}
			
			if (this.walking) {
				this.walkingTime += delta;
			}
			else {
				this.walkingTime = 0f;
			}
			
			if (this.isSlowingDown && this.getXMove() == null) {
				if ((this.velocity.x > 0 && this.acceleration.x > 0) || (this.velocity.x < 0 && this.acceleration.x < 0)) {
					this.velocity.x = 0;
					this.acceleration.x = 0;
					this.isSlowingDown = false;
				}
			}
			CollisionCheck collisionX = this.checkForXCollision(delta, collisionLayer, this.velocity.x, this.acceleration.x, true);
			if (collisionX.doesCollide) {
				this.getVelocity().x = 0;
				this.getAcceleration().x = 0;
			}
			else if (!isTryingToMoveHorizontally().equals(Direction.NaN) && !this.actionLocked) {
				boolean left = isTryingToMoveHorizontally().equals(Direction.LEFT);
				this.setHorizontalSpeedForMovement(left);
				this.setMovementStatesIfNeeded();
			}

			CollisionCheck collisionY = this.checkForYCollision(delta, collisionLayer, this.velocity.y, true, true);
			if (collisionY.doesCollide) {
				if (this.getVelocity().y < 0 || collisionY.collisionType.equals(CollisionType.Slope)) {
					landed(collisionY.getCollisionType().equals(CollisionType.Entity));

				}
				if (!collisionY.getCollisionType().equals(CollisionType.Entity)) {
					if (collisionY.isVelocityPositive) {
						this.getVelocity().y = -5f;

					}
					else {
						this.getVelocity().y = 0;
					}
				}
			}
			else if (!isInAir && this.getVelocity().y < -100f) {
				this.falling();
			}

		}
		
		public float howLongTillYCollision(float maxTime, TiledMapTileLayer collisionLayer) {
			CollisionCheck collisionY = this.checkForYCollision(maxTime, collisionLayer, this.velocity.y, false, true);
			return collisionY.timeUntilCollision;
		}
		

		
		public float howLongTillXCollision(float maxTime, TiledMapTileLayer collisionLayer) {
			CollisionCheck collisionX = this.checkForXCollision(maxTime, collisionLayer, this.velocity.x, this.acceleration.x, false);
			return collisionX.timeUntilCollision;
		}
		
	    public void landed(boolean isFromEntityCollision) {
	    	if (this.injuredStaggering && this.isInAir && this.velocity.y < 0) {
	    		//do wakeup action.
	    		injuredStaggering = false;
	    		this.forceEndForActiveAction();
	    		this.isInAir = false;
	    		if (this.currentHealth != 0) {
		    		ActionSequence wakeupSeq = ActionSequence.createWakeupSequence(this, this.actionListener);
		    		this.addActionSequence(wakeupSeq);
	    		}
	    	}
	    	else if (this.isInAir) {
	    		if (!isFromEntityCollision)
	    		{
	    			this.forceEndForActiveAction();
	    			this.isInAir = false;
					this.acceleration.x = 0;
					this.velocity.x = 0;
	    		}

				if (!this.isTryingToMoveHorizontally().equals(Direction.NaN)) {
					boolean left = this.isTryingToMoveHorizontally().equals(Direction.LEFT);
					this.horizontalMove(left);
				}
				else {
					this.setMovementStatesIfNeeded();
				}
	    	}
    		if (!isFromEntityCollision) {
    			this.setCurrentJumpTokens(maxJumpTokens);
    			this.entityCollisionRepositionTokens = 1;

    		}
	    }
		
	    public void falling() {
	    	this.isInAir = true;
//	    	this.setMovementStatesIfNeeded();
	    }
		
		public void forceEndForActiveAction() {
			for (int i = 0; i < this.processingActionSequences.size(); i++) {
				ActionSequence sequence = this.processingActionSequences.get(i);
				if (sequence.isActive()) {
					sequence.forceEnd();
					break;
				}
			}
		}
		
		public void forceCooldownForActiveAction() {
			for (int i = 0; i < this.processingActionSequences.size(); i++) {
				ActionSequence sequence = this.processingActionSequences.get(i);
				if (sequence.isActive()) {
					sequence.forceCooldownState();
					break;
				}
			}
		}
		
		public void forceActiveForActiveAction() {
			for (int i = 0; i < this.processingActionSequences.size(); i++) {
				ActionSequence sequence = this.processingActionSequences.get(i);
				if (sequence.isActive()) {
					sequence.forceActiveState();
					break;
				}
			}
		}
		
		private void staggerAction(YMovementEffectSettings yPotentialMovementSettings) {
			this.forceEndForActiveAction();
			this.injuredStaggering = true;
			StaggerType staggerType = this.isInAir() || (yPotentialMovementSettings != null && yPotentialMovementSettings.getVelocity() > 0 && !yPotentialMovementSettings.onlyWhenInAir()) ? StaggerType.Aerial : StaggerType.Normal;
			ActionSequence staggerAction = ActionSequence.createStaggerSequence(this, this.actionListener, staggerType);
    		this.addActionSequence(staggerAction);
    		this.actionStagger(true);
    		if (!this.isInAir && (yPotentialMovementSettings == null || (yPotentialMovementSettings.getVelocity() <= 0 && yPotentialMovementSettings.getAcceleration() <= 0))) {
        		this.setCurrentStability(this.maxStability, null);
    		}
    		else {
        		this.setCurrentStability(1, null);

    		}
		}
		
		public void shouldProjectileHit(Projectile projectile) {
			if (!isImmuneToInjury()) {
				boolean didInterceptAttack = false;
				for (EntityEffect effect : this.currentEffects) {
					if (effect instanceof AssaultInterceptor) {
						didInterceptAttack = ((AssaultInterceptor) effect).didInterceptProjectile(this, projectile);
						break;
					}
				}
				if (!didInterceptAttack) {
					projectile.processHit(this);
				}
			}
		}
		
		public void shouldExplosionHit(Explosion explosion) {
			if (!isImmuneToInjury) {
				boolean didInterceptAttack = false;
				for (EntityEffect effect : this.currentEffects) {
					if (effect instanceof AssaultInterceptor) {
						didInterceptAttack = ((AssaultInterceptor) effect).didInterceptExplosion(this, explosion);
						break;
					}
				}
				if (!didInterceptAttack) {
					explosion.processExplosionHit(this);
				}
			}
		}
		
		public void shouldAttackHit(Attack attack) {
			if (!isImmuneToInjury()) {
				boolean didInterceptAttack = false;
				for (EntityEffect effect : this.currentEffects) {
					if (effect instanceof AssaultInterceptor) {
						didInterceptAttack = ((AssaultInterceptor) effect).didInterceptAttack(this, attack);
					}
				}
				if (!didInterceptAttack) {
					attack.processAttackOnEntity(this);
				}
			}
		}
		
		public void performDeathSequence() {
			ActionSequence deathSequence = this.getCharacterProperties().getActions().get(ActionSequence.deathKey).cloneSequenceWithSourceAndTarget(this, null, actionListener, collisionChecker);
			if (deathSequence != null) {
				this.addActionSequence(deathSequence);
			}

		}
		
		public void deathCheck() {
			if (this.getCurrentHealth() == 0 && !alreadyDead) {
				this.forceEndForActiveAction();
				this.performDeathSequence();
				this.alreadyDead = true;
			}
		}
		
		public float getHealthRatio() {
			return ((float)this.getCurrentHealth()) / ((float)this.getMaxHealth());
		}
		
		public float getTensionRatio() {
			return ((float)this.getCurrentTension()) / ((float)this.getMaxTension());
		}
	
		public void addToCurrentHealth(float value) {
			this.setCurrentHealth(value + this.currentHealth);
		}
		
		public void removeFromCurrentHealth(float value) {
			this.setCurrentHealth(this.currentHealth - value);
		}
		
		public void addToCurrentWill(float value) {
			this.setCurrentWill(value + this.currentWill);
		}
		
		public void removeFromCurrentWill(float value) {
			this.setCurrentWill(this.currentWill - value);
		}
		
		public void removeFromCurrentStability(float value, YMovementEffectSettings yReplacementMovement) {
			this.setCurrentStability(this.currentStability - value, yReplacementMovement);
		}
		
		public void addToCurrentStability(float value) {
			this.setCurrentStability(value + this.currentStability, null);
		}
		
		public void addToCurrentTension(float value) {
			this.setCurrentTension(this.currentTension + value);
		}
		
		public void removeFromCurrentTension(float value) {
			this.setCurrentTension(this.currentTension - value);
		}
		
		public Vector2 getCenteredPosition() {
			Vector2 sourcePosition = new Vector2(this.gameplayCollisionBox.x + this.gameplayCollisionBox.width / 2, this.gameplayCollisionBox.y + this.gameplayCollisionBox.height / 2); 
			return sourcePosition;
		}
		
		public void setTurnAngle(float turnAngle) {
			if (turnAngle <= -80f) {
				this.turnAngle = TurnAngle.NEGNINETY;
			}
			else if (turnAngle > -80f && turnAngle <= -30f) {
				this.turnAngle = TurnAngle.NEGFOURTYFIVE;
			}
			else if (turnAngle > -30f && turnAngle <= 30f) {
				this.turnAngle = TurnAngle.ZERO;
			}
			else if (turnAngle > 30f && turnAngle <= 80f) {
				this.turnAngle = TurnAngle.FOURTYFIVE;
			}
			else if (turnAngle > 80f){
				this.turnAngle = TurnAngle.NINETY;
			}
		}
		
		public float convertTurnAngleToFloat() {
			switch (this.turnAngle) {
			case NEGNINETY:
				return -90f;
			case NEGFOURTYFIVE:
				return -45f;
			case ZERO:
				return 0f;
			case FOURTYFIVE: 
				return 45f;
			case NINETY: 
				return 90f;
			}
			return 0f;
		}
		
		public void endActionStagger() {
			if (actionStaggering) {
//				if (this.getCurrentMovement() == null) {
					this.velocity.x = tempVelocityX;
					this.velocity.y = tempVelocityY;
//				}
				this.actionStaggering = false;
				this.actionStaggerTime = 0f;
				if (this.queuedJump) {
					this.jumpCode();
					this.queuedJump = false;
				}
			}

		}
		
		public abstract void setPatrolInfo(Array<Float> wayPoints, float patrolDuration, float breakDuration);
		
		public void actionStagger(boolean stabilityStaggering) {
			this.actionStaggering = true;
			this.tempVelocityX = this.velocity.x;
			this.tempVelocityY = this.velocity.y;
			this.velocity.x = 0f;
			this.velocity.y = 0f;
			ActionSequence currentSeq = this.getCurrentActiveActionSeq();
			if (currentSeq != null) {
				currentSeq.stagger();
			}
				
			for (EntityEffect effect : this.currentEffects) {
				if (effect instanceof XMovementEffect || effect instanceof YMovementEffect)
					effect.stagger();
			}
				
			this.modelListener.actionStagger();
		}

		
		public void setWalking(boolean walking) {
			if (walking) {
				this.walking = true;
			}
			else {
				this.walking = false;
				sprinting = false;
			}
		}
		
		public void refreshHurtBoxesX() {
			for (int i = 0; i < this.gameplayHurtBoxes.size; i++) {
				Rectangle hurtBox = gameplayHurtBoxes.get(i);
				if (i < this.fixedHurtBoxProperties.size) {
					hurtBox.x = this.gameplayCollisionBox.x + this.fixedHurtBoxProperties.get(i).x;
					hurtBox.width = this.fixedHurtBoxProperties.get(i).width;
				}
				else {
//					this.gameplayHurtBoxes.removeIndex(i);
					break;
				}
			}
		}
		
		public void refreshHurtBoxesY() {
			for (int i = 0; i < this.gameplayHurtBoxes.size; i++) {
				Rectangle hurtBox = gameplayHurtBoxes.get(i);
				if (i < this.fixedHurtBoxProperties.size) {
					hurtBox.y = this.gameplayCollisionBox.y + this.fixedHurtBoxProperties.get(i).y;
					hurtBox.height = this.fixedHurtBoxProperties.get(i).height;
				}
				else {
//					this.gameplayHurtBoxes.removeIndex(i);
					break;
				}
			}
		}
 		
		//-------------GETTERS/SETTERS------------//
		
		public String getState() {
			return state;
		}

		public boolean isAlreadyDead() {
			return alreadyDead;
		}

		public boolean isActionStaggering() {
			return actionStaggering;
		}

		public float getCurrentStability() {
			return currentStability;
		}

		public ArrayDeque<ActionSequence> getNextActiveActionSequences() {
			return nextActiveActionSequences;
		}

		public void setCurrentStability(float currentStability, YMovementEffectSettings yPotentialMovement) {
			float realStability = Math.max(0, currentStability);
			this.currentStability = Math.min(realStability, this.maxStability);
			if (realStability == 0 && !alreadyDead && !this.getCharacterProperties().boltedDown) {
				staggerAction(yPotentialMovement);
			}
			else if (!alreadyDead){
				//only action stagger.
	    		this.actionStagger(true);
			}
		}

		public float getMaxStability() {
			return maxStability;
		}

		public void setMaxStability(float maxStability) {
			this.maxStability = maxStability;
		}

		public ArrayList<ActionSequence> getProcessingActionSequences() {
			return processingActionSequences;
		}

		public boolean isActionLock() {
			return actionLocked;
		}

		public void setActionLock(boolean actionLock) {
			this.actionLocked = actionLock;
		}

		public float getGameplayHitBoxWidthModifier() {
			return widthCoefficient;
		}

		public float getGameplayHitBoxHeightModifier() {
			return heightCoefficient;
		}
		

		public String getUuid() {
			return uuid;
		}

		public ObjectListener getObjectListener() {
			return objectListener;
		}

		public void setObjectListener(ObjectListener objectListener) {
			this.objectListener = objectListener;
		}

		public ActionListener getAttackListener() {
			return actionListener;
		}

		public void setActionListener(ActionListener attackListener) {
			this.actionListener = attackListener;
		}
		
		public void setState(String state) {
			this.setState(state, false);
		}

		public void setState(String state, boolean overrideDuplicate) {
			if (this.state == null || (!this.state.equals(state))) {
				this.state = state;
				this.didChangeState = true;
				this.stateTime = 0f;
			}
			else if (overrideDuplicate) {
				this.state = state;
				this.didChangeState = true;
				this.stateTime = 0f;
			}
		}

		public float getCurrentHealth() {
			return currentHealth;
		}

		public void setCurrentHealth(float currentHealth) {
			this.currentHealth = Math.min(Math.max(0, currentHealth), this.maxHealth);
		}

		public float getMaxHealth() {
			return maxHealth;
		}

		public void setMaxHealth(float maxHealth) {
			this.maxHealth = maxHealth;
			this.currentHealth = maxHealth;
		}

		public float getCurrentWill() {
			return currentWill;
		}

		public void setCurrentWill(float currentWill) {
			this.currentWill = Math.min(Math.max(0, currentWill), this.maxWill);
		}

		public float getMaxWill() {
			return maxWill;
		}

		public void setMaxWill(float maxWill) {
			this.maxWill = maxWill;
			this.currentWill = maxWill;
		}

		public int getCurrentJumpTokens() {
			return currentJumpTokens;
		}

		public void setCurrentJumpTokens(int currentJumpTokens) {
			this.currentJumpTokens = Math.min(Math.max(0, currentJumpTokens), this.maxJumpTokens);
		}

		public int getMaxJumpTokens() {
			return maxJumpTokens;
		}

		public void setMaxJumpTokens(int maxJumpTokens) {
			this.maxJumpTokens = maxJumpTokens;
			this.currentJumpTokens = maxJumpTokens;
		}

		public float getAttack() {
			return attack;
		}

		public void setAttack(float attack) {
			this.attack = attack;
		}

		public String getName() {
			return name;
		}
		
		public String getNameForWakeupSeq() {
			return this.properties.useDefaultWakeup ? "" : this.getName();
		}
		
		public String getNameForStaggerSeq(StaggerType type) {
			if (type.equals(StaggerType.Normal)) {
				return this.properties.useDefaultStagger ? "" : this.getName();
			}
			else if (type.equals(StaggerType.Aerial)) {
				return this.properties.useDefaultAerialStagger ? "" : this.getName();
			}
			else if (type.equals(StaggerType.Tension)) {
				return this.properties.useDefaultTensionStagger ? "" : this.getName();

			}
			return "";
		}

		public void setName(String name) {
			this.name = name;
		}

		public Vector2 getVelocity() {
			return velocity;
		}

		public Vector2 getAcceleration() {
			return acceleration;
		}

		public Rectangle getGameplayCollisionBox() {
			return gameplayCollisionBox;
		}

		public Rectangle getImageHitBox() {
			return imageBounds;
		}
		
		public boolean isImmuneToInjury() {
			return isImmuneToInjury;
		}

		public void setImmuneToInjury(boolean isImmuneToInjury) {
			this.isImmuneToInjury = isImmuneToInjury;
		}
		
		public ActionListener getActionListener() {
			return actionListener;
		}

		public boolean isAttacking() {
			return attacking;
		}

		public void setAttacking(boolean attacking) {
			this.attacking = attacking;
		}

		public boolean isInAir() {
			return isInAir;
		}

		public void setIsInAir(boolean isInAir) {
			this.isInAir = isInAir;
		}

		public boolean isFacingLeft() {
			return facingLeft;
		}

		public void setFacingLeft(boolean facingLeft) {
			this.facingLeft = facingLeft;
		}

		public EntityUIModel getUiModel() {
			return uiModel;
		}

		public CharacterProperties getCharacterProperties() {
			return properties;
		}

		public Weapon getCurrentWeapon() {
			return currentWeapon;
		}

		public float getCurrentTension() {
			return currentTension;
		}

		public void setCurrentTension(float currentTension) {
			this.currentTension = Math.min(Math.max(0, currentTension), this.maxTension);
			if (this.currentTension == this.maxTension) {
				this.tensionOverload();
			}
		}
		
		public abstract void tensionOverload();

		public float getMaxTension() {
			return maxTension;
		}

		public void setMaxTension(float maxTension) {
			this.maxTension = maxTension;
			this.currentTension = 0;
		}



		public boolean isSprinting() {
			return sprinting;
		}

		public boolean isInjuredStaggering() {
			return injuredStaggering;
		}
		
		public float getXRotationCoefficient() {
			return this.getCharacterProperties().xRotationCoefficient;
		}
		
		public float getYRotationCoefficient() {
			return this.getCharacterProperties().yRotationCoefficient;
		}
	}
	
	@Override 
	public boolean equals(Object object) {
		if (object instanceof Character) {
			return ((Character)object).getCharacterData().getUuid().equals(this.getCharacterData().getUuid());
		}
		return super.equals(object);
	}
	
	public CharacterModel getCharacterData() {
		return characterData;
	}

	public EntityUIModel getCharacterUIData() {
		return characterUIData;
	}

	public void setCharacterUIData(EntityUIModel characterUIData) {
		this.characterUIData = characterUIData;
	}

	public void setCharacterData(CharacterModel characterData) {
		this.characterData = characterData;
	}
}
