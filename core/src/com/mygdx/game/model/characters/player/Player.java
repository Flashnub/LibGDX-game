package com.mygdx.game.model.characters.player;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.game.constants.InputConverter;
import com.mygdx.game.constants.InputConverter.DirectionalInput;
import com.mygdx.game.constants.InputType;
import com.mygdx.game.constants.XBox360Pad;
import com.mygdx.game.model.actions.ActionSegment.ActionState;
import com.mygdx.game.model.actions.ActionSequence.StaggerType;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.actions.nonhostile.ConditionalDialogueSettings;
import com.mygdx.game.model.characters.Character;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.characters.ModelListener;
import com.mygdx.game.model.characters.player.GameSave.UUIDType;
import com.mygdx.game.model.events.InteractableObject;
import com.mygdx.game.model.world.DialogueController;
import com.mygdx.game.model.world.SpawnPoint;
import com.mygdx.game.model.worldObjects.Item;

public class Player extends Character implements InputProcessor, ControllerListener {

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
		setCharacterData(new PlayerModel(characterName, this.getCharacterUIData(), this));
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
	

	@Override
	public void connected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnected(Controller controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean buttonDown(Controller controller, int buttonCode) {
		return ((PlayerModel)getCharacterData()).handleButtonDown(controller, buttonCode);

	}

	@Override
	public boolean buttonUp(Controller controller, int buttonCode) {
		return ((PlayerModel)getCharacterData()).handleButtonUp(controller, buttonCode);
	}

	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value) {
		return ((PlayerModel)getCharacterData()).handleAxisMoved(controller, axisCode, value);
	}

	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value) {
		return ((PlayerModel)getCharacterData()).handlePovMoved(controller, povCode, value);
	}

	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public class PlayerModel extends CharacterModel{
	
		final String blockState = "Block";		
		boolean dashing, canControl, isJumpPressed;
		DirectionalInput currentlyHeldDirection;
	    Array <String> processedCondtionalDialogueUUIDs;
	    SpawnPoint spawnPoint;
	    InteractableObject nearbyObject;
	    PlayerProperties playerProperties;
	    DialogueDatabase dialogues;
	    DialogueController dialogueController;
	    GameSave gameSave;
	    boolean isTalking;
		Queue <String> inputs;
		InputConverter inputConverter;

		public PlayerModel(String characterName, EntityUIModel uiModel, ModelListener modelListener) {
			super(characterName, uiModel, modelListener);
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
			dashing = false;
			canControl = false;
			this.currentlyHeldDirection = DirectionalInput.NONE;
			isJumpPressed = false;
			this.widthCoefficient = 0.19f;
			this.heightCoefficient = 0.6f;
			this.inputs = new Queue <String> ();
			this.inputConverter = new InputConverter(this.gameSave);
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
	    
//	    private void block() {
//	    	if (!jumping) {
//		    	ActionSequence blockAction = this.getCharacterProperties().getActions().get("PlayerBlock").cloneSequenceWithSourceAndTarget(this, null, this.getActionListener(), this.getCollisionChecker());
//		    	this.addActionSequence(blockAction);
//	    	}
//	    }
	    
	    public boolean isDodging() {
	        return dashing;
	    }
	    
//	    private void attack() {
//	    	if (!this.isProcessingActiveSequences() || this.isAbleToEnqueueAction()) {
//	    		ActionSequence followup = this.getFollowupSequence();
//	    	 	ActionSequence nextAction;
//	    	 	if (followup != null) {
//	    	 		nextAction =  this.getCharacterProperties().getActions().get(followup.getActionKey().getKey()).cloneSequenceWithSourceAndTarget(this, null, this.getActionListener(), this.getCollisionChecker());;
//	    	 	}
//	    	 	else if (this.jumping){
//	    	 		nextAction = this.getCharacterProperties().getActions().get("Aerial1").cloneSequenceWithSourceAndTarget(this, null, this.getActionListener(), this.getCollisionChecker());
//	    	 	}
//	    	 	else if (this.isLockDirection()) {
//	    	 		nextAction = this.getCharacterProperties().getActions().get("Rushdown").cloneSequenceWithSourceAndTarget(this, null, this.getActionListener(), this.getCollisionChecker());;
//	    	 	}
//	    	 	else {
//	    	 		nextAction = this.getCharacterProperties().getActions().get("PlayerAttack").cloneSequenceWithSourceAndTarget(this, null, this.getActionListener(), this.getCollisionChecker());
//	    	 	}
//	    		this.addActionSequence(nextAction);
//	    	}
//	   
//	    }
	    

	    private void checkToDisruptCurrentAct(String inputType) {
	    	if (this.getCurrentActiveActionSeq() != null) {
		    	boolean needsInterruptForWindup = this.getCurrentActiveActionSeq().getAction().doesNeedDisruptionDuringWindup() && this.getCurrentActiveActionSeq().getAction().getActionState().equals(ActionState.WINDUP);
		    	boolean needsInterruptForAction = this.getCurrentActiveActionSeq().getAction().doesNeedDisruptionDuringActive() && this.getCurrentActiveActionSeq().getAction().getActionState().equals(ActionState.ACTION);
		    	
		    	if (needsInterruptForWindup || needsInterruptForAction) {
		    		Queue <String> inputs = new Queue <String>();
			    	inputs.addFirst(inputType);
			    	if (this.getCurrentActiveActionSeq().doInputsMatch(inputs, this, true)) {
			    		if (needsInterruptForWindup) {
			    			this.forceActiveForActiveAction();
			    		}
			    		else if (needsInterruptForAction) {
			    			this.forceCooldownForActiveAction();
			    		}
			    	}
		    	}
	    	}
	    	
	    }
	    
	    private boolean queueUpActionFromInputs() {
	    	ActionSequence sequence = null;
	    	//Check char props first.
	    	sequence = this.getCharacterProperties().getSequenceGivenInputs(this.inputs, this);
	    	if (sequence != null) {
		    	this.stopHorizontalMovement();
	    		this.addActionSequence(sequence.cloneSequenceWithSourceAndTarget(this, null, getActionListener(), this.getCollisionChecker()));
	    		return true;
	    	}
	    	//Check weapon next.
	    	sequence = this.getCurrentWeapon().getSpecificWeaponAction(this.inputs, this);
	    	if (sequence != null) {
		    	this.stopHorizontalMovement();
	    		this.addActionSequence(sequence.cloneSequenceWithSourceAndTarget(this, null, getActionListener(), this.getCollisionChecker()));
	    		return true;
	    	}
	    	
	    	return false;
	    }
	    
//	    @Override
//	    public void shouldUnlockControls(ActionSequence action) {
//	    	if ((action.getAction().getActionState().equals(ActionState.COOLDOWN) && action.isActive()) || action.getAction().isFinished())
//	    	{
//	    		this.setActionLock(false);
//	    	}
//	    }
	    
//	    @Override
//		public boolean isAbleToEnqueueAction() {
//	    	return this.getCurrentActiveActionSeq() != null && !this.getCurrentActiveActionSeq().getAction().getActionState().equals(ActionState.WINDUP);
//	    }
	    
	    
		public boolean handleKeyDown (int keyCode)
		{
			DirectionalInput potentialDirectionalInput = this.inputConverter.getDirectionFromKeyCodeForDown(keyCode);
			if (!potentialDirectionalInput.equals(DirectionalInput.NONE))
				this.currentlyHeldDirection = potentialDirectionalInput;
			
			String inputType = this.inputConverter.convertKeyCodeToInputType(keyCode, this.currentlyHeldDirection);
			if (!inputType.equals("")) {
				this.inputs.addFirst(inputType);
				if (this.queueUpActionFromInputs())
				{
					return true;
				}
				switch (inputType) {
				case InputType.LEFT:
					horizontalMove(true);
					break;
				case InputType.RIGHT:
					horizontalMove(false);
					break;
				case InputType.JUMP:
					if (!actOnObject())
						jump();
					break;
//				case InputType.ACTION:
//					actOnObject();
//					break;
				case InputType.USEITEM:
//					this.lockDirection();
					break;
				default:
					break;
				}
			}

			
			return true;
		}
		
		public boolean handleKeyUp (int keyCode) {
			DirectionalInput directionHeld = this.inputConverter.getDirectionFromKeyCodeForUp(keyCode, this.currentlyHeldDirection);
			if (directionHeld != null) {
				this.currentlyHeldDirection = directionHeld;
				checkIfNeedToStopWalk();
			}
			String inputType = this.inputConverter.convertKeyCodeToInputType(keyCode, this.currentlyHeldDirection);
			if (!inputType.equals("")) {
				this.checkToDisruptCurrentAct(inputType);
				switch (inputType) {
				case InputType.USEITEM:
//					unlockDirection();
					break;
				default:
					break;
				}
			}


			return true;
		}
		
		public boolean handleButtonDown(Controller controller, int buttonCode) {
			String inputType = this.inputConverter.convertButtonCodeToInputType(buttonCode, currentlyHeldDirection);
			if (!inputType.equals("")) {
				this.inputs.addFirst(inputType);
				if (this.queueUpActionFromInputs())
				{
					return true;
				}
				switch (inputType) {
				case InputType.JUMP:
					if (!actOnObject())
						jump();
					break;
//				case InputType.ACTION:
//					actOnObject();
//					break;
				case InputType.USEITEM:
//					this.lockDirection();
					break;
				default:
					break;
				}
			}
			
			
			return true;
		}
		
		public boolean handleButtonUp(Controller controller, int buttonCode) {
			String inputType = this.inputConverter.convertButtonCodeToInputType(buttonCode, this.currentlyHeldDirection);
			if (!inputType.equals("")) {
				this.checkToDisruptCurrentAct(inputType);
				switch (inputType) {
				case InputType.USEITEM:
					unlockDirection();
					break;
				default:
					break;
				}
			}

			return true;
		}
		
		public boolean handleAxisMoved(Controller controller, int axisCode, float value) {
			if (axisCode == XBox360Pad.AXIS_LEFT_X) {
				DirectionalInput potentialDirectionalInput = this.inputConverter.getDirectionFromAxisCode(axisCode, value);
				this.currentlyHeldDirection = potentialDirectionalInput;

				switch (potentialDirectionalInput) {
				case LEFT:
					horizontalMove(true);
					break;
				case RIGHT:
					horizontalMove(false);
					break;
				case NONE:
					this.checkIfNeedToStopWalk();
				default:
					break;
				}
			}
			return true;
		}
		
		public boolean handlePovMoved(Controller controller, int povCode, PovDirection value) {
			//Handle item switching stuff here
			return true;
		}
		
		private void checkIfNeedToStopWalk() {
			if (!this.currentlyHeldDirection.equals(DirectionalInput.LEFT) && !this.currentlyHeldDirection.equals(DirectionalInput.RIGHT)) {
				this.stopHorizontalMovement();
			}
		}
		

		private boolean actOnObject() {
			if (nearbyObject != null) {
				this.nearbyObject.actOnThis(this);
				return true;
			}
			return false;
		}
		


		@Override
		public int getAllegiance() {
			return Player.allegiance;
		}

		public void setNearbyObject(InteractableObject nearbyObject) {
			this.nearbyObject = nearbyObject;
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
		public boolean handleAdditionalXCollisionLogic(Rectangle tempGameplayBounds, Rectangle tempImageBounds,  boolean alreadyCollided) {
			if (alreadyCollided) {
				if (this.walking) {
					this.stopHorizontalMovement();
				}
				return alreadyCollided;
			}
			else {
				EntityModel collidedEntity = this.getCollisionChecker().checkIfEntityCollidesWithOthers(this, tempGameplayBounds);
				boolean entityCollision = this.respectEntityCollision() && collidedEntity != null;
				if (entityCollision) {
					this.stopHorizontalMovement();
				}
				if (!this.hasProcessedOverlapCorrection()) {
					this.stopEntityOverlapIfNeeded(collidedEntity, tempGameplayBounds, tempImageBounds);
				}
				return entityCollision;
			}
		}
		
		@Override
		public boolean handleAdditionalYCollisionLogic(Rectangle tempGameplayBounds, Rectangle tempImageBounds, boolean alreadyCollided) {
			return this.getCollisionChecker().checkIfEntityCollidesWithOthers(this, tempGameplayBounds) != null;
		}

		public void setDialogueController(DialogueController dialogueController) {
			this.dialogueController = dialogueController;
		}

		@Override
		public void setPatrolInfo(Array<Float> wayPoints, float patrolDuration, float breakDuration) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void patrolWalk(boolean left) {
			
		}

		@Override
		public Direction isTryingToMoveHorizontally() {
			if (this.currentlyHeldDirection.equals(DirectionalInput.LEFT)) {
				return Direction.LEFT;
			}
			else if (this.currentlyHeldDirection.equals(DirectionalInput.RIGHT)) {
				return Direction.RIGHT;
			}
			return Direction.NaN;

		}

		@Override
		public void tensionOverload() {
			ActionSequence staggerAction = ActionSequence.createStaggerSequence(this, null, this.getActionListener(), StaggerType.Tension);
    		this.addActionSequence(staggerAction);
    		this.setCurrentTension(0);
		}


		
	}

	
}
