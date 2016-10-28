package com.mygdx.game.model.actions;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.effects.EffectSettings;

public class AbilitySettings implements Serializable {
	ArrayList<EffectSettings> sourceEffectSettings;
	float delayToActivate;
	Float duration;
    public boolean isConcurrent;
	
	@Override
	public void write(Json json) {
		json.writeValue("sourceEffectSettings", sourceEffectSettings);
		json.writeValue("delayToActivate", delayToActivate);
		json.writeValue("duration", duration);		
		json.writeValue("isConcurrent", isConcurrent);		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		Float duration = json.readValue("duration", Float.class, jsonData);
		if (duration != null) {
			this.duration = duration;
		}
		else {
			this.duration = 0.5f;
		}
		Float delayToActivate = json.readValue("delayToActivate", Float.class, jsonData);
		if (delayToActivate != null) {
			this.delayToActivate = delayToActivate;
		}
		else {
			this.delayToActivate = 0f;
		}
		sourceEffectSettings = json.readValue("sourceEffectSettings", ArrayList.class, jsonData);
		Boolean concurrency = json.readValue("isConcurrent", Boolean.class, jsonData);
		if (concurrency != null) {
			isConcurrent = concurrency;
		}
		else {
			isConcurrent = false;
		}
	}

	public float getDelayToActivate() {
		return delayToActivate;
	}

	public Float getDuration() {
		return duration;
	}
	
	
}
