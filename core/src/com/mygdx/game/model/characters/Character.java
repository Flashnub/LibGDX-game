package com.mygdx.game.model.characters;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.actions.Attack;
import com.mygdx.game.model.characters.player.Player.PlayerModel;
import com.mygdx.game.model.effects.Effect;
import com.mygdx.game.model.effects.MovementEffect;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.ObjectListener;
import com.mygdx.game.model.projectiles.Projectile;

public abstract class Character {
	
	private EntityUIModel characterUIData;
	private CharacterModel characterData;
	
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
	
	//=============================================================//
	//----------------------------MODEL----------------------------//
	//=============================================================//

	public abstract class CharacterModel extends EntityModel{
		
		public final String idleState = "Idle";
		public final String walkState = "Walk";
		public final String backWalkState = "Backwalk";
		
		String state;
		float currentHealth, maxHealth, currentWill, maxWill, attack;
		boolean isImmuneToInjury, attacking;
		protected boolean jumping;
		boolean didChangeState;
		boolean staggering;
		boolean facingLeft;
	    public float injuryTime = 0f;
		String name, uuid; 
		
//		public float gameplayHitBoxWidthModifier;
//		public float gameplayHitBoxHeightModifier;
		ActionListener actionListener;
		ObjectListener objectListener;
		
//		public Vector2 velocity, acceleration;
//		public Rectangle gameplayHitBox;
//		public Rectangle imageHitBox;
//		boolean isProcessingMovementEffect;
		EntityUIModel uiModel;
		CharacterProperties properties;
		ArrayList <Effect> currentEffects;
		ArrayList <Integer> indicesToRemove;
		ActionSequence currentActionSequence;
		ArrayDeque <ActionSequence> actionSequences;
		
		//Debug
		float stateTime;
		
		public CharacterModel(String characterName, EntityUIModel uiModel) {
			this.currentEffects = new ArrayList <Effect>();
			this.indicesToRemove = new ArrayList<Integer>();
			this.actionSequences = new ArrayDeque<ActionSequence>();
			setState(idleState);
			isImmuneToInjury = false;
			attacking = false;
			staggering = false;
			jumping = true;
			facingLeft = false;
//			isProcessingMovementEffect = false;
			stateTime = 0f;

			UUID id = UUID.randomUUID();
			this.name = characterName;
			this.uuid = id.toString();
			this.gameplayHitBoxWidthModifier = 0.19f;
			this.gameplayHitBoxHeightModifier = 0.6f;
			this.uiModel = uiModel;
			this.properties = JSONController.loadCharacterProperties(characterName);
			this.properties.setSource(this);
			setMaxHealth(properties.getMaxHealth());
			setCurrentHealth(properties.getMaxHealth());
			setMaxWill(properties.getMaxWill());
			setCurrentWill(properties.getMaxWill());
			setAttack(properties.getAttack());
			acceleration.y = -properties.getGravity();
		}
		
		public void update(float delta, TiledMapTileLayer collisionLayer) {
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
			
			super.setGameplaySize(delta, collisionLayer);
			//clamp velocity
			if (this.getVelocity().y > this.properties.jumpSpeed)
				this.getVelocity().y = this.properties.jumpSpeed;
//			else if (this.getVelocity().y < -this.properties.jumpSpeed)
//				this.getVelocity().y = -this.properties.jumpSpeed;
				
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
			
			if (jumping) {
				setState(velocity.y >= 0 ? idleState : idleState); //Jump : Fall
			}
		}
		
		private void handleActionSequences(float delta) {
			if (this.currentActionSequence != null) {
				currentActionSequence.process(delta, actionListener);
				if (this.currentActionSequence.isFinished()) {
					currentActionSequence = null;
				}
			}
			else if (actionSequences.peek() != null) {
				this.currentActionSequence = actionSequences.poll();
			}
			
		}
		
		
		public void addActionSequence (ActionSequence sequence) {
//			sequence.setSource(this);
			actionSequences.add(sequence);
		}
		
		private void handleEffects(float delta) {
			this.indicesToRemove.clear();
//			//process existing effects.
//			for (int i = 0; i < currentEffects.size(); i++) {
//				Effect effect = currentEffects.get(i);
//				boolean isFinished = effect.process(this, delta);
//				if (isFinished) {
//					indicesToRemove.add(i);
//				}
//			}
//			//Remove finished effects
//			for (int i = 0; i < indicesToRemove.size(); i++) {
//				currentEffects.remove(indicesToRemove.get(i));
//			}
			
			for(Iterator<Effect> iterator = this.currentEffects.iterator(); iterator.hasNext();) {
				Effect effect = iterator.next();
				boolean isFinished = effect.process(this, delta);
				if (isFinished) {
					effect.completion(this);
					iterator.remove();
				}
			}
//			this.isProcessingMovementEffect = movementEffect;
		}
		
