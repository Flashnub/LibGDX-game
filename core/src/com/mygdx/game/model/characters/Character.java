package com.mygdx.game.model.characters;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.actions.Attack;
import com.mygdx.game.model.effects.Effect;
import com.mygdx.game.model.effects.MovementEffect;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.ObjectListener;
import com.mygdx.game.model.projectiles.Projectile;

public abstract class Character {
	
	private EntityUIModel characterUIData;
	private CharacterModel characterData;
	
	
	public Character(String characterName) {
		setCharacterUIData(new EntityUIModel(characterName));
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

	public abstract class CharacterModel {
		
		private final String idleState = "Idle";
		
		String state;
		int currentHealth, maxHealth, currentWill, maxWill, attack;
		boolean isImmuneToInjury, attacking;
		protected boolean jumping;
		boolean staggering;
		boolean facingLeft;
		String name, uuid; 
		
		float gravity = 120f; 
		float jumpSpeed = 120f; 
		public float gameplayHitBoxWidthModifier;
		public float gameplayHitBoxHeightModifier;
		ActionListener actionListener;
		ObjectListener objectListener;
		
		public Vector2 velocity, acceleration;
		public Rectangle gameplayHitBox;
		public Rectangle imageHitBox;
//		boolean isProcessingMovementEffect;
		EntityUIModel uiModel;
		CharacterProperties properties;
		ArrayList <Effect> currentEffects;
		ActionSequence currentActionSequence;
		ArrayDeque <ActionSequence> actionSequences;
		
		public CharacterModel(String characterName, EntityUIModel uiModel) {
			velocity = new Vector2();
			acceleration = new Vector2();
			gameplayHitBox = new Rectangle();
			imageHitBox = new Rectangle();
			
			setState(idleState);
			isImmuneToInjury = false;
			attacking = false;
			staggering = false;
			jumping = true;
			facingLeft = false;
//			isProcessingMovementEffect = false;
			setMaxHealth(maxHealth);
			setMaxWill(maxWill);
			setAttack(attack);
			UUID id = UUID.randomUUID();
			this.uuid = id.toString();
			this.gameplayHitBoxWidthModifier = 0.19f;
			this.gameplayHitBoxHeightModifier = 0.6f;
			this.uiModel = uiModel;
			Json json = new Json();
			this.properties = json.fromJson(CharacterProperties.class, Gdx.files.internal("Json/" + characterName + "/properties.json"));
			this.currentEffects = new ArrayList <Effect>();
			this.actionSequences = new ArrayDeque<ActionSequence>();
		}
		
		public void update(float delta, TiledMapTileLayer collisionLayer) {
			this.move(delta, collisionLayer);
			this.movementWithCollisionDetection(delta, collisionLayer);
			this.manageAutomaticStates(delta, collisionLayer);
			this.handleEffects(delta);
			this.handleActionSequences(delta);
//			System.out.println(imageHitBox.x + " " + imageHitBox.y + " " + imageHitBox.width + " " + imageHitBox.height);
		}
		
		public void move(float delta, TiledMapTileLayer collisionLayer) {
			this.getVelocity().y += this.getAcceleration().y * delta;
			this.getVelocity().x += this.getAcceleration().x * delta;
			
			this.gameplayHitBox.width = this.getImageHitBox().width * gameplayHitBoxWidthModifier;
			this.gameplayHitBox.height = this.getImageHitBox().height * gameplayHitBoxHeightModifier;

			if (currentActionSequence != null) {
				System.out.println(velocity);
			}
			//clamp velocity
			if (this.getVelocity().y > this.jumpSpeed)
				this.getVelocity().y = this.jumpSpeed;
			else if (this.getVelocity().y < -this.jumpSpeed)
				this.getVelocity().y = -this.jumpSpeed;
				
//			}

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
			sequence.setSource(this);
			actionSequences.add(sequence);
		}
		
		private void handleEffects(float delta) {
			boolean movementEffect = false;
			for(Iterator<Effect> iterator = this.currentEffects.iterator(); iterator.hasNext();) {
				Effect effect = iterator.next();
				boolean isFinished = effect.process(this, delta);
				movementEffect = (effect instanceof MovementEffect && !isFinished) || movementEffect;
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

		
		public void clearActionSequencesWithNewSequence(ActionSequence sequence) {
			actionSequences.clear();
			this.currentActionSequence = sequence;
		}
		
		protected void movementWithCollisionDetection(float delta, TiledMapTileLayer collisionLayer) {
		//logic for collision detection
			
			//save old position
			float oldX = this.getImageHitBox().getX(), oldY = this.getImageHitBox().getY(), tileWidth = collisionLayer.getTileWidth(), tileHeight = collisionLayer.getTileHeight();
			boolean collisionX = false, collisionY = false;
			
			//move on x axis
			this.getImageHitBox().setX(this.getImageHitBox().getX() + this.getVelocity().x * delta);
			this.getGameplayHitBox().setX(this.getImageHitBox().getX() + (this.getImageHitBox().getWidth() * .4f));
			
			
			
			
			if (this.getVelocity().x < 0) {
				//left blocks
				Cell topLeftBlock = collisionLayer.getCell
						((int) (this.gameplayHitBox.x / tileWidth), 
						(int) ((this.gameplayHitBox.y + this.gameplayHitBox.height) / tileHeight));
				Cell middleLeftBlock = collisionLayer.getCell
						((int) (this.gameplayHitBox.x / tileWidth), 
						(int) ((this.gameplayHitBox.y + this.gameplayHitBox.height / 2) / tileHeight));
				Cell lowerLeftBlock = collisionLayer.getCell
						((int) (this.gameplayHitBox.x / tileWidth), 
						(int) ((this.gameplayHitBox.y) / tileHeight));
				
				if (topLeftBlock != null)
					collisionX = ((String)topLeftBlock.getTile().getProperties().get("Impassable")).equals("true");
					
				
				//middle left block
				if(!collisionX && middleLeftBlock != null)
					collisionX = ((String)middleLeftBlock.getTile().getProperties().get("Impassable")).equals("true");
				
				//lower left block
				if(!collisionX && lowerLeftBlock != null )
					collisionX = ((String)lowerLeftBlock.getTile().getProperties().get("Impassable")).equals("true");
				
//				this.leftcollisionX = collisionX;
				
			}
			else if (this.getVelocity().x > 0) {
				//right blocks
				Cell topRightBlock = collisionLayer.getCell
						((int) ((this.gameplayHitBox.x + this.gameplayHitBox.width) / tileWidth),
						(int) ((this.gameplayHitBox.y + this.gameplayHitBox.height) / tileHeight));
				Cell middleRightBlock = collisionLayer.getCell
						((int) ((this.gameplayHitBox.x + this.gameplayHitBox.width) / tileWidth),
						(int) ((this.gameplayHitBox.y + this.gameplayHitBox.height / 2) / tileHeight));
				Cell lowerRightBlock = collisionLayer.getCell
						((int) ((this.gameplayHitBox.x + this.gameplayHitBox.width) / tileWidth), 
						(int) ((this.gameplayHitBox.y) / tileHeight));
				
				// top right block
				if (topRightBlock != null)
					collisionX = ((String)topRightBlock.getTile().getProperties().get("Impassable")).equals("true");
				
				//middle right block
				if(!collisionX && middleRightBlock != null)
					collisionX = ((String)middleRightBlock.getTile().getProperties().get("Impassable")).equals("true");
				
				//lower right block
				if(!collisionX && lowerRightBlock != null)
					collisionX = ((String)lowerRightBlock.getTile().getProperties().get("Impassable")).equals("true");
				
//				this.rightCollisionX = collisionX;
			}
			
			//react to X collision
			if (collisionX) {
				this.getImageHitBox().setX(oldX);
				this.gameplayHitBox.setX(oldX + this.getImageHitBox().width * .4f);
				this.getVelocity().x = 0;
				this.getAcceleration().x = 0;
			}

			
		
			//move on y axis
			this.getImageHitBox().setY(this.getImageHitBox().getY() + this.getVelocity().y * delta);
			this.gameplayHitBox.setY(this.getImageHitBox().getY() + this.getImageHitBox().getHeight() * .2f);
			
			//Collision detection: Y axis
			
			if (this.getVelocity().y < 0) {
				Cell bottomLeftBlock = collisionLayer.getCell((int) (this.gameplayHitBox.x / tileWidth), (int) ((this.gameplayHitBox.y) / tileHeight));
				Cell bottomMiddleBlock = collisionLayer.getCell((int) ((this.gameplayHitBox.x + this.gameplayHitBox.width / 2) / tileWidth), (int) ((this.gameplayHitBox.y) / tileHeight));
				Cell bottomRightBlock = collisionLayer.getCell((int) ((this.gameplayHitBox.x + this.gameplayHitBox.width) / tileWidth), (int) ((this.gameplayHitBox.y) / tileHeight));
				//bottom left block
				if (bottomLeftBlock != null)
					collisionY = ((String)bottomLeftBlock.getTile().getProperties().get("Impassable")).equals("true");
					
				//bottom middle block
				if(!collisionY && bottomMiddleBlock != null)
					collisionY = ((String)bottomMiddleBlock.getTile().getProperties().get("Impassable")).equals("true");
				
				//bottom right block
				if(!collisionY && bottomRightBlock != null)
					collisionY = ((String)bottomRightBlock.getTile().getProperties().get("Impassable")).equals("true");
				
		
			} 
			else if (this.getVelocity().y > 0) {
				Cell topLeftBlock = collisionLayer.getCell((int) (this.gameplayHitBox.x / tileWidth), (int) ((this.gameplayHitBox.y + this.gameplayHitBox.height) / tileHeight));			
				Cell topMiddleBlock = collisionLayer.getCell((int) ((this.gameplayHitBox.x + this.gameplayHitBox.width / 2) / tileWidth), (int) ((this.gameplayHitBox.y + this.gameplayHitBox.height) / tileHeight));
				Cell topRightBlock = collisionLayer.getCell((int) ((this.gameplayHitBox.x + this.gameplayHitBox.width) / tileWidth), (int) ((this.gameplayHitBox.y + this.gameplayHitBox.height) / tileHeight));
				
				//top left block
				if (topLeftBlock != null)
					collisionY = ((String)topLeftBlock.getTile().getProperties().get("Impassable")).equals("true");
				
				//top middle block
				if(!collisionY && topMiddleBlock != null)
					collisionY = ((String)topMiddleBlock.getTile().getProperties().get("Impassable")).equals("true");
				
				//top right block
				if(!collisionY && topRightBlock != null)
					collisionY = ((String)topRightBlock.getTile().getProperties().get("Impassable")).equals("true");
			}
			
			//react to Ycollision
			if (collisionY) {
				this.getImageHitBox().setY(oldY);
				this.gameplayHitBox.setY(oldY + this.getImageHitBox().height * .2f);
				if (this.getVelocity().y < 0) {
					landed();
				}
				this.getVelocity().y = 0;

					
			}
			return;
		}
		
		public float howLongTillYCollision(float maxTime, TiledMapTileLayer collisionLayer) {
			float time = 0f;
			Rectangle tempImageBounds = new Rectangle(this.imageHitBox);
			Rectangle tempGameplayBounds = new Rectangle(this.gameplayHitBox);
			float tempVelocity = this.velocity.y;
			while (time <= maxTime) {
				float delta =  1 / 300f;
				tempVelocity += this.acceleration.y * delta;
				float tileWidth = collisionLayer.getTileWidth(), tileHeight = collisionLayer.getTileHeight();
				
				tempImageBounds.setY(tempImageBounds.getY() + tempVelocity * delta);
				tempGameplayBounds.setY(tempImageBounds.getY() + tempImageBounds.getHeight() * .2f);
				boolean collisionY = false;
				
				//Collision detection: Y axis
				
				if (tempVelocity < 0) {
					Cell bottomLeftBlock = collisionLayer.getCell((int) (tempGameplayBounds.x / tileWidth), (int) ((tempGameplayBounds.y) / tileHeight));
					Cell bottomMiddleBlock = collisionLayer.getCell((int) ((tempGameplayBounds.x + tempGameplayBounds.width / 2) / tileWidth), (int) ((tempGameplayBounds.y) / tileHeight));
					Cell bottomRightBlock = collisionLayer.getCell((int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth), (int) ((tempGameplayBounds.y) / tileHeight));
					//bottom left block
					if (bottomLeftBlock != null)
						collisionY = ((String)bottomLeftBlock.getTile().getProperties().get("Impassable")).equals("true");
						
					//bottom middle block
					if(!collisionY && bottomMiddleBlock != null)
						collisionY = ((String)bottomMiddleBlock.getTile().getProperties().get("Impassable")).equals("true");
					
					//bottom right block
					if(!collisionY && bottomRightBlock != null)
						collisionY = ((String)bottomRightBlock.getTile().getProperties().get("Impassable")).equals("true");
					
			
				} 
				else if (tempVelocity > 0) {
					Cell topLeftBlock = collisionLayer.getCell((int) (tempGameplayBounds.x / tileWidth), (int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight));			
					Cell topMiddleBlock = collisionLayer.getCell((int) ((tempGameplayBounds.x + tempGameplayBounds.width / 2) / tileWidth), (int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight));
					Cell topRightBlock = collisionLayer.getCell((int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth), (int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight));
					
					//top left block
					if (topLeftBlock != null)
						collisionY = ((String)topLeftBlock.getTile().getProperties().get("Impassable")).equals("true");
					
					//top middle block
					if(!collisionY && topMiddleBlock != null)
						collisionY = ((String)topMiddleBlock.getTile().getProperties().get("Impassable")).equals("true");
					
					//top right block
					if(!collisionY && topRightBlock != null)
						collisionY = ((String)topRightBlock.getTile().getProperties().get("Impassable")).equals("true");
				}
				
				if (collisionY) {
					break;
				}
				
				time += delta;
			}
			if (time >= maxTime) {
				return maxTime;
			}
			return time;
		}
		
		public float howLongTillXCollision(float maxTime, TiledMapTileLayer collisionLayer) {
			float time = 0f;
			Rectangle tempImageBounds = new Rectangle(this.imageHitBox);
			Rectangle tempGameplayBounds = new Rectangle(this.gameplayHitBox);
			float tempVelocity = this.velocity.x;
			while (time < maxTime) {
				float delta =  1 / 300f;
				tempVelocity += this.acceleration.x * delta;
				float tileWidth = collisionLayer.getTileWidth(), tileHeight = collisionLayer.getTileHeight();
				
				tempImageBounds.setX(tempImageBounds.getX() + tempVelocity * delta);
				tempGameplayBounds.setX(tempImageBounds.getX() + tempImageBounds.getWidth() * .4f);
				boolean collisionX = false;
				
				if (tempVelocity < 0) {
					//left blocks
					Cell topLeftBlock = collisionLayer.getCell
							((int) (tempGameplayBounds.x / tileWidth), 
							(int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight));
					Cell middleLeftBlock = collisionLayer.getCell
							((int) (tempGameplayBounds.x / tileWidth), 
							(int) ((tempGameplayBounds.y + tempGameplayBounds.height / 2) / tileHeight));
					Cell lowerLeftBlock = collisionLayer.getCell
							((int) (tempGameplayBounds.x / tileWidth), 
							(int) ((tempGameplayBounds.y) / tileHeight));
					
					if (topLeftBlock != null)
						collisionX = ((String)topLeftBlock.getTile().getProperties().get("Impassable")).equals("true");
						
					
					//middle left block
					if(!collisionX && middleLeftBlock != null)
						collisionX = ((String)middleLeftBlock.getTile().getProperties().get("Impassable")).equals("true");
					
					//lower left block
					if(!collisionX && lowerLeftBlock != null )
						collisionX = ((String)lowerLeftBlock.getTile().getProperties().get("Impassable")).equals("true");
					
//					this.leftcollisionX = collisionX;
					
				}
				else if (tempVelocity > 0) {
					//right blocks
					Cell topRightBlock = collisionLayer.getCell
							((int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth),
							(int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight));
					Cell middleRightBlock = collisionLayer.getCell
							((int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth),
							(int) ((tempGameplayBounds.y + tempGameplayBounds.height / 2) / tileHeight));
					Cell lowerRightBlock = collisionLayer.getCell
							((int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth), 
							(int) ((tempGameplayBounds.y) / tileHeight));
					
					// top right block
					if (topRightBlock != null)
						collisionX = ((String)topRightBlock.getTile().getProperties().get("Impassable")).equals("true");
					
					//middle right block
					if(!collisionX && middleRightBlock != null)
						collisionX = ((String)middleRightBlock.getTile().getProperties().get("Impassable")).equals("true");
					
					//lower right block
					if(!collisionX && lowerRightBlock != null)
						collisionX = ((String)lowerRightBlock.getTile().getProperties().get("Impassable")).equals("true");
					
//					this.rightCollisionX = collisionX;
				}
				
				if (collisionX) {
					break;
				}
				
				time += delta;
			}
			if (time >= maxTime) {
				return maxTime;
			}
			return time;
		}
		
		protected abstract void manageAutomaticStates(float delta, TiledMapTileLayer collisionLayer);
		
		protected abstract void landed(); 
		
		public abstract int getAllegiance();
		
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
		
		public void addToCurrentHealth(int value) {
			this.setCurrentHealth(value + this.currentHealth);
		}
		
		public void removeFromCurrentHealth(int value) {
			this.setCurrentHealth(this.currentHealth - value);
		}


		//-------------GETTERS/SETTERS------------//
		
		public String getState() {
			return state;
		}

		public ObjectListener getObjectListener() {
			return objectListener;
		}

		public void setItemListener(ObjectListener itemListener) {
			this.objectListener = itemListener;
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

		public ActionListener getAttackListener() {
			return actionListener;
		}

		public void setActionListener(ActionListener attackListener) {
			this.actionListener = attackListener;
		}

		public void setState(String state) {
			this.state = state;
		}

		public int getCurrentHealth() {
			return currentHealth;
		}

		public void setCurrentHealth(int currentHealth) {
			this.currentHealth = Math.min(Math.max(0, currentHealth), this.maxHealth);
		}

		public int getMaxHealth() {
			return maxHealth;
		}

		public void setMaxHealth(int maxHealth) {
			this.maxHealth = maxHealth;
			this.currentHealth = maxHealth;
		}

		public int getCurrentWill() {
			return currentWill;
		}

		public void setCurrentWill(int currentWill) {
			this.currentWill = currentWill;
		}

		public int getMaxWill() {
			return maxWill;
		}

		public void setMaxWill(int maxWill) {
			this.maxWill = maxWill;
			this.currentWill = maxWill;
		}

		public int getAttack() {
			return attack;
		}

		public void setAttack(int attack) {
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

		public float getJumpSpeed() {
			return jumpSpeed;
		}

		public void setJumpSpeed(float jumpSpeed) {
			this.jumpSpeed = jumpSpeed;
		}

		public float getGravity() {
			return gravity;
		}

		public void setGravity(float gravity) {
			this.gravity = gravity;
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
