package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class EffectActionConditionSettings extends ActionConditionSettings{
	
	Integer effectID;

	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue("effectID", effectID);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		json.readValue("effectID", Integer.class, jsonData);
		type = EffectActionCondition.type;
	}

	public Integer getEffectID() {
		return effectID;
	}
}
