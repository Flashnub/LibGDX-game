package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json.Serializable;
import com.mygdx.game.model.actions.ActionSequence.ActionType;
import com.mygdx.game.wrappers.StringWrapper;

public class ActionSegmentKey implements Serializable{
	String key;
	ActionType typeOfAction;
	
	public ActionSegmentKey() {
		
	}
	
	public ActionSegmentKey(String key, ActionType typeOfAction) {
		this.key = key;
		this.typeOfAction = typeOfAction;
	}

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
		key = json.readValue("key", String.class, jsonData);
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof ActionSegmentKey) {
			ActionSegmentKey segmentKey = (ActionSegmentKey) object;
			return this.typeOfAction.equals(segmentKey.typeOfAction) && this.key.equals(segmentKey.key);
		}
		return false;
	}
	
	public String getKey() {
		return key;
	}
	public ActionType getTypeOfAction() {
		return typeOfAction;
	}
	
	
}