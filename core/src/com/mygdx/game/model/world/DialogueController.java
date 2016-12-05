package com.mygdx.game.model.world;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.actions.nonhostile.DialogueSettings;
import com.mygdx.game.model.actions.nonhostile.NormalDialogueSettings;
import com.mygdx.game.model.characters.NPCCharacter;
import com.mygdx.game.model.characters.NPCCharacter.NPCCharacterModel;
import com.mygdx.game.model.characters.enemies.Enemy;
import com.mygdx.game.model.characters.player.Player;
import com.mygdx.game.model.characters.player.Player.PlayerModel;
import com.mygdx.game.model.events.DialogueListener;

public class DialogueController implements InputProcessor{

	Array <NPCCharacter> npcCharacters;
	Player player;
	Array <DialogueListener> listeners;
	Array <String> conditionalDialogueUUIDs;
	float currentTime;
	float dialogueRenderTime;
	int currentCharacterIndex;
	DialogueSettings currentDialogue;
	boolean isProcessingDialogue;
	
	
	public DialogueController() {
		this.listeners = new Array <DialogueListener>();
		this.conditionalDialogueUUIDs = new Array <String>();
		this.currentTime = 0f;
		this.currentCharacterIndex = 0;
		this.isProcessingDialogue = false;
	}
	
	public void setEntities(Player player, Array<NPCCharacter> npcCharacters, Array<Enemy> enemies) {
		this.player = player;
		((PlayerModel)this.player.getCharacterData()).setDialogueController(this);
		this.npcCharacters = new Array <NPCCharacter>();
		for (NPCCharacter character : npcCharacters) {
			this.npcCharacters.add(character);
		}
		for (Enemy enemy : enemies) {
			this.npcCharacters.add(enemy);
		}
	}
	
	
	public void update(float delta) {
		if (this.hasDialogue()) {
			this.currentTime += delta;
		}
		else {
			this.currentTime = 0f;
		}
		if (this.hasDialogue() && this.shouldStartProcessingDialogue() && !this.isFinishedPlacingDialogue()) {
			
			dialogueRenderTime += delta;
			if (dialogueRenderTime * currentDialogue.getSpeed() >= 1) {
				this.setCurrentTextToDisplay(
						this.currentDialogue.getWrittenDialogue().substring(0, currentCharacterIndex), 
						this.currentDialogue.getFontName(),
						this.currentDialogue.getFontColor(),
						!this.isProcessingDialogue);
				currentCharacterIndex += 1;
				dialogueRenderTime = 0f; //Need to figure out a better way to handle this
			}
			if (!this.isProcessingDialogue) {
				this.isProcessingDialogue = true;
			}
		}
		else if (this.hasDialogue() && this.isFinishedPlacingDialogue()) {
			float currentTime = this.currentTime - (this.currentDialogue.getTimeToDisplayDialogue() + this.currentDialogue.getWindupTime());
			this.isProcessingDialogue = false;
			if (currentTime >= this.currentDialogue.getCooldownTime()) {
				//Get rid of current dialogue and move on to next one if there is one.
				moveToNextDialogue();
			}
		}
	}
	
	private void reset() {
		this.currentCharacterIndex = 0;
		this.currentTime = 0f;
		this.dialogueRenderTime = 0f;

	}
	
	public void handleDialogue(DialogueSettings dialogue) {
		PlayerModel playerModel = (PlayerModel) player.getCharacterData();
		playerModel.setTalking(true);
		this.currentDialogue = dialogue;

//		if (dialogue instanceof NormalDialogueSettings) {
//			//check to see if should enqueue next dialogue action for character.
//			NormalDialogueSettings nDialogue = (NormalDialogueSettings) dialogue;
//			if (nDialogue.doesHaveMoreDialogue()) {
//			}
//		}
	}


	
	private void moveToNextDialogue() {
		reset();
		if (this.currentDialogue != null && this.currentDialogue.getUUIDForNextCharacterToTalk() != null) {
			//Check player
			String UUIDForCharacter = this.currentDialogue.getUUIDForNextCharacterToTalk();
			if (UUIDForCharacter.equals(Player.DialogueUUID)) {
				PlayerModel pModel = (PlayerModel) player.getCharacterData();
				pModel.dialogueAction(this.currentDialogue.getUUIDForNextDialogue());
			}
			//Check npcs
			else {
				for (NPCCharacter npc : this.npcCharacters) {
					NPCCharacterModel characterModel = (NPCCharacterModel) npc.getCharacterData();
					if (characterModel.getUUID().equals(UUIDForCharacter)) {
						//Handle next dialogue
						if (this.currentDialogue.getUUIDForNextDialogue() != null) {
							characterModel.externalDialogueAction(player.getCharacterData(), this.currentDialogue.getUUIDForNextDialogue()); //How to figure out the target?
						}
						else {
							characterModel.dialogueAction(player.getCharacterData()); //How to figure out the target?
						}
						break;
					}
				}
			}
		}
		else {
			for (DialogueListener listener : listeners) {
				listener.endDialogue();
			}
		}
		this.currentDialogue = null;

	}
	
	private void setCurrentTextToDisplay(String textToDisplay, String fontName, String fontColor, boolean newDialogue) {
		for (DialogueListener listener : listeners) {
			listener.updateDialogueText(textToDisplay, fontName, fontColor, newDialogue);
		}
	}
	
	private boolean shouldStartProcessingDialogue() {
		return this.currentTime >= this.currentDialogue.getWindupTime();
	}
	
	private boolean hasDialogue() {
		return this.currentDialogue != null;
	}
	
	private boolean isFinishedPlacingDialogue() {
		return this.currentDialogue.getWrittenDialogue().length() == this.currentCharacterIndex - 1;
	}
	
	public void addDialogueListener(DialogueListener listener) {
		this.listeners.add(listener);
	}
	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
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
}