		public void addEffect(Effect effect) {
			effect.initialProcess(this);
			if (effect instanceof MovementEffect) {
				MovementEffect mEffect = ((MovementEffect) effect);
				for (Effect currentEffect : currentEffects) {
					if (currentEffect instanceof MovementEffect) {
						mEffect.setOldAccel(((MovementEffect) currentEffect).getOldAccel());
						currentEffects.remove(currentEffect);
						break;
					}
				}
			}
			currentEffects.add(effect);
		}
		
		public MovementEffect getCurrentMovement() {
			for (Effect effect : this.currentEffects) {
				if (effect instanceof MovementEffect) {
					return (MovementEffect) effect;
				}
			}
			return null;
		}
		
		public boolean isTargetToLeft(CharacterModel target) {
			return this.gameplayHitBox.x > target.gameplayHitBox.x; 
		}
		
		public void jump() {
	        if (!jumping) {
	            jumping = true;
	            this.getVelocity().y = getJumpSpeed();
	        }
	    }
		
	    public void stopJump() {
	    	if (jumping && this.getVelocity().y >= 0) {
	    		this.getVelocity().y = 0;
	    	}
	    }
	    
		public void walk(boolean left) {
			this.setFacingLeft(left);
			this.velocity.x = left ? -this.properties.getWalkingSpeed() : this.properties.getWalkingSpeed();
			if (this instanceof PlayerModel) {
				setState(left ? this.backWalkState : this.walkState); //Walk
			}
		}
		
		public void stopWalk() {
			this.velocity.x = 0;
//			setState(this.idleState);
		}
		
		public float getJumpSpeed() {
			return this.properties.jumpSpeed;
		}
		
