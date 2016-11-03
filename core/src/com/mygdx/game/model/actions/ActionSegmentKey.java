package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json.Serializable;
import com.mygdx.game.model.actions.ActionSequence.ActionType;
import com.mygdx.game.wrappers.StringWrapper;

public class ActionSegmentKey implements Serializable{
	StringWrapper key;
	ActionType typeOfAction;
	

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("key", key);
		json.writeValue("typeOfAction", typeOfAction);
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		typeOfAction = json.readValue("typeOfAction", ActionType.class, jsonData);
		key = json.readValue("key", StringWrapper.class, jsonData);
	}
	public StringWrapper getKey() {
		return key;
	}
	public ActionType getTypeOfAction() {
		return typeOfAction;
	}
	
	
}