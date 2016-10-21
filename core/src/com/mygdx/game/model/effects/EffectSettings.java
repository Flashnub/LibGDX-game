package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json.Serializable;
import com.mygdx.game.model.effects.Effect.EffectType;

public class EffectSettings implements Serializable {
	int value;
	Float duration;
	Boolean isInstantaneous;
	float delayToActivate;
	EffectType type;
	
	@Override
	public void write(Json json) {
		json.writeValue("isInstantaneous", isInstantaneous);
		json.writeValue("duration", duration);
		json.writeValue("delayToActivate", delayToActivate);
		json.writeValue("value", value);
	}
	
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		isInstantaneous = json.readValue("isInstantaneous", Boolean.class, jsonData);
		if (isInstantaneous == null || isInstantaneous == true) {
			isInstantaneous = true;
			duration = 0f;
			delayToActivate = 0f;
		}
		else {
			isInstantaneous = false;
			duration = json.readValue("duration", Float.class, jsonData);
			Float delayToActivate = json.readValue("delayToActivate", Float.class, jsonData);
			if (delayToActivate != null) {
				this.delayToActivate = delayToActivate;
			}
			else 
			{
				this.delayToActivate = 0f;
			}
		}
		value = json.readValue("value", Integer.class, jsonData);
	}
	
}