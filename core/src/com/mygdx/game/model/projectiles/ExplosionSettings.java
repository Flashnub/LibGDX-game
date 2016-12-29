package com.mygdx.game.model.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.actions.AbilitySettings;
import com.mygdx.game.model.effects.EffectSettings;

public class ExplosionSettings extends AbilitySettings implements Serializable{
	
	Array <EffectSettings> targetEffects;
	Vector2 origin;
	float widthCoefficient;
	float heightCoefficient;
	float hitRate;
	
	public ExplosionSettings deepCopy() {
		ExplosionSettings copy = new ExplosionSettings();
		copy.setFieldsWithAbilitySettings(super.deepCopy());
		copy.widthCoefficient = this.widthCoefficient;
		copy.heightCoefficient = this.heightCoefficient;
		copy.hitRate = this.hitRate;
		Array <EffectSettings> effectCopy = new Array<EffectSettings>();
		for (EffectSettings effectSettings : this.targetEffects) {
			effectCopy.add(effectSettings.deepCopy());
		}
		copy.targetEffects = effectCopy;
		copy.origin = this.origin;
		return copy;
	}
	
	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue("hitRate", hitRate);
		json.writeValue("widthCoefficient", widthCoefficient);
		json.writeValue("heightCoefficient", heightCoefficient);
		json.writeValue("targetEffects", targetEffects);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		targetEffects = json.readValue("targetEffects", Array.class, jsonData);	
		Float hitRate = json.readValue("hitRate", Float.class, jsonData);
		if (hitRate != null) {
			this.hitRate = hitRate;
		}
		else {
			this.hitRate = Float.MAX_VALUE;
		}
		
		Float widthCoefficient = json.readValue("widthCoefficient", Float.class, jsonData);
		if (widthCoefficient != null) {
			this.widthCoefficient = widthCoefficient;
		}
		else {
			this.widthCoefficient = 1f;
		}
		
		Float heightCoefficient = json.readValue("heightCoefficient", Float.class, jsonData);
		if (heightCoefficient != null) {
			this.heightCoefficient = heightCoefficient;
		}
		else {
			this.heightCoefficient = 1f;
		}
	}
	
	public Vector2 getOrigin() {
		return origin;
	}

	public void setOrigin(Vector2 origin) {
		this.origin = origin;
	}

	public Array<EffectSettings> getTargetEffects() {
		return targetEffects;
	}

	public float getWidthCoefficient() {
		return widthCoefficient;
	}

	public float getHeightCoefficient() {
		return heightCoefficient;
	}

}
