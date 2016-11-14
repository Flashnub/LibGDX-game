package com.mygdx.game.model.characters.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.constants.SaveController;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.characters.Character;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.characters.player.GameSave.UUIDType;
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
		boolean dashing, canControl, isWalkLeftPressed, isWalkRightPressed, isJumpPressed;
	    Array<Integer> applicableKeys;
	    SpawnPoint spawnPoint;
	    WorldObject nearbyObject;
	    static final int allegiance = 0;
	    PlayerProperties playerProperties;
	    GameSave gameSave;

		public PlayerModel(String characterName, EntityUIModel uiModel) {
			super(characterName, uiModel);
			Json json = new Json();
			GameSave gameSave;
			FileHandle fileHandle = Gdx.files.local("Saves/currentSave.json");
			if (fileHandle.exists()) {
				gameSave = json.fromJson(GameSave.class, fileHandle);
			}
			else {
				gameSave = GameSave.testSave();
			}
			this.gameSave = gameSave;
			playerProperties = json.fromJson(PlayerProperties.class, Gdx.files.internal("Json/Player/playerActions.json"));
			playerProperties.populateWith(gameSave);
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
			this.gameplayHitBoxWidthModifier = 0.19f;
			this.gameplayHitBoxHeightModifier = 0.6f;
		}
		

		@Override
		protected void manageAutomaticStates(float delta, TiledMapTileLayer collisionLayer) {
			super.manageAutomaticStates(delta, collisionLayer);
			
			 if (dashing) {
				setState(idleState); //Dash
			}
		}
	    
		public boolean isUUIDInSave(Integer UUID) {
			return gameSave.isUUIDInSave(UUID);
		}

		public void addUUIDToSave(Integer UUID, UUIDType uuidType) {
			gameSave.addUUIDToSave(UUID, uuidType);
		}
	    
	    public void dash(boolean left) {
	    	if (!jumping) {
	    		ActionSequence dashAction = this.getCharacterProperties().getActions().get("PlayerDash").cloneSequenceWithSourceAndTarget(this, null, this.getActionListener());
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
    		ActionSequence attackAction = this.getCharacterProperties().getActions().get("PlayerAttack");
    		this.addActionSequence(attackAction);
   	
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
		

		private void actOnObject() {
			this.getObjectListener().objectToActOn(this.nearbyObject);
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

		public GameSave getGameSave() {
			return gameSave;
		}
		
	}

	
}
