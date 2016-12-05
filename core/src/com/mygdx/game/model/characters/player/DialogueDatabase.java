package com.mygdx.game.model.characters.player;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.actions.nonhostile.ConditionalDialogueSettings;
import com.mygdx.game.model.actions.nonhostile.DialogueSettings;
import com.mygdx.game.model.actions.nonhostile.NormalDialogueSettings;
import com.mygdx.game.model.characters.Character.CharacterModel;

public class DialogueDatabase implements Serializable{
	HashMap <String, NormalDialogueSettings> normalDialogues;
	Array <ConditionalDialogueSettings> conditionalDialogues;
	
	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("normalDialogues", normalDialogues);
		json.writeValue("conditionalDialogues", conditionalDialogues);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		normalDialogues = json.readValue("normalDialogues", HashMap.class, jsonData);
		conditionalDialogues = json.readValue("conditionalDialogues", Array.class, jsonData);
	}
	
	public DialogueSettings getDialogueForUUID(String string) {
		return normalDialogues.get(string);
	}

	public void setSource (CharacterModel source) {
		for (ConditionalDialogueSettings dialogue : conditionalDialogues) {
			dialogue.setSource(source);
		}
	}
}
