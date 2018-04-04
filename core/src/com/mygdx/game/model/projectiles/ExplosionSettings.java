package com.mygdx.game.model.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.assets.HitSparkUtils;
import com.mygdx.game.model.actions.AbilitySettings;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.hitSpark.HitSparkData;

public class ExplosionSettings extends AbilitySettings implements Serializable{
	
	Array <EffectSettings> targetEffects;
	Vector2 origin;
	float widthCoefficient;
	float heightCoefficient;
	float hitRate;
	Float windupTime;
	Float cooldownTime;
	protected Float duration;
	HitSparkData hitSparkData;
	
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
		copy.windupTime = this.windupTime;
		copy.duration = this.duration;
		copy.cooldownTime = this.cooldownTime;
		copy.hitSparkData = this.hitSparkData;
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
		
		Float duration = json.readValue("duration", Float.class, jsonData);
		if (this.activeTillDisruption()) {
			this.duration = Float.MAX_VALUE;
		}
		else if (duration != null) {
			this.duration = duration;
		}
		else {
			this.duration = 0.5f;
		}
		Float windupTime = json.readValue("windUpTime", Float.class, jsonData);
		if (this.windupTillDisruption()) {
			this.windupTime = Float.MAX_VALUE;
		}
		else if (windupTime != null) {
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
		this.hitSparkData = json.readValue("hitSparkData", HitSparkData.class, jsonData);
		if (this.hitSparkData == null) {
			this.hitSparkData = HitSparkUtils.defaultData();
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

	public HitSparkData getHitSparkData() {
		return this.hitSparkData;
	}

}
