package com.mygdx.game.model.characters;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.actions.nonhostile.ConditionalDialogueSettings;
import com.mygdx.game.model.characters.player.GameSave;
import com.mygdx.game.model.characters.player.Player;
import com.mygdx.game.model.characters.player.Player.PlayerModel;
import com.mygdx.game.model.events.InteractableObject;
import com.mygdx.game.model.world.DialogueActor;
import com.mygdx.game.model.world.DialogueController;

public class NPCCharacter extends Character{

	public static final String characterType = "NPC";
	
	public NPCCharacter(String characterName, DialogueController controller) {
		super(characterName);
		this.setCharacterData(new NPCCharacterModel(characterName, this.getCharacterUIData(), this));
		((NPCCharacterModel)this.getCharacterData()).setController(controller);
	}
	
	public class NPCCharacterModel extends CharacterModel implements DialogueActor, InteractableObject {
		
		NPCProperties npcProperties;
		boolean isCurrentlyInteractable;
		DialogueController controller;
	    Array <String> processedCondtionalDialogueUUIDs;
	    float currentStartPatrol;
	    float currentEndPatrol;
	    boolean onPatrolBreak;
	    boolean onPatrol;
	    float breakTime;
	    float patrolTime;
		
		public NPCCharacterModel(String characterName, EntityUIModel uiModel, ModelListener modelListener) {
			super(characterName, uiModel, modelListener);
			npcProperties = JSONController.loadNPCProperties(characterName);
			if (npcProperties != null) {
				npcProperties.setSource(this);
			}
			this.isCurrentlyInteractable = true;
			this.processedCondtionalDialogueUUIDs = new Array <String> ();
			this.onPatrolBreak = false;
			this.onPatrol = false;
			this.breakTime = 0f;
			this.patrolTime = 0f;
		}

		@Override
		public boolean handleAdditionalXCollisionLogic(Rectangle tempGameplayBounds, Rectangle tempImageBounds, boolean alreadyCollided) {
			return false;
		}
		
		@Override
		public boolean handleAdditionalYCollisionLogic(Rectangle tempGameplayBounds, Rectangle tempImageBounds, boolean alreadyCollided) {
			return false;
		}
		
		@Override
		public void patrolWalk(boolean left) {
			this.setFacingLeft(left);
			this.walking = true;
			this.velocity.x = left ? -this.getNPCProperties().patrolWalkSpeed : this.getNPCProperties().patrolWalkSpeed;
			this.setMovementStatesIfNeeded();
		}

		@Override
		public int getAllegiance() {
			return Player.allegiance;
		}
		
		@Override
		public void update(float delta, TiledMapTileLayer collisionLayer) {
			super.update(delta, collisionLayer);
			this.checkForDialoguesToHandle();
		}
		
		private void checkForDialoguesToHandle() {
			if (getNPCProperties() != null) {
				for (ConditionalDialogueSettings dialogue : getNPCProperties().getConditionalDialogues()){
					if (!this.processedCondtionalDialogueUUIDs.contains(dialogue.getUUID(), true) && dialogue.conditionsMet()) {
						controller.handleDialogue(dialogue);
						this.processedCondtionalDialogueUUIDs.add(dialogue.getUUID());
					}
				}
			}
		}

		@Override
		public void stopHorizontalMovement() {
			super.stopHorizontalMovement();
			if (this.onPatrol)
				this.setOnBreak(true);
		}
		
		public void resetPatrolFields() {
			this.onPatrol = false;
			this.onPatrolBreak = false;
			this.patrolTime = 0f;
			this.breakTime = 0f;
			this.currentStartPatrol = 0;
			this.currentEndPatrol = this.getNPCProperties().getRandomPatrolWayPoint(currentStartPatrol);
		}
		
