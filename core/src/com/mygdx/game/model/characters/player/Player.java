package com.mygdx.game.model.characters.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.model.actions.ActionSegment.ActionState;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.actions.ActionSequence.ActionType;
import com.mygdx.game.model.actions.nonhostile.ConditionalDialogueSettings;
import com.mygdx.game.model.characters.Character;
import com.mygdx.game.model.characters.NPCCharacter;
import com.mygdx.game.model.characters.NPCCharacter.NPCCharacterModel;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.characters.player.GameSave.UUIDType;
import com.mygdx.game.model.world.DialogueController;
import com.mygdx.game.model.world.SpawnPoint;
import com.mygdx.game.model.worldObjects.Item;
import com.mygdx.game.model.worldObjects.WorldObject;

public class Player extends Character implements InputProcessor {

	public static final String characterName = "Player";
	public static final String characterType = "Player";
	public static final int allegiance = 1;
	public static final String DialogueUUID = "Player";

	
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
	    Array <String> processedCondtionalDialogueUUIDs;
	    SpawnPoint spawnPoint;
	    WorldObject nearbyObject;
	    NPCCharacter nearbyNPC;
	    PlayerProperties playerProperties;
	    DialogueDatabase dialogues;
	    DialogueController dialogueController;
	    GameSave gameSave;
	    boolean isTalking;

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
			dialogues = json.fromJson(DialogueDatabase.class, Gdx.files.internal("Json/Player/dialogues.json"));
			dialogues.setSource(this);
			this.processedCondtionalDialogueUUIDs = new Array <String>();
			applicableKeys = new Array<Integer>();
			applicableKeys.add(Keys.W);
			applicableKeys.add(Keys.A);
			applicableKeys.add(Keys.S);
			applicableKeys.add(Keys.D);
			applicableKeys.add(Keys.Q);
			applicableKeys.add(Keys.E);
			applicableKeys.add(Keys.R);
			applicableKeys.add(Keys.Z);
			applicableKeys.add(Keys.X);
			applicableKeys.add(Keys.TAB); 
			applicableKeys.add(Keys.SPACE);
			dashing = false;
			canControl = false;
			isWalkLeftPressed = false;
			isWalkRightPressed = false;
			isJumpPressed = false;
			this.widthCoefficient = 0.19f;
			this.heightCoefficient = 0.6f;
		}
		
		@Override
		public void update(float delta, TiledMapTileLayer collisionLayer) {
			super.update(delta, collisionLayer);
			this.handleConditionalDialogues();
		}

		@Override
		protected void manageAutomaticStates(float delta, TiledMapTileLayer collisionLayer) {
			super.manageAutomaticStates(delta, collisionLayer);
			
			 if (dashing) {
				setState(idleState); //Dash
			}
		}
		
		private void handleConditionalDialogues() {
			if (dialogues != null) {
				for (ConditionalDialogueSettings dialogue : this.dialogues.conditionalDialogues)
				{
					if (!this.processedCondtionalDialogueUUIDs.contains(dialogue.getUUID(), true) && dialogue.conditionsMet()) {
						dialogueController.handleDialogue(dialogue);
						this.processedCondtionalDialogueUUIDs.add(dialogue.getUUID());
					}
				}
			}
		}
	    
		public boolean isUUIDInSave(Integer UUID) {
			return gameSave.isUUIDInSave(UUID);
		}

		public void addUUIDToSave(Integer UUID, UUIDType uuidType) {
			gameSave.addUUIDToSave(UUID, uuidType);
		}
		
		public void addItemToInventory(Item item) {
			this.getPlayerProperties().getInventory().add(item);
			//Eventually add HUD code to reflect receiving item.
		}
		
		public void dialogueAction(String uuid) {
			ActionSequence sequence = ActionSequence.createSequenceWithDialog(this.dialogues.getDialogueForUUID(uuid), this, null, dialogueController, this.getActionListener());
			this.addActionSequence(sequence);
		}
	    
	    public void dash(boolean left) {
	    	if (!jumping) {
	    		ActionSequence dashAction = this.getCharacterProperties().getActions().get("PlayerDash").cloneSequenceWithSourceAndTarget(this, null, this.getActionListener(), this.getCollisionChecker());
	    		this.addActionSequence(dashAction);
	    	}
	    }
	    
	    private void block() {
	    	if (!jumping) {
		    	ActionSequence blockAction = this.getCharacterProperties().getActions().get("PlayerBlock").cloneSequenceWithSourceAndTarget(this, null, this.getActionListener(), this.getCollisionChecker());
		    	this.addActionSequence(blockAction);
	    	}
	    }
	    
	    public boolean isDodging() {
	        return dashing;
	    }
	    
	    private void attack() {
	    	if (!this.isProcessingActiveSequences() || this.isAbleToEnqueueAction()) {
	    		ActionSequence followup = this.getFollowupSequence();
	    	 	ActionSequence nextAction;
	    	 	if (followup != null) {
	    	 		nextAction =  this.getCharacterProperties().getActions().get(followup.getActionKey().getKey().value).cloneSequenceWithSourceAndTarget(this, null, this.getActionListener(), this.getCollisionChecker());;
	    	 	}
	    	 	else if (this.jumping){
	    	 		nextAction = this.getCharacterProperties().getActions().get("Aerial1").cloneSequenceWithSourceAndTarget(this, null, this.getActionListener(), this.getCollisionChecker());
	    	 	}
	    	 	else if (this.isLockDirection()) {
	    	 		nextAction = this.getCharacterProperties().getActions().get("Launch").cloneSequenceWithSourceAndTarget(this, null, this.getActionListener(), this.getCollisionChecker());;
	    	 	}
	    	 	else {
	    	 		nextAction = this.getCharacterProperties().getActions().get("PlayerAttack").cloneSequenceWithSourceAndTarget(this, null, this.getActionListener(), this.getCollisionChecker());
	    	 	}
	    		this.addActionSequence(nextAction);
	    	}
	   
	    }
	    

	       	
	    private ActionSequence getFollowupSequence() {
    	 	for (ActionSequence action : this.getProcessingActionSequences()) {
	    		if (action.isActive() 
	    		&& action.getActionKey().getTypeOfAction().equals(ActionType.Attack)
	    		&& action.getNextActionKey() != null) {
	    			ActionSequence followingAttack = this.getCharacterProperties().getActions().get(action.getNextActionKey().getKey().value).cloneSequenceWithSourceAndTarget(this, null, this.getActionListener(), this.getCollisionChecker());;
	    			return followingAttack;
	    		}
	    	}
	    	return null;
	    }
	    
	    @Override
	    public void shouldUnlockControls(ActionSequence action) {
	    	if ((action.getAction().getActionState().equals(ActionState.COOLDOWN) && action.isActive()) || action.getAction().isFinished())
	    	{
	    		this.setActionLock(false);
	    	}
	    }
	    
	    @Override
		public boolean isAbleToEnqueueAction() {
	    	return this.getCurrentActiveActionSeq() != null && !this.getCurrentActiveActionSeq().getAction().getActionState().equals(ActionState.WINDUP);
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
			case Keys.X:
				this.processNPCRequest();
				break;
			case Keys.TAB:
				this.lockDirection();
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
			case Keys.SPACE:
				stopJump();
				break;
			case Keys.TAB:
				unlockDirection();
				break;
			default:
				break;
			}

			return true;
		}
		
		private void checkIfNeedToStopWalk() {
			if (!isWalkLeftPressed && !isWalkRightPressed) {
				this.stopWalk();
			}
		}
		

		private void actOnObject() {
			this.getObjectListener().objectToActOn(this.nearbyObject);
		}
		
		private void processNPCRequest() {
			if (this.nearbyNPC != null) {
				((NPCCharacterModel) this.nearbyNPC.getCharacterData()).dialogueAction(this);
			}
		}

		@Override
		public int getAllegiance() {
			return Player.allegiance;
		}

		public WorldObject getNearbyObject() {
			return nearbyObject;
		}

		public void setNearbyObject(WorldObject nearbyObject) {
			this.nearbyObject = nearbyObject;
		}
		
		public void setNearbyNPC(NPCCharacter npc) {
			this.nearbyNPC = npc;
		}

		public PlayerProperties getPlayerProperties() {
			return playerProperties;
		}

		public GameSave getGameSave() {
			return gameSave;
		}
		
		public boolean isTalking() {
			return isTalking;
		}


		public void setTalking(boolean isTalking) {
			this.isTalking = isTalking;
		}


		public DialogueDatabase getDialogues() {
			return dialogues;
		}


		@Override
		public boolean handleAdditionCollisionLogic(Rectangle tempGameplayBounds) {
			return this.getCollisionChecker().checkIfEntityCollidesWithOthers(this, tempGameplayBounds);
		}


		public void setDialogueController(DialogueController dialogueController) {
			this.dialogueController = dialogueController;
		}
		
		
	}

	
}
