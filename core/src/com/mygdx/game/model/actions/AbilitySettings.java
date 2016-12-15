package com.mygdx.game.model.actions;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.MovementEffectSettings;

public class AbilitySettings implements Serializable {
	Array<EffectSettings> sourceEffectSettings;
	Array <EffectSettings> windupEffectSettings;
	Float windupTime;
	Float cooldownTime;
	Float duration;
	boolean isPermanent;
	
	@Override
	public void write(Json json) {
		json.writeValue("sourceEffectSettings", sourceEffectSettings);
		json.writeValue("windupEffectSettings", windupEffectSettings);
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
		sourceEffectSettings = json.readValue("sourceEffectSettings", Array.class, jsonData);
		windupEffectSettings = json.readValue("windupEffectSettings", Array.class, jsonData);
		if (windupEffectSettings == null) {
			windupEffectSettings = new Array <EffectSettings> ();
		}
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
		
	public void replaceMovementIfNecessary(MovementEffectSettings movementSettings) {
		if (movementSettings != null) {
			Iterator <EffectSettings> iterator = this.sourceEffectSettings.iterator();
			boolean hasMovement = false;
			while (iterator.hasNext()) {
				EffectSettings settings = iterator.next();
				if (settings instanceof MovementEffectSettings) {
					iterator.remove();
					hasMovement = true;
					break;
				}
			}
			if (hasMovement) {
				this.sourceEffectSettings.add(movementSettings);
			}
		}
	}
	
	public AbilitySettings deepCopy() {
		AbilitySettings copy = new AbilitySettings();
		copy.windupTime = this.windupTime;
		copy.cooldownTime = this.cooldownTime;
		copy.isPermanent = this.isPermanent;
		copy.duration = this.duration;
		Array <EffectSettings> effectSettingsCopy = new Array <EffectSettings> ();
		for (EffectSettings eSettings : this.sourceEffectSettings) {
			effectSettingsCopy.add(eSettings.deepCopy());
		}
		copy.sourceEffectSettings = effectSettingsCopy;
		
		Array <EffectSettings> effectSettingsCopy2 = new Array <EffectSettings> ();
		for (EffectSettings eSettings : this.windupEffectSettings) {
			effectSettingsCopy2.add(eSettings.deepCopy());
		}
		copy.windupEffectSettings = effectSettingsCopy2;
		return copy;
	}
	
}
