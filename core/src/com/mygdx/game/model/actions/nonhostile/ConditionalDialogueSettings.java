package com.mygdx.game.model.actions.nonhostile;

import java.util.UUID;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.characters.Character.CharacterModel;

public abstract class ConditionalDialogueSettings extends DialogueSettings{
	//Use this class for dialogue that happens on proximity/injury/some condition
		
	boolean isProcessed;
	CharacterModel source;
	String uuid;
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		UUID id = UUID.randomUUID();
		this.uuid = id.toString();
		
	}
	
	public boolean conditionsMet() {
		return !isProcessed;
	}
	
	public void setSource(CharacterModel source) {
		this.source = source;
	}

	public boolean isProcessed() {
		return isProcessed;
	}

	public void setisProcessed(boolean isProcessed) {
		this.isProcessed = isProcessed;
	}

	public String getUUID() {
		return uuid;
	}
	
	
	
}