		public void handlePatrol(float delta) {
			
			//On start.
			if ((!this.onPatrol && !this.onPatrolBreak) || (this.onPatrol && this.gameplayHitBox.x - 10f < this.currentEndPatrol && this.gameplayHitBox.x + 10f > this.currentEndPatrol)) {
				this.setOnBreak(true);
			}
			
			if (this.onPatrolBreak) {
				this.breakTime += delta;
				if (this.walking)
					this.stopHorizontalMovement();
				if (this.breakTime > this.getNPCProperties().getBreakDuration()) {
					this.setOnBreak(false);
				}
			}
			else if (this.onPatrol){
				//figure out velocity.x
				float startPatrolInWorldCoordinates = this.currentStartPatrol + this.gameplayHitBox.x;
				float endPatrolInWorldCoordinates = this.currentEndPatrol + this.gameplayHitBox.x;
				
				if (startPatrolInWorldCoordinates == endPatrolInWorldCoordinates) {
					this.stopHorizontalMovement();
				}
				else if (this.gameplayHitBox.x < startPatrolInWorldCoordinates && this.gameplayHitBox.x < endPatrolInWorldCoordinates) 
				{
					//NPC is to the left of startPatrol and outside patrol routine, move back to start.
					this.patrolWalk(false);
				}
				else if (this.gameplayHitBox.x > startPatrolInWorldCoordinates && this.gameplayHitBox.x > endPatrolInWorldCoordinates) 
				{
					//NPC is to the right of startPatrol and outside patrol routine, move back to start.
					this.patrolWalk(true);
				}
				else if (startPatrolInWorldCoordinates < endPatrolInWorldCoordinates) 
				{
					//Patrol is from left to right.
					this.patrolWalk(false);
					this.patrolTime += delta;
				}
				else if (startPatrolInWorldCoordinates > endPatrolInWorldCoordinates) {
					//Patrol is from right to left.
					this.patrolWalk(true);
					this.patrolTime += delta;
				}
				
				if (this.patrolTime > this.getNPCProperties().getPatrolDuration()) {
					this.setOnBreak(true);
				}

			}

			
		}
		
		public void setOnBreak(boolean onBreak) {
			this.breakTime = 0f;
			this.patrolTime = 0f;
			if (onBreak) {
				this.onPatrol = false;
				this.onPatrolBreak = true;
			}
			else {
				this.currentStartPatrol = 0;
				this.currentEndPatrol = this.getNPCProperties().getRandomPatrolWayPoint(currentStartPatrol);
				this.onPatrol = true;
				this.onPatrolBreak = false;
			}
		}
		
		
		private void processDialogueRequest(CharacterModel target) {
			//Make ActionSequence for dialogue.
			ActionSequence dialogueAction = ActionSequence.createSequenceWithDialog(getNPCProperties().getNextStoryDialogue(), this, target, this.controller, this.actionListener);
			System.out.println("Adding dialogue");
			this.addActionSequence(dialogueAction);

		}
		
		private void processDialogueRequestWithUUID(CharacterModel target, String UUIDForDialogue) {
			//Make ActionSequence for dialogue.
			ActionSequence dialogueAction = ActionSequence.createSequenceWithDialog(getNPCProperties().getSpecificExternalConditionalDialogue(UUIDForDialogue), this, target, this.controller, this.actionListener);
			this.addActionSequence(dialogueAction);
		}
		
		public void setController(DialogueController controller) {
			this.controller = controller;
		}
		
		public void setDialogueIndexWithGameSave(GameSave gameSave) {
			this.npcProperties.setChapterIndex(gameSave.chapterIndexForNPCUUID(this.getNPCProperties().UUID));
		}
		
		public void setPatrolInfo(Array <Float> wayPoints, float patrolDuration, float breakDuration) {
			this.getNPCProperties().setPatrolWaypoints(wayPoints);
			this.getNPCProperties().setPatrolDuration(patrolDuration);
			this.getNPCProperties().setBreakDuration(breakDuration);
			if (wayPoints.size > 1) {
				this.currentStartPatrol = 0;
				this.currentEndPatrol = this.getNPCProperties().getRandomPatrolWayPoint(this.currentStartPatrol);
			}
			else {
				this.currentStartPatrol = 0f;
				this.currentEndPatrol = 0f;
			}

		}

		@Override
		public String getUUID() {
			return getNPCProperties().UUID;
		}

		@Override
		public void dialogueAction(CharacterModel target) {
			this.processDialogueRequest(target);
		}

		@Override
		public void responseDialogueAction(CharacterModel target, String UUIDForDialogue) {
			this.processDialogueRequestWithUUID(target, UUIDForDialogue);
		}

		public boolean isCurrentlyInteractable() {
			return isCurrentlyInteractable;
		}

		public void setCurrentlyInteractable(boolean isCurrentlyInteractable) {
			this.isCurrentlyInteractable = isCurrentlyInteractable;
		}

		public NPCProperties getNPCProperties() {
			return this.npcProperties;
		}

		public void setCurrentStartPatrol(float currentStartPatrol) {
			this.currentStartPatrol = currentStartPatrol;
		}

		public void setCurrentEndPatrol(float currentEndPatrol) {
			this.currentEndPatrol = currentEndPatrol;
		}

		public float getCurrentStartPatrol() {
			return currentStartPatrol;
		}

		public float getCurrentEndPatrol() {
			return currentEndPatrol;
		}

		@Override
		public Direction isTryingToMoveHorizontally() {
			return Direction.NaN;
		}

		@Override
		public void actOnThis(PlayerModel player) {
			this.dialogueAction(player);
		}

		@Override
		public void tensionOverload() {
			
		}


	}
}
