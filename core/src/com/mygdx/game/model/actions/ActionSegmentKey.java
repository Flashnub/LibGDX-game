package com.mygdx.game.model.actions;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json.Serializable;
import com.mygdx.game.model.actions.ActionSequence.ActionType;
import com.mygdx.game.wrappers.StringWrapper;

public class ActionSegmentKey implements Serializable{
	ArrayList<StringWrapper> keys;
	ActionType typeOfAction;
	

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("keys", keys);
		json.writeValue("typeOfAction", typeOfAction);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		typeOfAction = json.readValue("typeOfAction", ActionType.class, jsonData);
		keys = json.readValue("keys", ArrayList.class, jsonData);
	}
	public ArrayList<StringWrapper> getKeys() {
		return keys;
	}
	public ActionType getTypeOfAction() {
		return typeOfAction;
	}
	
	
}