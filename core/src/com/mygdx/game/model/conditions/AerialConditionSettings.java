package com.mygdx.game.model.conditions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class AerialConditionSettings extends PassiveConditionSettings{
	
	public AerialConditionSettings() {
		this.type = AerialCondition.type;
	}

	@Override
	public PassiveConditionSettings deepCopy() {
		AerialConditionSettings settings = new AerialConditionSettings();
		settings.type = AerialCondition.type;
		return settings;
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		this.type = AerialCondition.type;
	}
}
