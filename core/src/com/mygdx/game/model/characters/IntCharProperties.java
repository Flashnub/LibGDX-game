package com.mygdx.game.model.characters;

import java.util.HashMap;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class IntCharProperties implements Serializable {
	private HashMap <Integer, String> dialogues;
	private int currentDialogueIndex;

	@Override
	public void write(Json json) {
		json.writeValue("dialogues", dialogues);

	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		dialogues = json.readValue("dialogues", HashMap.class, jsonData);
		currentDialogueIndex = 0;
	}
	
	
	
}
