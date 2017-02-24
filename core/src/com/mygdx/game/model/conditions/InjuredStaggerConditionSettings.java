package com.mygdx.game.model.conditions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class InjuredStaggerConditionSettings extends PassiveConditionSettings{
	
	public InjuredStaggerConditionSettings() {
		this.type = InjuredStaggerCondition.type;
	}

	@Override
	public PassiveConditionSettings deepCopy() {
		InjuredStaggerConditionSettings settings = new InjuredStaggerConditionSettings();
		settings.type = InjuredStaggerCondition.type;
		return settings;
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		this.type = InjuredStaggerCondition.type;
	}
	
}
