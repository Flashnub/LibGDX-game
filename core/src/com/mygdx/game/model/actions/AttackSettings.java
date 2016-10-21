package com.mygdx.game.model.actions;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.effects.EffectSettings;

public class AttackSettings extends AbilitySettings{
	float originX;
	float originY;
	float width;
	float height;
	float damageTickRate;
	ArrayList<EffectSettings> targetEffectSettings;
	
	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		super.write(json);
		json.writeValue("damageTickRate", damageTickRate);
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
		targetEffectSettings = json.readValue("targetEffectSettings", ArrayList.class, jsonData);
		
		
		Float tickRate = json.readValue("damageTickRate", Float.class, jsonData);
		if (tickRate != null) {
			damageTickRate = tickRate.floatValue();
		}
		else {
			damageTickRate = 0.45f;
		}

	}
	
}
