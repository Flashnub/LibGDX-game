package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public abstract class EntityEffectSettings extends EffectSettings{
	
	float value;
	int specificID;
	
	public static final int defaultID = 1;
	
	public void fillInDefaults() {
		super.fillInDefaults();
		value = 0f;
		specificID = defaultID;
	}
	
	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue("value", value);
		json.writeValue("specificID", specificID);
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		Float value = json.readValue("value", Float.class, jsonData);
		if (value != null) {
			this.value = value;
		}
		else {
			this.value = 0f;
		}
		
		Integer specificID = json.readValue("specificID", Integer.class, jsonData);
		if (specificID != null) {
			this.specificID = specificID.intValue();
		}
		else {
			this.specificID = defaultID;
		}
	}

	public void setBaseFieldsForSettings(EntityEffectSettings settings) {
		super.setBaseFieldsForSettings(settings);
		settings.value = this.value;
	}

	public int getSpecificID() {
		return specificID;
	}
}
