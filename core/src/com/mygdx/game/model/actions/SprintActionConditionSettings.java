package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class SprintActionConditionSettings extends ActionConditionSettings {
	
	boolean onlyIfSprinting;
	
	@Override
	public void write(Json json) {
		json.writeValue("onlyIfSprinting", onlyIfSprinting);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		Boolean onlyIfSprinting = json.readValue("onlyIfSprinting", Boolean.class, jsonData);
		if (onlyIfSprinting != null) {
			this.onlyIfSprinting = onlyIfSprinting.booleanValue();
		}
		else {
			this.onlyIfSprinting = true;
		}
		this.type = SprintActionCondition.type;
	}
	
	@Override
	public ActionConditionSettings deepCopy() {
		SprintActionConditionSettings copy = new SprintActionConditionSettings();
		copy.onlyIfSprinting = this.onlyIfSprinting;
		copy.type = this.type;
		return copy;
	}
}
