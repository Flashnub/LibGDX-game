package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public abstract class EntityEffectSettings extends EffectSettings{
	
	int value;
	
	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue("value", value);

	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		value = json.readValue("value", Integer.class, jsonData);

	}

	public void setBaseFieldsForSettings(EntityEffectSettings settings) {
		super.setBaseFieldsForSettings(settings);
		settings.value = this.value;
	}
}