		public float getWalkSpeed() {
			return this.properties.walkingSpeed;
		}

		
		public void clearActionSequencesWithNewSequence(ActionSequence sequence) {
			actionSequences.clear();
			this.currentActionSequence = sequence;
		}
		
//		public CollisionCheck checkForXCollision(float maxTime, TiledMapTileLayer collisionLayer, float xVelocity, boolean shouldMove) {
//			float tileWidth = collisionLayer.getTileWidth();
//			float tileHeight = collisionLayer.getTileHeight();
//			
//			boolean collisionX = false;
//			float time = 0f;
//			
//			Rectangle tempImageBounds = new Rectangle(this.imageHitBox);
//			Rectangle tempGameplayBounds = new Rectangle(this.gameplayHitBox);
//			Array <CellWrapper> tilesToCheckForWorldObjects = new Array<CellWrapper>();
//			float tempVelocity = xVelocity;
//			float delta;
//			if (maxTime > 1 / 300f) {
//				delta = 1 / 300f;
//			}
//			else {
//				delta = maxTime;
//			}
//			while (time < maxTime) {
//				time += delta;
//				tempVelocity += this.acceleration.x * delta;
//				tempImageBounds.setX(tempImageBounds.getX() + tempVelocity * delta);
//				tempGameplayBounds.setX(tempImageBounds.getX() + tempImageBounds.getWidth() * .4f);
//				
//
//				if (this.getVelocity().x < 0) {
//					//left blocks
//					int topLeftXIndex = ((int) (tempGameplayBounds.x / tileWidth));
//					int topLeftYIndex = ((int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight));
//
//					Cell topLeftBlock = collisionLayer.getCell(topLeftXIndex, topLeftYIndex);
//					
//					int middleLeftXIndex = ((int) (tempGameplayBounds.x / tileWidth));
//					int middleLeftYIndex = ((int) ((tempGameplayBounds.y + tempGameplayBounds.height / 2) / tileHeight));
//					
//					Cell middleLeftBlock = collisionLayer.getCell(middleLeftXIndex, middleLeftYIndex);
//					
//					int lowerLeftXIndex = ((int) (tempGameplayBounds.x / tileWidth));
//					int lowerLeftYIndex = ((int) ((tempGameplayBounds.y) / tileHeight));
//					
//					Cell lowerLeftBlock = collisionLayer.getCell(lowerLeftXIndex, lowerLeftYIndex);
//					
//					if (topLeftBlock != null)
//						collisionX = ((Boolean)topLeftBlock.getTile().getProperties().get("Impassable")).equals(true);
//						
//					
//					//middle left block
//					if(!collisionX && middleLeftBlock != null)
//						collisionX = ((Boolean)middleLeftBlock.getTile().getProperties().get("Impassable")).equals(true);
//					
//					//lower left block
//					if(!collisionX && lowerLeftBlock != null )
//						collisionX = ((Boolean)lowerLeftBlock.getTile().getProperties().get("Impassable")).equals(true);
//					
//					tilesToCheckForWorldObjects.add(new CellWrapper(topLeftBlock, new Vector2(topLeftXIndex * tileWidth, topLeftYIndex * tileHeight)));
//					tilesToCheckForWorldObjects.add(new CellWrapper(middleLeftBlock, new Vector2(middleLeftXIndex * tileWidth, middleLeftYIndex * tileHeight)));
//					tilesToCheckForWorldObjects.add(new CellWrapper(lowerLeftBlock, new Vector2(lowerLeftXIndex * tileWidth, lowerLeftYIndex * tileHeight)));
//
////					this.leftcollisionX = collisionX;
//					
//				}
//				else if (this.getVelocity().x > 0) {
//					//right blocks
//					int topRightXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth);
//					int topRightYIndex = (int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight);
//					
//					Cell topRightBlock = collisionLayer.getCell(topRightXIndex, topRightYIndex);
//					
//					int middleRightXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth);
//					int middleRightYIndex = (int) ((tempGameplayBounds.y + tempGameplayBounds.height / 2) / tileHeight);
//					
//					Cell middleRightBlock = collisionLayer.getCell(middleRightXIndex, middleRightYIndex);
//					
//					int lowerRightXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth);
//					int lowerRightYIndex = (int) ((tempGameplayBounds.y) / tileHeight);
//					
//					Cell lowerRightBlock = collisionLayer.getCell(lowerRightXIndex, lowerRightYIndex);
//					
//					// top right block
//					if (topRightBlock != null)
//						collisionX = ((Boolean)topRightBlock.getTile().getProperties().get("Impassable")).equals(true);
//					
//					//middle right block
//					if(!collisionX && middleRightBlock != null)
//						collisionX = ((Boolean)middleRightBlock.getTile().getProperties().get("Impassable")).equals(true);
//					
//					//lower right block
//					if(!collisionX && lowerRightBlock != null)
//						collisionX = ((Boolean)lowerRightBlock.getTile().getProperties().get("Impassable")).equals(true);
//					
////					this.rightCollisionX = collisionX;
//					tilesToCheckForWorldObjects.add(new CellWrapper(topRightBlock, new Vector2(topRightXIndex * tileWidth, topRightYIndex * tileHeight)));
//					tilesToCheckForWorldObjects.add(new CellWrapper(middleRightBlock, new Vector2(middleRightXIndex * tileWidth, middleRightYIndex * tileHeight)));
//					tilesToCheckForWorldObjects.add(new CellWrapper(lowerRightBlock, new Vector2(lowerRightXIndex * tileWidth, lowerRightYIndex * tileHeight)));
//				}
//				
//				collisionX = collisionX || this.objectListener.doTilesCollideWithObjects(tilesToCheckForWorldObjects, collisionLayer);
//				if (collisionX)
//				{
//					break;
//				}
//				else {
//					tilesToCheckForWorldObjects.clear();
//				}
//			}
//			//move on x axis
//			if (shouldMove && !collisionX) {
//				this.gameplayHitBox.x = tempGameplayBounds.x;
//				this.imageHitBox.x = tempImageBounds.x;
//			}
//			//react to X collision
//			return new CollisionCheck(collisionX, time);
//		}
//		
//		public CollisionCheck checkForYCollision(float maxTime, TiledMapTileLayer collisionLayer, float yVelocity, boolean shouldMove) {
//			float tileWidth = collisionLayer.getTileWidth();
//			float tileHeight = collisionLayer.getTileHeight();
//			
//			boolean collisionY = false;
//			float time = 0f;
//			
//			Rectangle tempImageBounds = new Rectangle(this.imageHitBox);
//			Rectangle tempGameplayBounds = new Rectangle(this.gameplayHitBox);
//			Array <CellWrapper> tilesToCheckForWorldObjects = new Array<CellWrapper>();
//			float tempVelocity = yVelocity;
//			float delta;
//			if (maxTime > 1 / 300f) {
//				delta = 1 / 300f;
//			}
//			else {
//				delta = maxTime;
//			}
//			while (time < maxTime) {
//				
//				time += delta;
//				
//				tempVelocity += this.acceleration.y * delta;
//				
//				tempImageBounds.setY(tempImageBounds.getY() + tempVelocity * delta);
//				tempGameplayBounds.setY(tempImageBounds.getY() + tempImageBounds.getHeight() * .2f);
//				
//
//				if (this.getVelocity().y < 0) {
//					int bottomLeftXIndex = (int) (tempGameplayBounds.x / tileWidth);
//					int bottomLeftYIndex = (int) ((tempGameplayBounds.y) / tileHeight);
//					
//					Cell bottomLeftBlock = collisionLayer.getCell(bottomLeftXIndex, bottomLeftYIndex);
//					
//					int bottomMiddleXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width / 2) / tileWidth);
//					int bottomMiddleYIndex = (int) (tempGameplayBounds.y / tileHeight);
//					
//					Cell bottomMiddleBlock = collisionLayer.getCell(bottomMiddleXIndex, bottomMiddleYIndex);
//					
//					int bottomRightXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth);
//					int bottomRightYIndex = (int) (tempGameplayBounds.y / tileHeight);
//					
//					Cell bottomRightBlock = collisionLayer.getCell(bottomRightXIndex, bottomRightYIndex);
//					//bottom left block
//					if (bottomLeftBlock != null)
//						collisionY = ((Boolean)bottomLeftBlock.getTile().getProperties().get("Impassable")).equals(true);
//						
//					//bottom middle block
//					if(!collisionY && bottomMiddleBlock != null)
//						collisionY = ((Boolean)bottomMiddleBlock.getTile().getProperties().get("Impassable")).equals(true);
//					
//					//bottom right block
//					if(!collisionY && bottomRightBlock != null)
//						collisionY = ((Boolean)bottomRightBlock.getTile().getProperties().get("Impassable")).equals(true);
//					
//					tilesToCheckForWorldObjects.add(new CellWrapper(bottomLeftBlock, new Vector2(bottomLeftXIndex * tileWidth, bottomLeftYIndex * tileHeight)));
//					tilesToCheckForWorldObjects.add(new CellWrapper(bottomMiddleBlock, new Vector2(bottomMiddleXIndex * tileWidth, bottomMiddleYIndex * tileHeight)));
//					tilesToCheckForWorldObjects.add(new CellWrapper(bottomRightBlock, new Vector2(bottomRightXIndex * tileWidth, bottomRightYIndex * tileHeight)));
//			
//				} 
//				else if (this.getVelocity().y > 0) {
//					
//					int topLeftXIndex = (int) (tempGameplayBounds.x / tileWidth);
//					int topLeftYIndex = (int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight);
//					
//					Cell topLeftBlock = collisionLayer.getCell(topLeftXIndex, topLeftYIndex);
//					
//					int topMiddleXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width / 2) / tileWidth);
//					int topMiddleYIndex = (int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight);
//					
//					Cell topMiddleBlock = collisionLayer.getCell(topMiddleXIndex, topMiddleYIndex);
//					
//					int topRightXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth);
//					int topRightYIndex = (int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight);
//					
//					Cell topRightBlock = collisionLayer.getCell(topRightXIndex, topRightYIndex);
//					
//					//top left block
//					if (topLeftBlock != null)
//						collisionY = ((Boolean)topLeftBlock.getTile().getProperties().get("Impassable")).equals(true);
//					
//					//top middle block
//					if(!collisionY && topMiddleBlock != null)
//						collisionY = ((Boolean)topMiddleBlock.getTile().getProperties().get("Impassable")).equals(true);
//					
//					//top right block
//					if(!collisionY && topRightBlock != null)
//						collisionY = ((Boolean)topRightBlock.getTile().getProperties().get("Impassable")).equals(true);
//					
//					tilesToCheckForWorldObjects.add(new CellWrapper(topLeftBlock, new Vector2(topLeftXIndex * tileWidth, topLeftYIndex * tileHeight)));
//					tilesToCheckForWorldObjects.add(new CellWrapper(topMiddleBlock, new Vector2(topMiddleXIndex * tileWidth, topMiddleYIndex * tileHeight)));
//					tilesToCheckForWorldObjects.add(new CellWrapper(topRightBlock, new Vector2(topRightXIndex * tileWidth, topRightYIndex * tileHeight)));
//				}
//				collisionY = collisionY || this.objectListener.doTilesCollideWithObjects(tilesToCheckForWorldObjects, collisionLayer);
//				
//				if (collisionY)
//				{
//					break;
//				}
//				else {
//					tilesToCheckForWorldObjects.clear();
//				}
//			}
//			//move on x axis
////			this.getImageHitBox().setX(this.getImageHitBox().getX() + this.getVelocity().x * delta);
////			this.getGameplayHitBox().setX(this.getImageHitBox().getX() + (this.getImageHitBox().getWidth() * .4f));
//			if (shouldMove && !collisionY) {
//				this.gameplayHitBox.y = tempGameplayBounds.y;
//				this.imageHitBox.y = tempImageBounds.y;
//			}
//
//			//react to X collision
//			return new CollisionCheck(collisionY, time);
//		}
		
