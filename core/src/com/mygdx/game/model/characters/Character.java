package com.mygdx.game.model.characters;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.actions.Attack;
import com.mygdx.game.model.characters.CollisionCheck.CollisionType;
import com.mygdx.game.model.actions.ActionSegment.ActionState;
import com.mygdx.game.model.actions.ActionSequence.ActionType;
import com.mygdx.game.model.actions.ActionSequence.StaggerType;
import com.mygdx.game.model.effects.EntityEffect;
import com.mygdx.game.model.effects.EntityEffectSettings;
import com.mygdx.game.model.effects.MovementEffect;
import com.mygdx.game.model.effects.MovementEffectSettings;
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
	
	//=============================================================//
	//----------------------------MODEL----------------------------//
	//=============================================================//

	public abstract class CharacterModel extends EntityModel{
		
		public final String idleState = "Idle";
		public final String walkState = "Walk";
		public final String backWalkState = "Backwalk";
		public final String jumpState = "Jump";
		public final String fallState = "Fall";
		
		String state;
		float currentHealth, maxHealth, currentWill, maxWill, attack, currentStability, maxStability, currentTension, maxTension;
		boolean isImmuneToInjury, attacking, directionLock;
		protected boolean jumping;
		protected boolean walking;
		boolean didChangeState;
		boolean actionStaggering;
		boolean facingLeft;
		boolean actionLocked;
	    public float injuryTime = 0f;
	    float actionStaggerTime;
	    float tempVelocityX;
	    float tempVelocityY;
		String name, uuid; 
		Direction lockedFacingDirection;
		
		
//		public float gameplayHitBoxWidthModifier;
//		public float gameplayHitBoxHeightModifier;
		ActionListener actionListener;
		ObjectListener objectListener;
		ModelListener modelListener;
		
//		public Vector2 velocity, acceleration;
//		public Rectangle gameplayHitBox;
//		public Rectangle imageHitBox;
//		boolean isProcessingMovementEffect;
		EntityUIModel uiModel;
		CharacterProperties properties;
		ArrayList <EntityEffect> currentEffects;
		ArrayList <Integer> indicesToRemove;
		ArrayList <ActionSequence> processingActionSequences;
		ArrayDeque <ActionSequence> nextActiveActionSequences;
		Array <Weapon> weapons;
		Weapon currentWeapon;
		
		//Debug
		float stateTime;
		
		public CharacterModel(String characterName, EntityUIModel uiModel, ModelListener modelListener) {
			this.currentEffects = new ArrayList <EntityEffect>();
			this.indicesToRemove = new ArrayList<Integer>();
			this.nextActiveActionSequences = new ArrayDeque<ActionSequence>();
			this.processingActionSequences = new ArrayList <ActionSequence>();
			setState(idleState);
			isImmuneToInjury = false;
			attacking = false;
			jumping = true;
			facingLeft = false;
			lockedFacingDirection = Direction.RIGHT;
			actionLocked = false;
			walking = false;
			directionLock = false;
			stateTime = 0f;
			actionStaggerTime = 0f;
			tempVelocityX = 0f;
			tempVelocityY = 0f;
			this.modelListener = modelListener;

			UUID id = UUID.randomUUID();
			this.uuid = id.toString();
			this.name = characterName;
			this.uiModel = uiModel;
			this.properties = JSONController.loadCharacterProperties(characterName);
			this.properties.setSource(this);
			this.widthCoefficient = this.properties.getWidthCoefficient();
			this.heightCoefficient = this.properties.getHeightCoefficient();
			setMaxHealth(properties.getMaxHealth());
			setCurrentHealth(properties.getMaxHealth());
			setMaxWill(properties.getMaxWill());
			setCurrentWill(properties.getMaxWill());
			setAttack(properties.getAttack());
			setMaxStability(properties.getMaxStability());
			setCurrentStability(properties.getMaxStability(), null);
			setMaxTension(properties.getMaxTension());
			setCurrentTension(0);
			acceleration.y = -properties.getGravity();
			weapons = new Array <Weapon> ();
			for (StringWrapper key : this.getCharacterProperties().getWeaponKeys()) {
				weapons.add(new Weapon(key.value, characterName));
			}
			if (weapons.size > 0) {
				currentWeapon = weapons.get(0);
			}
		}
		
		public void update(float delta, TiledMapTileLayer collisionLayer) {
			this.handleOverlapCooldown(delta);
			this.setGameplaySize(delta, collisionLayer);
			this.movementWithCollisionDetection(delta, collisionLayer);
			this.manageAutomaticStates(delta, collisionLayer);
			this.handleEffects(delta);
			this.handleActionSequences(delta);
			if (this.actionStaggering) {
				this.actionStaggerTime += delta;
				if (this.actionStaggerTime > EntityUIModel.standardStaggerDuration) {
					if (this.getCurrentMovement() == null) {
						this.velocity.x = tempVelocityX;
						this.velocity.y = tempVelocityY;
					}
					this.actionStaggering = false;
					this.actionStaggerTime = 0f;
				}
			}
			//Debug
			this.stateTime += delta;
		}
		
		public void setGameplaySize(float delta, TiledMapTileLayer collisionLayer) {

			
			super.setGameplaySize(delta);
			//clamp velocity
			if (this.getVelocity().y > this.properties.jumpSpeed)
				this.getVelocity().y = this.properties.jumpSpeed;
			else if (this.getVelocity().y < -this.properties.jumpSpeed) {
				this.getVelocity().y = -this.properties.jumpSpeed; 
			}

			if (this.getCurrentMovement() != null)
			{
				float xVelocityMax = this.getCurrentMovement().getMaxVelocity().x;
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
			if (this.isImmuneToInjury()) {
				injuryTime += delta;
			}
			
			if (this.injuryTime > this.properties.getInjuryImmunityTime()) {
				this.setImmuneToInjury(false);
			}
			if (!jumping && !walking && !this.isProcessingActiveSequences()) {
				setState(idleState);
			}
			else if (jumping && this.velocity.y <= 0 && this.state.equals(jumpState)) {
				setState(fallState);
			}
		}
		
		private void handleActionSequences(float delta) {
			if (this.processingActionSequences != null) {
				Iterator <ActionSequence> iterator = processingActionSequences.iterator();
				while (iterator.hasNext()) {
					ActionSequence actionSequence = iterator.next();
					actionSequence.process(delta, actionListener);
					if (actionSequence.isFinished()) {
						iterator.remove();
					}
				}

			}
			ActionSequence nextActiveAction = nextActiveActionSequences.peek();
			if (nextActiveAction != null 
					&& (!isProcessingActiveSequences() 
					|| (((this.getCurrentActiveActionSeq().isActionChainableWithThis(nextActiveAction) && this.actionStaggering)) 
					&& !this.getCurrentActiveActionSeq().cannotBeOverriden()))) {
				if (this.isProcessingActiveSequences()) {
					this.forceEndForActiveAction();
				}
				if (nextActiveAction.getActionKey().getKey().equals("Attack2") || nextActiveAction.getActionKey().getTypeOfAction().equals(ActionType.Stagger)) {
					System.out.println("");
				}
				this.processingActionSequences.add(nextActiveActionSequences.poll());
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
		
		private void handleEffects(float delta) {
			this.indicesToRemove.clear();
			for(Iterator<EntityEffect> iterator = this.currentEffects.iterator(); iterator.hasNext();) {
				EntityEffect effect = iterator.next();
				boolean isFinished = effect.process(this, delta);
				if (isFinished) {
					iterator.remove();
				}
			}
		}
		
		public void addEffect(EntityEffect effect) {
			if (effect.isUniqueEffect()) {
				for (EntityEffect currentEffect : this.currentEffects) {
					if (currentEffect.getClass().equals(effect.getClass())) {
						currentEffect.setForceEnd(true);
					}
				}
			}
			currentEffects.add(effect);
		}
		
		public void removeEffectByID(Integer effectID) {
			if (effectID.intValue() != EntityEffectSettings.defaultID) {
				for(Iterator<EntityEffect> iterator = this.currentEffects.iterator(); iterator.hasNext();) {
					EntityEffect effect = iterator.next();
					if (effect.getSpecificID().intValue() == effectID.intValue() && effect.getSpecificID().intValue() != EntityEffectSettings.defaultID) {
						effect.setForceEnd(true);
						return;
					}
				}
			}
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
		
		public MovementEffect getCurrentMovement() {
			for (EntityEffect effect : this.currentEffects) {
				if (effect instanceof MovementEffect) {
					return (MovementEffect) effect;
				}
			}
			return null;
		}
		
		public boolean checkIfIntercepted(Attack attack) {
			boolean interceptedAttack = false;
			for (EntityEffect effect : this.currentEffects) {
				if (effect instanceof AssaultInterceptor) {
					AssaultInterceptor attackInterceptor = (AssaultInterceptor) effect;
					interceptedAttack = attackInterceptor.didInterceptAttack(this, attack);
				}
			}
			return interceptedAttack;
		}
		
		protected void setMovementStatesIfNeeded() {
			if (this.jumping) {
				if (this.velocity.y > 0) {
					this.setState(jumpState);
				}
				else {
					this.setState(fallState);
				}
			}
			else if (this.walking) {
				if (this.directionLock) {
					if (this.velocity.x >= 0 && this.lockedFacingDirection.equals(Direction.LEFT)) {
		    			this.setState(backWalkState);
		    		}
		    		else if (this.velocity.x < 0 && this.lockedFacingDirection.equals(Direction.LEFT)) {
		    			this.setState(walkState);
		    		}
		    		else if (this.velocity.x >= 0 && this.lockedFacingDirection.equals(Direction.RIGHT)) {
		    			this.setState(walkState);
		    		}
		    		else if (this.velocity.x < 0 && this.lockedFacingDirection.equals(Direction.RIGHT)) {
		    			this.setState(backWalkState);
		    		}
				}
				else {
					this.setState(walkState);
				}
			}
			
		}
		
	    public void lockDirection() {
	    	this.directionLock = true;
	    	this.lockedFacingDirection = this.isFacingLeft() ? Direction.LEFT : Direction.RIGHT;
	    	//change walk if necessary
	    	this.setMovementStatesIfNeeded();
	    }
	    
	    public void unlockDirection() {
	    	this.directionLock = false;
	    	if (this.lockedFacingDirection.equals(Direction.LEFT) && this.velocity.x > 0) {
		    	this.setFacingLeft(false);
	    	}
	    	else if (this.lockedFacingDirection.equals(Direction.RIGHT) && this.velocity.x < 0) {
		    	this.setFacingLeft(true);
	    	}
	    	this.lockedFacingDirection = Direction.NaN; 
	    	//change walk if necessary
	    	this.setMovementStatesIfNeeded();
	    }
		
		public boolean isTargetToLeft(CharacterModel target) {
			return this.gameplayHitBox.x > target.gameplayHitBox.x; 
		}
		
		public void jump() {
	        if (!jumping && !actionLocked) {
	            jumping = true;
	            walking = false;
	            this.getVelocity().y = getJumpSpeed();
		    	this.setMovementStatesIfNeeded();
	        }
	    }
		
	    public void stopJump() {
	    	if (jumping && this.getVelocity().y >= 0 && !this.actionLocked) {
	    		this.getVelocity().y = 0;
		    	this.setMovementStatesIfNeeded();
	    	}
	    }
	    
		public void horizontalMove(boolean left) {
			if (!this.actionLocked) {
				this.setFacingLeft(left);
				if (!jumping) {
					this.walking = true;
				}

				setHorizontalSpeedForMovement(left);

				this.setMovementStatesIfNeeded();
			}
		}
		
		public void setHorizontalSpeedForMovement(boolean movingLeft) {
			if (this.walking) {
				this.velocity.x = movingLeft ? -this.properties.getHorizontalSpeed() : this.properties.getHorizontalSpeed();
			}
			else if (this.jumping) {
				this.acceleration.x = movingLeft ? -this.properties.getHorizontalAcceleration() : this.properties.getHorizontalAcceleration();
			}
		}
		
		public abstract void patrolWalk(boolean left);
		
		public void stopHorizontalMovement() {
			if (this.walking) {
				this.velocity.x = 0;
				setState(this.idleState);
			}
			else if (this.jumping) {
				this.acceleration.x = 0;
			}
			this.walking = false;
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
			CollisionCheck collisionX = this.checkForXCollision(delta, collisionLayer, this.velocity.x, this.acceleration.x, true);
			if (collisionX.doesCollide) {
				this.getVelocity().x = 0;
				this.getAcceleration().x = 0;
			}
			else if (!isTryingToMoveHorizontally().equals(Direction.NaN)) {
				boolean left = isTryingToMoveHorizontally().equals(Direction.LEFT);
				this.setHorizontalSpeedForMovement(left);
			}

			CollisionCheck collisionY = this.checkForYCollision(delta, collisionLayer, this.velocity.y, true, true);
			if (collisionY.doesCollide) {
				if (this.getVelocity().y < 0) {
					landed(collisionY.getCollisionType().equals(CollisionType.Entity));
				}
				if (!collisionY.getCollisionType().equals(CollisionType.Entity))
					this.getVelocity().y = 0;
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
		
		public boolean isHitBoxModified() {
			return super.isHitBoxModified() 
					|| this.widthCoefficient != this.getCharacterProperties().getWidthCoefficient()
					|| this.heightCoefficient != this.getCharacterProperties().getHeightCoefficient();
		}
		
	    public void landed(boolean isFromEntityCollision) {
	    	if (this.jumping) {
	    		if (!isFromEntityCollision)
	    		{
	    			this.forceEndForActiveAction();
	    			this.jumping = false;
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
		
		private void staggerAction(MovementEffectSettings potentialMovementSettings) {
			this.forceEndForActiveAction();
			ActionSequence staggerAction = ActionSequence.createStaggerSequence(this, potentialMovementSettings, this.actionListener, StaggerType.Normal);
    		this.addActionSequence(staggerAction);
    		this.actionStagger(true);
    		this.setCurrentStability(this.maxStability, null);

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
					attack.processAttackOnCharacter(this);
				}
			}
		}
		
		public float getHealthRatio() {
			return ((float)this.getCurrentHealth()) / ((float)this.getMaxHealth());
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
		
		public void removeFromCurrentStability(float value, MovementEffectSettings replacementMovement) {
			this.setCurrentStability(this.currentStability - value, replacementMovement);
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
			Vector2 sourcePosition = new Vector2(this.gameplayHitBox.x + this.gameplayHitBox.width / 2, this.gameplayHitBox.y + this.gameplayHitBox.height / 2); 
			return sourcePosition;
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
				if (effect instanceof MovementEffect)
					effect.stagger();
			}
			
			this.modelListener.actionStagger();
		}
		
		//-------------GETTERS/SETTERS------------//
		
		public String getState() {
			return state;
		}

		public boolean isActionStaggering() {
			return actionStaggering;
		}

		public boolean isLockDirection() {
			return directionLock;
		}

		public float getCurrentStability() {
			return currentStability;
		}

		public ArrayDeque<ActionSequence> getNextActiveActionSequences() {
			return nextActiveActionSequences;
		}

		public void setCurrentStability(float currentStability, MovementEffectSettings potentialMovement) {
			float realStability = Math.max(0, currentStability);
			this.currentStability = Math.min(realStability, this.maxStability);
			if (realStability == 0) {
				staggerAction(potentialMovement);
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
			if (this.state == null || !this.state.equals(state)) {
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

		public float getAttack() {
			return attack;
		}

		public void setAttack(float attack) {
			this.attack = attack;
		}

		public String getName() {
			return name;
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

		public Rectangle getGameplayHitBox() {
			return gameplayHitBox;
		}

		public Rectangle getImageHitBox() {
			return imageHitBox;
		}
		
		public boolean isImmuneToInjury() {
			return isImmuneToInjury;
		}

		public void setImmuneToInjury(boolean isImmuneToInjury) {
			this.isImmuneToInjury = isImmuneToInjury;
			injuryTime = 0f;
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

		public boolean isJumping() {
			return jumping;
		}

		public void setJumping(boolean jumping) {
			this.jumping = jumping;
		}

		public boolean isFacingLeft() {
			return facingLeft;
		}

		public void setFacingLeft(boolean facingLeft) {
			if (!this.directionLock) {
				this.facingLeft = facingLeft;
			}
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

		public ArrayList<EntityEffect> getCurrentEffects() {
			return currentEffects;
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
