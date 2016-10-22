package com.mygdx.game.model.characters.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.characters.Character;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.world.SpawnPoint;
import com.mygdx.game.model.worldObjects.WorldObject;

public class Player extends Character implements InputProcessor {

	public static final String characterName = "Player";
	public static final String characterType = "Player";
	
	public Player() {
		super(characterName);
		setUpPlayer();
	}
	
	private void setUpPlayer() {
		loadSaveFile();
		setCharacterData(new PlayerModel(characterName, this.getCharacterUIData()));
		this.getCharacterData().setName(characterName);
	}
	
	private void loadSaveFile() {
		//Do stuff to load in spawn point + player info.
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return ((PlayerModel)getCharacterData()).handleKeyDown(keycode);
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return ((PlayerModel)getCharacterData()).handleKeyUp(keycode);
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public class PlayerModel extends CharacterModel{
	
		final String blockState = "Block";
		final String idleState = "Idle";
		final String walkState = "Walk";
		final String backWalkState = "Backwalk";
		
		boolean dashing, canControl, isWalkLeftPressed, isWalkRightPressed, isJumpPressed;
		final float walkingVelocity = 200;
		boolean didChangeState;
	    Array<Integer> applicableKeys;
	    SpawnPoint spawnPoint;
	    float injuryTime = 0f;
	    WorldObject nearbyObject;
	    static final int allegiance = 0;
	    PlayerProperties playerProperties;

		public PlayerModel(String characterName, EntityUIModel uiModel) {
			super(characterName, uiModel);
			Json json = new Json();
			playerProperties = json.fromJson(PlayerProperties.class, Gdx.files.internal("Json/Player/playerActions.json"));
			applicableKeys = new Array<Integer>();
			applicableKeys.add(Keys.W);
			applicableKeys.add(Keys.A);
			applicableKeys.add(Keys.S);
			applicableKeys.add(Keys.D);
			applicableKeys.add(Keys.Q);
			applicableKeys.add(Keys.E);
			applicableKeys.add(Keys.R);
			applicableKeys.add(Keys.Z);
			applicableKeys.add(Keys.SPACE);
			dashing = false;
			canControl = false;
			isWalkLeftPressed = false;
			isWalkRightPressed = false;
			isJumpPressed = false;
			this.setJumpSpeed(700f);
			this.setGravity(950f);
			this.acceleration.y = -this.getGravity();
			this.gameplayHitBoxWidthModifier = 0.19f;
			this.gameplayHitBoxHeightModifier = 0.6f;
		}
		

		@Override
		protected void manageAutomaticStates(float delta, TiledMapTileLayer collisionLayer) {
			// TODO Auto-generated method stub
			if (jumping) {
				setState(velocity.y >= 0 ? idleState : idleState); //Jump : Fall
			}
			else if (dashing) {
				setState(idleState); //Dash
			}

			if (this.didChangeState) {
				this.getUiModel().setAnimationTime(0f);
				this.didChangeState = false; 
			}
			if (this.isImmuneToInjury()) {
				injuryTime += delta;
			}
			if (this.injuryTime > 2f) {
				this.setImmuneToInjury(false);
			}
		}
		
		private void jump() {
	        if (!jumping) {
	            jumping = true;
	            this.getVelocity().y = getJumpSpeed();
	        }
	    }
	    
	    private void stopJump() {
	    	if (jumping && this.getVelocity().y >= 0) {
	    		this.getVelocity().y = 0;
	    	}
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

	    public void dash(boolean left) {
	    	if (!jumping) {
//	    		float velocityX = left ? -500f : 500f;
//	    		float velocityY = this.velocity.y;
//	    		float accelerationX = left ? 1000f : -1000f;
//	    		float accelerationY = this.acceleration.y;
//	    		float duration = 0.5f;
//	    		
//	    		Movement movement = new Movement();
//	    		movement.setVelocity(new Vector2(velocityX, velocityY));
//	    		movement.setAcceleration(new Vector2(accelerationX, accelerationY));
//	    		movement.setDuration(duration);
//	    		movement.setDelayToActivate(0.5f);
//	    		movement.setFunction(new MovementFunction() {
//	    			@Override 
//	    			public void process(CharacterModel mover, Movement movement, float delta) {
//	    				super.process(mover, movement, delta);
//	    			}
//	    			
//	    			@Override
//	    			public void completion(CharacterModel mover, Movement movement) {
//	    				super.completion(mover, movement);
//	    				mover.velocity.x = 0;
//	    				mover.acceleration.x = 0;
//	    			}
//	    		});
	    		ActionSequence dashAction = this.getCharacterProperties().getActions().get("PlayerDash").cloneSequence();
//	    		for (Movement movement : dashAction.getMovements()) {
//	    			this.getTempMovementActions().add(movement);
//	    		}
//	    		this.setCurrentMovementAction(this.getTempMovementActions().poll());
	    		this.addActionSequence(dashAction);
	    	}
	    }
	    
	    private void block() {
	    	this.setImmuneToInjury(true);
	    	setState(this.blockState);
	    }
	    
	    private void stopBlock() {
	    	setState(this.idleState);
	    	this.setImmuneToInjury(false);
	    }

	    public boolean isDodging() {
	        return dashing;
	    }
	    
	    private void attack() {
//	    	this.setState(PlayerState.Block);
//	    	Rectangle attackHitBox = new Rectangle();
//	    	attackHitBox.x = this.gameplayHitBox.x + this.gameplayHitBox.width;
//	    	attackHitBox.y = this.gameplayHitBox.y;
//	    	attackHitBox.width = 400;
//	    	attackHitBox.height = this.gameplayHitBox.height + 80;
//	    	
//	    	int allegiance = this.getAllegiance();
////	    	AttackFunction function = new AttackFunction() {
////	    		public void process(CharacterModel target) {
////	    	    	int damage = 2;
////	    	    	target.setCurrentHealth(target.getCurrentHealth() - damage);
////	    	    	target.velocity.x += 10;
////	    		}
////	    	};
//	    	//Lambda Expression - Java 8
////	    	AttackFunction function = (target) -> {
////	    		
////	    	};
//	    	
//	    	Attack attack = new Attack();
//	    	attack.setAllegiance(allegiance);
//	    	attack.setFunction(function);
//	    	attack.setSource(this);
//	    	attack.setHitBox(attackHitBox);
    		ActionSequence attackAction = this.getCharacterProperties().getActions().get("PlayerAttack");
    		this.addActionSequence(attackAction);

	    	
//	    	this.getAttackListener().processAttack(attack);
	    }
	    
		public boolean handleKeyDown (int keyCode)
		{
			if (!applicableKeys.contains(keyCode, true)) {
				return false;
			}
			switch (keyCode) {
			case Keys.A:
				isWalkLeftPressed = true;
				walk(true);
				break;
			case Keys.D:
				isWalkRightPressed = true;
				walk(false);
				break;
			case Keys.Q:
				block();
				break;
			case Keys.SPACE:
				jump();
				break;
			case Keys.E:
				attack();
				break;
			case Keys.Z:
				dash(true);
				break;
			case Keys.R:
				actOnObject();
				break;
			default:
				break;
			}
		
			
			return true;
		}
		
		public boolean handleKeyUp (int keyCode) {
			if (!applicableKeys.contains(keyCode, true)) {
				return false;
			}
			switch (keyCode) {
			case Keys.A:
				isWalkLeftPressed = false;
				checkIfNeedToStopWalk();
				break;
			case Keys.D: 
				isWalkRightPressed = false;
				checkIfNeedToStopWalk();
				break;
			case Keys.Q:
				stopBlock();
				break;
			case Keys.SPACE:
				stopJump();
				break;
			default:
				break;
			}

			return true;
		}
		
		private void checkIfNeedToStopWalk() {
			if (!isWalkLeftPressed && !isWalkRightPressed) {
				this.velocity.x = 0;
				setState(this.idleState);
			}
		}
		
		private void walk(boolean left) {
			this.setFacingLeft(left);
			this.velocity.x = left ? -walkingVelocity : walkingVelocity;
			setState(left ? this.backWalkState : this.walkState); //Walk
		}
		
		private void actOnObject() {
			this.getObjectListener().objectToActOn(this.nearbyObject);
		}
		
		@Override public void setState(String state) {
			super.setState(state);
			this.didChangeState = true;
		}
		
		@Override
		public void setImmuneToInjury(boolean isImmuneToInjury) {
			super.setImmuneToInjury(isImmuneToInjury);
			injuryTime = 0f;
		}


		@Override
		public int getAllegiance() {
			return PlayerModel.allegiance;
		}

		public WorldObject getNearbyObject() {
			return nearbyObject;
		}


		public void setNearbyObject(WorldObject nearbyObject) {
			this.nearbyObject = nearbyObject;
		}

		public PlayerProperties getPlayerProperties() {
			return playerProperties;
		}
		
	}

	
}