		protected void movementWithCollisionDetection(float delta, TiledMapTileLayer collisionLayer) {
		//logic for collision detection
			CollisionCheck collisionX = this.checkForXCollision(delta, collisionLayer, this.velocity.x, true);
			if (collisionX.doesCollide) {
				this.getVelocity().x = 0;
				this.getAcceleration().x = 0;
			}
			CollisionCheck collisionY = this.checkForYCollision(delta, collisionLayer, this.velocity.y, true);
			if (collisionY.doesCollide) {
				if (this.getVelocity().y < 0) {
					landed();
				}
				this.getVelocity().y = 0;
			}

		}
		
		public float howLongTillYCollision(float maxTime, TiledMapTileLayer collisionLayer) {
			CollisionCheck collisionY = this.checkForYCollision(maxTime, collisionLayer, this.velocity.y, false);
			return collisionY.timeUntilCollision;
		}
		

		
		public float howLongTillXCollision(float maxTime, TiledMapTileLayer collisionLayer) {
			CollisionCheck collisionX = this.checkForXCollision(maxTime, collisionLayer, this.velocity.x, false);
			return collisionX.timeUntilCollision;
		}
		
	    public void landed() {
	    	if (this.jumping) {
				this.jumping = false;
				if (this.getVelocity().x > 0)
				{
					setState(walkState);  
				}
				else if (this.getVelocity().x < 0) {
					setState(backWalkState);
				}
	    	}
	    }
		
		
		public void onDeath() {
			
		}
		
