package com.mygdx.game.model.characters;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.actions.nonhostile.ConditionalDialogueSettings;
import com.mygdx.game.model.characters.player.GameSave;
import com.mygdx.game.model.characters.player.Player;
import com.mygdx.game.model.events.DialogueListener;
import com.mygdx.game.model.world.DialogueActor;
import com.mygdx.game.model.world.DialogueController;

public class NPCCharacter extends Character{

	public static final String characterType = "NPC";
	
	public NPCCharacter(String characterName, DialogueController controller) {
		super(characterName);
		((NPCCharacterModel)this.getCharacterData()).setController(controller);
	}
	
	public class NPCCharacterModel extends CharacterModel implements DialogueActor {

		NPCProperties npcProperties;
		boolean isCurrentlyInteractable;
		DialogueController controller;
		
		public NPCCharacterModel(String characterName, EntityUIModel uiModel) {
			super(characterName, uiModel);
			npcProperties = JSONController.loadNPCProperties(characterName);
			if (npcProperties != null) {
				npcProperties.setSource(this);
			}
			this.isCurrentlyInteractable = true;
		}

		@Override
		public boolean handleAdditionCollisionLogic(Rectangle tempGameplayBounds) {
			return false;
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
			for (ConditionalDialogueSettings dialogueSettings : npcProperties.getConditionalDialogues()){
				if (dialogueSettings.conditionsMet()) {
					controller.handleDialogue(dialogueSettings);
				}
			}
		}
		
		private void processDialogueRequest(CharacterModel target) {
			//Make ActionSequence for dialogue.
			ActionSequence dialogueAction = ActionSequence.createSequenceWithDialog(npcProperties.getNextStoryDialogue(), this, target, this.controller);
			this.addActionSequence(dialogueAction);

		}
		
		private void processDialogueRequestWithUUID(CharacterModel target, String UUIDForDialogue) {
			//Make ActionSequence for dialogue.
			ActionSequence dialogueAction = ActionSequence.createSequenceWithDialog(npcProperties.getSpecificExternalConditionalDialogue(UUIDForDialogue), this, target, this.controller);
			this.addActionSequence(dialogueAction);
		}
		
		public void setController(DialogueController controller) {
			this.controller = controller;
		}
		
		public void setDialogueIndexWithGameSave(GameSave gameSave) {
			this.npcProperties.setChapterIndex(gameSave.chapterIndexForNPCUUID(this.npcProperties.UUID));
		}

		@Override
		public String getUUID() {
			return npcProperties.UUID;
		}

		@Override
		public void dialogueAction(CharacterModel target) {
			this.processDialogueRequest(target);
		}

		@Override
		public void externalDialogueAction(CharacterModel target, String UUIDForDialogue) {
			this.processDialogueRequestWithUUID(target, UUIDForDialogue);
		}

		public boolean isCurrentlyInteractable() {
			return isCurrentlyInteractable;
		}

		public void setCurrentlyInteractable(boolean isCurrentlyInteractable) {
			this.isCurrentlyInteractable = isCurrentlyInteractable;
		}
	}
}
