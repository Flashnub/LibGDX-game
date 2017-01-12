package com.mygdx.game.model.actions;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.MovementEffectSettings;

public class AbilitySettings implements Serializable {
	Array <EffectSettings> sourceEffectSettings;
	Array <EffectSettings> windupEffectSettings;
	
	Float windupTime;
	Float cooldownTime;
	protected Float duration;
	boolean activeTillDisruption;
	boolean sourceRespectEntityCollisions; //if this is false, the action ignores all entity collisions.
	Float tempWidthModifier;
	Float tempHeightModifier;
	Float xOffsetModifier;
	Float yOffsetModifier;

	@Override
	public void write(Json json) {
		json.writeValue("sourceEffectSettings", sourceEffectSettings);
		json.writeValue("windupEffectSettings", windupEffectSettings);
		json.writeValue("windupTime", windupTime);
		json.writeValue("cooldownTime", cooldownTime);
		json.writeValue("duration", duration);		
		json.writeValue("sourceRespectEntityCollisions", sourceRespectEntityCollisions);
		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		tempWidthModifier = json.readValue("tempWidthModifier", Float.class, jsonData);
		tempHeightModifier = json.readValue("tempHeightModifier", Float.class, jsonData);
		Float xOffsetModifier = json.readValue("xOffsetModifier", Float.class, jsonData);
		if (xOffsetModifier != null) {
			this.xOffsetModifier = xOffsetModifier;
		}
		else {
			this.xOffsetModifier = 0f;
		}
		
		Float yOffsetModifier = json.readValue("yOffsetModifier", Float.class, jsonData);
		if (yOffsetModifier != null) {
			this.yOffsetModifier = yOffsetModifier;
		}
		else {
			this.yOffsetModifier = 0f;
		}
		
		Boolean activeTillDisruption = json.readValue("activeTillDisruption", Boolean.class, jsonData);
		Float duration = json.readValue("duration", Float.class, jsonData);
		if (activeTillDisruption != null && activeTillDisruption.booleanValue()) {
			this.duration = Float.MAX_VALUE;
			this.activeTillDisruption = activeTillDisruption.booleanValue();
		}
		else if (duration != null) {
			this.duration = duration;
			this.activeTillDisruption = false;
		}
		else {
			this.duration = 0.5f;
			this.activeTillDisruption = false;
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
		if (sourceEffectSettings == null) {
			sourceEffectSettings = new Array <EffectSettings> ();
		}
		
		windupEffectSettings = json.readValue("windupEffectSettings", Array.class, jsonData);
		if (windupEffectSettings == null) {
			windupEffectSettings = new Array <EffectSettings> ();
		}
		
		Boolean sourceRespectEntityCollisions = json.readValue("sourceRespectEntityCollisions", Boolean.class, jsonData);
		if (sourceRespectEntityCollisions != null) {
			this.sourceRespectEntityCollisions = sourceRespectEntityCollisions;
		}
		else {
			this.sourceRespectEntityCollisions = true;
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

	public Float getWindUpPlusDuration() {
		return this.windupTime + this.duration;
	}
	
	public Float getTotalTime() {
		return this.windupTime + this.duration + this.cooldownTime;
	}
	
	public boolean activeTillDisruption() {
		return activeTillDisruption;
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
	
	public void setFieldsWithAbilitySettings(AbilitySettings settings)
	{
		this.duration = settings.duration;
		this.cooldownTime = settings.cooldownTime;
		this.windupTime = settings.windupTime;
		this.sourceEffectSettings = settings.sourceEffectSettings;
		this.windupEffectSettings = settings.windupEffectSettings;
		this.activeTillDisruption = settings.activeTillDisruption;
		this.sourceRespectEntityCollisions = settings.sourceRespectEntityCollisions;
		this.tempHeightModifier = settings.tempHeightModifier;
		this.tempWidthModifier = settings.tempWidthModifier;
		this.xOffsetModifier = settings.xOffsetModifier;
		this.yOffsetModifier = settings.yOffsetModifier;
	}
	
	public AbilitySettings deepCopy() {
		AbilitySettings copy = new AbilitySettings();
		copy.windupTime = this.windupTime;
		copy.cooldownTime = this.cooldownTime;
		copy.activeTillDisruption = this.activeTillDisruption;
		copy.duration = this.duration;
		copy.tempHeightModifier = this.tempHeightModifier;
		copy.tempWidthModifier = this.tempWidthModifier;
		copy.xOffsetModifier = this.xOffsetModifier;
		copy.yOffsetModifier = this.yOffsetModifier;
		if (sourceEffectSettings != null) {
			Array <EffectSettings> effectSettingsCopy = new Array <EffectSettings> ();
			for (EffectSettings eSettings : this.sourceEffectSettings) {
				effectSettingsCopy.add(eSettings.deepCopy());
			}
			copy.sourceEffectSettings = effectSettingsCopy;
		}

		if (windupEffectSettings != null) {
			Array <EffectSettings> effectSettingsCopy2 = new Array <EffectSettings> ();
			for (EffectSettings eSettings : this.windupEffectSettings) {
				effectSettingsCopy2.add(eSettings.deepCopy());
			}
			copy.windupEffectSettings = effectSettingsCopy2;
		}

		return copy;
	}

	public Float getTempWidthGameplayHitBoxModifier() {
		return tempWidthModifier;
	}

	public Float getTempHeightGameplayHitBoxModifier() {
		return tempHeightModifier;
	}
	
}
