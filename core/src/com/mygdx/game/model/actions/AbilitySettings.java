package com.mygdx.game.model.actions;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.effects.EffectSettings;

public class AbilitySettings implements Serializable {
	ArrayList<EffectSettings> sourceEffectSettings;
	Float windupTime;
	Float cooldownTime;
	Float duration;
	boolean isPermanent;
	
	@Override
	public void write(Json json) {
		json.writeValue("sourceEffectSettings", sourceEffectSettings);
		json.writeValue("windupTime", windupTime);
		json.writeValue("cooldownTime", cooldownTime);
		json.writeValue("duration", duration);		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		Boolean isPermanent = json.readValue("isPermanent", Boolean.class, jsonData);
		Float duration = json.readValue("duration", Float.class, jsonData);
		if (isPermanent != null && isPermanent.booleanValue()) {
			this.duration = Float.MAX_VALUE;
			this.isPermanent = isPermanent.booleanValue();
		}
		else if (duration != null) {
			this.duration = duration;
			this.isPermanent = false;
		}
		else {
			this.duration = 0.5f;
			this.isPermanent = false;
		}
		Float windupTime = json.readValue("windUpTime", Float.class, jsonData);
		if (windupTime != null) {
			this.windupTime = windupTime;
		}
		else {
			this.windupTime = 0f;
		}
		Float cooldownTime = json.readValue("cooldownTime", Float.class, jsonData);
		if (cooldownTime != null) {
			this.cooldownTime = cooldownTime;
		}
		else {
			this.cooldownTime = 0f;
		}
		sourceEffectSettings = json.readValue("sourceEffectSettings", ArrayList.class, jsonData);
	}

	public Float getDuration() {
		return duration;
	}

	public Float getWindUpTime() {
		return windupTime;
	}

	public Float getCooldownTime() {
		return cooldownTime;
	}

	public boolean isPermanent() {
		return isPermanent;
	}
	
}
