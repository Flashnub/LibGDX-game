package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class PreviousActionConditionSettings extends ActionConditionSettings{

	ActionSegmentKey prevActionKey;
	
	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue("prevActionKey", prevActionKey);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		prevActionKey = json.readValue("prevActionKey", ActionSegmentKey.class, jsonData);
		type = PreviousActionCondition.type;
	}

	public ActionSegmentKey getPrevActionKey() {
		return prevActionKey;
	}
	
	@Override
	public ActionConditionSettings deepCopy() {
		PreviousActionConditionSettings copy = new PreviousActionConditionSettings();
		copy.prevActionKey = this.prevActionKey;
		copy.type = this.type;
		return copy;
	}
}
