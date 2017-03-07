package com.mygdx.game.model.actions;

import java.util.Iterator;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.XMovementEffectSettings;
import com.mygdx.game.model.effects.YMovementEffectSettings;

public class AbilitySettings implements Serializable {
	Array <EffectSettings> sourceEffectSettings;
	Array <EffectSettings> windupEffectSettings;
	
//	Float windupTime;
//	Float cooldownTime;
//	protected Float duration;
	boolean activeTillDisruption;
	boolean windupTillDisruption;
	boolean sourceRespectEntityCollisions; //if this is false, the action ignores all entity collisions.
	boolean chainsWithJump;
	boolean isSuper;
	Float tempWidthModifier;
	Float tempHeightModifier;
	Float xOffsetModifier;
	Float yOffsetModifier;
	String name;

	@Override
	public void write(Json json) {
		json.writeValue("sourceEffectSettings", sourceEffectSettings);
		json.writeValue("windupEffectSettings", windupEffectSettings);
//		json.writeValue("windupTime", windupTime);
//		json.writeValue("cooldownTime", cooldownTime);
//		json.writeValue("duration", duration);		
		json.writeValue("sourceRespectEntityCollisions", sourceRespectEntityCollisions);
		json.writeValue("name", name);
		json.writeValue("isSuper", isSuper);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		tempWidthModifier = json.readValue("tempWidthModifier", Float.class, jsonData);
		tempHeightModifier = json.readValue("tempHeightModifier", Float.class, jsonData);
		Float xOffsetModifier = json.readValue("xOffsetModifier", Float.class, jsonData);
		name = json.readValue("name", String.class, jsonData);
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
		this.activeTillDisruption = activeTillDisruption != null ? activeTillDisruption.booleanValue() : false;
//		Float duration = json.readValue("duration", Float.class, jsonData);
//		if (activeTillDisruption != null && activeTillDisruption.booleanValue()) {
//			this.duration = Float.MAX_VALUE;
//			this.activeTillDisruption = activeTillDisruption.booleanValue();
//		}
//		else if (duration != null) {
//			this.duration = duration;
//			this.activeTillDisruption = false;
//		}
//		else {
//			this.duration = 0.5f;
//			this.activeTillDisruption = false;
//		}
		Boolean windupTillDuration = json.readValue("windupTillDuration", Boolean.class, jsonData);
		this.windupTillDisruption = windupTillDuration != null ? windupTillDuration.booleanValue() : false;
		
		Boolean isSuper = json.readValue("isSuper", Boolean.class, jsonData);
		this.isSuper = isSuper != null ? isSuper.booleanValue() : false;
//		Float windupTime = json.readValue("windUpTime", Float.class, jsonData);
//		if (windupTillDuration != null && windupTillDuration.booleanValue()) {
//			this.windupTime = Float.MAX_VALUE;
//			this.windupTillDisruption = windupTillDuration.booleanValue();
//		}
//		else if (windupTime != null) {
//			this.windupTime = windupTime;
//		}
//		else {
//			this.windupTime = 0f;
//		}
//		Float cooldownTime = json.readValue("cooldownTime", Float.class, jsonData);
//		if (cooldownTime != null) {
//			this.cooldownTime = cooldownTime;
//		}
//		else {
//			this.cooldownTime = 0f;
//		}
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
		
		Boolean chainsWithJump = json.readValue("chainsWithJump", Boolean.class, jsonData);
		if (chainsWithJump != null) {
			this.chainsWithJump = chainsWithJump;
		}
		else {
			this.chainsWithJump = true;
		}	
	}

	public boolean activeTillDisruption() {
		return activeTillDisruption;
	}
	
	public boolean windupTillDisruption() {
		return this.windupTillDisruption;
	}
		
	public void replaceXMovementIfNecessary(XMovementEffectSettings xMovementSettings) {
		if (xMovementSettings != null) {
			Iterator <EffectSettings> iterator = this.sourceEffectSettings.iterator();
			while (iterator.hasNext()) {
				EffectSettings settings = iterator.next();
				if (settings instanceof XMovementEffectSettings) {
					iterator.remove();
					break;
				}
			}
			this.sourceEffectSettings.add(xMovementSettings);	
		}
	}
	
	public void replaceYMovementIfNecessary(YMovementEffectSettings yMovementSettings) {
		if (yMovementSettings != null) {
			Iterator <EffectSettings> iterator = this.sourceEffectSettings.iterator();
			while (iterator.hasNext()) {
				EffectSettings settings = iterator.next();
				if (settings instanceof YMovementEffectSettings) {
					iterator.remove();
					break;
				}
			}
			this.sourceEffectSettings.add(yMovementSettings);
		}
	}
	
	public void setFieldsWithAbilitySettings(AbilitySettings settings)
	{
//		this.duration = settings.duration;
//		this.cooldownTime = settings.cooldownTime;
//		this.windupTime = settings.windupTime;
		if (settings.sourceEffectSettings != null) {
			Array <EffectSettings> effectSettingsCopy = new Array <EffectSettings> ();
			for (EffectSettings eSettings : settings.sourceEffectSettings) {
				effectSettingsCopy.add(eSettings.deepCopy());
			}
			this.sourceEffectSettings = effectSettingsCopy;
		}

		if (settings.windupEffectSettings != null) {
			Array <EffectSettings> effectSettingsCopy2 = new Array <EffectSettings> ();
			for (EffectSettings eSettings : settings.windupEffectSettings) {
				effectSettingsCopy2.add(eSettings.deepCopy());
			}
			this.windupEffectSettings = effectSettingsCopy2;
		}

		this.activeTillDisruption = settings.activeTillDisruption;
		this.windupTillDisruption = settings.windupTillDisruption;
		this.sourceRespectEntityCollisions = settings.sourceRespectEntityCollisions;
		this.tempHeightModifier = settings.tempHeightModifier;
		this.tempWidthModifier = settings.tempWidthModifier;
		this.xOffsetModifier = settings.xOffsetModifier;
		this.yOffsetModifier = settings.yOffsetModifier;
		this.name = settings.name;
		this.chainsWithJump = settings.chainsWithJump;
		this.isSuper = settings.isSuper;
	}
	
	public AbilitySettings deepCopy() {
		AbilitySettings copy = new AbilitySettings();
		copy.name = this.name;
//		copy.windupTime = this.windupTime;
//		copy.cooldownTime = this.cooldownTime;
		copy.activeTillDisruption = this.activeTillDisruption;
		copy.windupTillDisruption = this.windupTillDisruption;
		copy.chainsWithJump = this.chainsWithJump;
//		copy.duration = this.duration;
		copy.tempHeightModifier = this.tempHeightModifier;
		copy.tempWidthModifier = this.tempWidthModifier;
		copy.xOffsetModifier = this.xOffsetModifier;
		copy.yOffsetModifier = this.yOffsetModifier;
		copy.isSuper = this.isSuper;
		copy.sourceEffectSettings = this.sourceEffectSettings;
		copy.sourceRespectEntityCollisions = this.sourceRespectEntityCollisions;
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

	public void setName(String name) {
		this.name = name;
	}
}
