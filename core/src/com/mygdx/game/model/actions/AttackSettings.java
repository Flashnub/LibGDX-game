package com.mygdx.game.model.actions;

import java.util.ArrayList;

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

	}
	
	public void setFieldsWithAbilitySettings(AbilitySettings settings)
	{
		this.duration = settings.duration;
		this.cooldownTime = settings.cooldownTime;
		this.windupTime = settings.windupTime;
		this.sourceEffectSettings = settings.sourceEffectSettings;
		this.isPermanent = settings.isPermanent;
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
		Array<EffectSettings> newTargetSettings = new Array <EffectSettings> ();
		for (EffectSettings eSettings : this.targetEffectSettings) {
			newTargetSettings.add(eSettings.deepCopy());
		}
		copy.targetEffectSettings = newTargetSettings;
		return copy;
	}
}
