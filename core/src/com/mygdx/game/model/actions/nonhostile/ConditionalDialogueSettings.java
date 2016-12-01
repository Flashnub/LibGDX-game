package com.mygdx.game.model.actions.nonhostile;

import com.mygdx.game.model.characters.Character.CharacterModel;

public abstract class ConditionalDialogueSettings extends DialogueSettings{
	//Use this class for dialogue that happens on proximity/injury/some condition
	
	public boolean conditionsMet() {
		return !isProcessed;
	}
	
	boolean isProcessed;
	CharacterModel source;
	
	public void setSource(CharacterModel source) {
		this.source = source;
	}

	public boolean isProcessed() {
		return isProcessed;
	}

	public void setisProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}
	
}
