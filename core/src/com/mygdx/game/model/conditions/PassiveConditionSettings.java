package com.mygdx.game.model.conditions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public abstract class PassiveConditionSettings implements Serializable{
	String type;
	boolean endIfNotMet;
	
	public abstract PassiveConditionSettings deepCopy();
	
	@Override
	public void write(Json json) {
		json.writeValue("endIfNotMet", endIfNotMet);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		Boolean endIfNotMet = json.readValue("endIfNotMet", Boolean.class, jsonData);
		if (endIfNotMet != null) {
			this.endIfNotMet = endIfNotMet;
		}
		else {
			this.endIfNotMet = false;
		}
	}

	public String getType() {
		return type;
	}
}
