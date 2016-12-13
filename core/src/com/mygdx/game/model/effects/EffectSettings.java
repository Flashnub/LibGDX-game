package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json.Serializable;
import com.mygdx.game.model.effects.Effect.EffectType;

public abstract class EffectSettings implements Serializable {
	int value;
	Float duration;
	Boolean isInstantaneous;
	float delayToActivate;
	Boolean isPermanent;
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
		if (isInstantaneous == null || isInstantaneous.booleanValue() == true) {
			isInstantaneous = true;
			duration = 0f;
			delayToActivate = 0f;
			isPermanent = false;
		}
		else {
			isInstantaneous = false;
			Boolean isPermanent = json.readValue("isPermanent", Boolean.class, jsonData);
			Float duration = json.readValue("duration", Float.class, jsonData);
			if (isPermanent != null && isPermanent.booleanValue()) {
				this.duration = Float.MAX_VALUE;
				this.isPermanent = true;
			}
			else if (duration != null) {
				this.duration = duration;
			}
			else {
				this.duration = 0.5f;
			}
			
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
	
	public abstract EffectSettings deepCopy();	
	
	public void setBaseFieldsForSettings(EffectSettings settings) {
		settings.duration = this.duration;
		settings.delayToActivate = this.delayToActivate;
		settings.isInstantaneous = this.isInstantaneous;
		settings.isPermanent = this.isPermanent;
		settings.type = this.type;
		settings.value = this.value;
	}
}