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
import com.mygdx.game.model.effects.EntityEffect;
import com.mygdx.game.model.effects.MovementEffect;
import com.mygdx.game.model.effects.MovementEffectSettings;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.AssaultInterceptor;
import com.mygdx.game.model.events.ObjectListener;
import com.mygdx.game.model.projectiles.Explosion;
import com.mygdx.game.model.projectiles.Projectile;

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
		float currentHealth, maxHealth, currentWill, maxWill, attack, currentStability, maxStability;
		boolean isImmuneToInjury, attacking, directionLock;
		protected boolean jumping;
		protected boolean walking;
		boolean didChangeState;
		boolean stabilityStaggering;
		boolean actionStaggering;
		boolean facingLeft;
		boolean actionLocked;
	    public float injuryTime = 0f;
	    float staggerTime;
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
			stabilityStaggering = false;
			jumping = true;
			facingLeft = false;
			lockedFacingDirection = Direction.RIGHT;
			actionLocked = false;
			walking = false;
			directionLock = false;
//			isProcessingMovementEffect = false;
			stateTime = 0f;
			staggerTime = 0f;
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
			acceleration.y = -properties.getGravity();
		}
		
		public void update(float delta, TiledMapTileLayer collisionLayer) {
			this.handleOverlapCooldown(delta);
			this.setGameplaySize(delta, collisionLayer);
			this.movementWithCollisionDetection(delta, collisionLayer);
			this.manageAutomaticStates(delta, collisionLayer);
			this.handleEffects(delta);
			this.handleActionSequences(delta);
//			System.out.println(imageHitBox.x + " " + imageHitBox.y + " " + imageHitBox.width + " " + imageHitBox.height);
			
			//Debug
			this.stateTime += delta;
		}
		
		public void setGameplaySize(float delta, TiledMapTileLayer collisionLayer) {
//			this.getVelocity().y += this.getAcceleration().y * delta;
//			this.getVelocity().x += this.getAcceleration().x * delta;
//			
//			this.gameplayHitBox.width = this.getImageHitBox().width * gameplayHitBoxWidthModifier;
//			this.gameplayHitBox.height = this.getImageHitBox().height * gameplayHitBoxHeightModifier;
			
			super.setGameplaySize(delta);
			//clamp velocity
			if (this.getVelocity().y > this.properties.jumpSpeed)
				this.getVelocity().y = this.properties.jumpSpeed;
//			else if (this.getVelocity().y < -this.properties.jumpSpeed)
//				this.getVelocity().y = -this.properties.jumpSpeed;
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
//			if (jumping) {
//				setState(velocity.y >= 0 ? idleState : idleState); //Jump : Fall
//			}
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
			if (nextActiveActionSequences.peek() != null && !isProcessingActiveSequences()) {
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
//			sequence.setSource(this);
			if (sequence.isActive() && !actionLocked) {
				forceEndForActiveAction(); //necessary? No action should be there if not actionLocked.
				nextActiveActionSequences.add(sequence);
			}
			else if (sequence.isActive() && isAbleToEnqueueAction()) {
				nextActiveActionSequences.add(sequence);
			}
			else if (!sequence.isActive()){
				processingActionSequences.add(sequence);
			}
		}
		
		public boolean isAbleToEnqueueAction() {
			return !this.isProcessingActiveSequences();
		}
		
		private void handleEffects(float delta) {
			this.indicesToRemove.clear();
//			//process existing effects.
			for(Iterator<EntityEffect> iterator = this.currentEffects.iterator(); iterator.hasNext();) {
				EntityEffect effect = iterator.next();
				boolean isFinished = effect.process(this, delta);
				if (isFinished) {
					iterator.remove();
				}
			}
		}
		
		public void addEffect(EntityEffect effect) {
			currentEffects.add(effect);
		}
		
		public void lockControls() {
			this.actionLocked = true;
		}
		
		public void shouldUnlockControls(ActionSequence action) {
			if (action.isFinished()) {
				this.actionLocked = false;
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
	    	if (jumping && this.getVelocity().y >= 0) {
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
			CollisionCheck collisionX = this.checkForXCollision(delta, collisionLayer, this.velocity.x, true);
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
					landed();
				}
				this.getVelocity().y = 0;
			}

		}
		
		public float howLongTillYCollision(float maxTime, TiledMapTileLayer collisionLayer) {
			CollisionCheck collisionY = this.checkForYCollision(maxTime, collisionLayer, this.velocity.y, false, true);
			return collisionY.timeUntilCollision;
		}
		

		
		public float howLongTillXCollision(float maxTime, TiledMapTileLayer collisionLayer) {
			CollisionCheck collisionX = this.checkForXCollision(maxTime, collisionLayer, this.velocity.x, false);
			return collisionX.timeUntilCollision;
		}
		
	    public void landed() {
	    	if (this.jumping) {
	    		this.forceEndForActiveAction();
				this.jumping = false;
				this.acceleration.x = 0;
				this.velocity.x = 0;
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
		
		private void staggerAction(MovementEffectSettings potentialMovementSettings) {
			this.forceEndForActiveAction();
//    		ActionSequence staggerAction = this.getCharacterProperties().getActions().get("Stagger").cloneSequenceWithSourceAndTarget(this, null, this.getActionListener(), this.getCollisionChecker());
			ActionSequence staggerAction = ActionSequence.createStaggerSequence(this, potentialMovementSettings, this.actionListener);
    		this.addActionSequence(staggerAction);
    		this.actionStagger();
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
		
		public Vector2 getCenteredPosition() {
			Vector2 sourcePosition = new Vector2(this.gameplayHitBox.x + this.gameplayHitBox.width / 2, this.gameplayHitBox.y + this.gameplayHitBox.height / 2); 
			return sourcePosition;
		}
		
		public abstract void setPatrolInfo(Array<Float> wayPoints, float patrolDuration, float breakDuration);
		
		public void actionStagger() {
			this.actionStaggering = true;
			ActionSequence currentSeq = this.getCurrentActiveActionSeq();
			if (currentSeq != null) {
				currentSeq.stagger();
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
//			System.out.println(this.name + "'s state: " + this.state);
//			System.out.println("Time spent in state: " + this.stateTime);
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

		public boolean isStabilityStaggering() {
			return stabilityStaggering;
		}

		public void setStabilityStaggering(boolean staggering) {
			this.stabilityStaggering = staggering;
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