		public void shouldProjectileHit(Projectile projectile) {
			if (!isImmuneToInjury()) {
				projectile.processExpirationOrHit(this);
			}
		}
		
		public void shouldAttackHit(Attack attack) {
			if (!isImmuneToInjury()) {
				attack.processAttackOnCharacter(this);
			}
		}
		
		public float getHealthRatio() {
			return ((float)this.getCurrentHealth()) / ((float)this.getMaxHealth());
		}
		
//		public void interruptMovementAction(Movement newMovement) {
//			if (newMovement != null) {
//				this.completeMovementAction();
//			}
//			this.currentMovementAction = newMovement;
//
//		}
//		
//		private void completeMovementAction() {
//			if (this.currentMovementAction != null && this.isProcessingMovementEffect) {
//				this.currentMovementAction.completion(this);
//				this.isProcessingMovementEffect = false;
//				this.currentMovementAction = this.tempMovementActions.poll();
//			}
//		}
		
		public void addToCurrentHealth(float value) {
			this.setCurrentHealth(value + this.currentHealth);
		}
		
		public void removeFromCurrentHealth(float value) {
			this.setCurrentHealth(this.currentHealth - value);
		}
		
		public boolean isProcessingAction() {
			return this.currentActionSequence != null;
		}


		//-------------GETTERS/SETTERS------------//
		
		public String getState() {
			return state;
		}

		public float getGameplayHitBoxWidthModifier() {
			return gameplayHitBoxWidthModifier;
		}

		public float getGameplayHitBoxHeightModifier() {
			return gameplayHitBoxHeightModifier;
		}

//		public boolean isProcessingMovementAction() {
//			return isProcessingMovementEffect;
//		}
//
//		public void setProcessingMovementAction(boolean isProcessingMovementAction) {
//			this.isProcessingMovementEffect = isProcessingMovementAction;
//		}
		
		

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
			this.state = state;
			this.didChangeState = true;
			this.stateTime = 0f;
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
			this.currentWill = currentWill;
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

		public boolean isStaggering() {
			return staggering;
		}

		public void setStaggering(boolean staggering) {
			this.staggering = staggering;
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
