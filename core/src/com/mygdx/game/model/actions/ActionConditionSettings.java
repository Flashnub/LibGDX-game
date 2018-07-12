package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public abstract class ActionConditionSettings implements Serializable{
	String type;

	@Override
	public void write(Json json) {
		
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		
	}

	public String getType() {
		return type;
	}
	
	public abstract ActionConditionSettings deepCopy();
}
