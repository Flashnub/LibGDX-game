package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.effects.EffectSettings;

public class AttackSettings extends AbilitySettings{
	float originX;
	float originY;
	float width;
	float height;
	float hitRate;
	Array<EffectSettings> targetEffectSettings;
	boolean shouldStagger;
	boolean targetRespectEntityCollisions; //if this is false, the action ignores all entity collisions.

	
	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		super.write(json);
		json.writeValue("hitRate", hitRate);
		json.writeValue("width", width);
		json.writeValue("height", height);
		json.writeValue("originX", originX);
		json.writeValue("originY", originY);		
		json.writeValue("targetEffectSettings", targetEffectSettings);
		json.writeValue("shouldStagger", shouldStagger);
		json.writeValue("targetRespectEntityCollisions", targetRespectEntityCollisions);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		super.read(json, jsonData);
		width = json.readValue("width", Float.class, jsonData);
		height = json.readValue("height", Float.class, jsonData);
		originX = json.readValue("originX", Float.class, jsonData);
		originY = json.readValue("originY", Float.class, jsonData);
		targetEffectSettings = json.readValue("targetEffectSettings", Array.class, jsonData);
		
		
		Float tickRate = json.readValue("hitRate", Float.class, jsonData);
		if (tickRate != null) {
			hitRate = tickRate.floatValue();
		}
		else {
			hitRate = Float.MAX_VALUE;
		}
		
		Boolean shouldStagger = json.readValue("shouldStagger", Boolean.class, jsonData);
		if (shouldStagger != null) {
			this.shouldStagger = shouldStagger;
		}
		else {
			this.shouldStagger = true;
		}
		
		Boolean targetRespectEntityCollisions = json.readValue("targetRespectEntityCollisions", Boolean.class, jsonData);
		if (targetRespectEntityCollisions != null) {
			this.targetRespectEntityCollisions = targetRespectEntityCollisions;
		}
		else {
			this.targetRespectEntityCollisions = true;
		}

	}
	
	@Override
	public AttackSettings deepCopy() {
		AttackSettings copy = new AttackSettings();
		copy.setFieldsWithAbilitySettings(super.deepCopy());
		copy.originX = this.originX;
		copy.originY = this.originY;
		copy.hitRate = this.hitRate;
		copy.width = this.width;
		copy.height = this.height;
		copy.shouldStagger = this.shouldStagger;
		Array<EffectSettings> newTargetSettings = new Array <EffectSettings> ();
		for (EffectSettings eSettings : this.targetEffectSettings) {
			newTargetSettings.add(eSettings.deepCopy());
		}
		copy.targetEffectSettings = newTargetSettings;
		return copy;
	}

	public Array<EffectSettings> getTargetEffectSettings() {
		return targetEffectSettings;
	}

	public boolean isShouldStagger() {
		return shouldStagger;
	}
}
