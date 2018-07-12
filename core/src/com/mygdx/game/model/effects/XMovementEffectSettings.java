package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.conditions.InjuredStaggerConditionSettings;

public class XMovementEffectSettings extends EntityEffectSettings{
	float maxVelocity;
	float velocity;
	float acceleration;
	boolean applyOnlyIfInjuredStaggered;
	
	public void fillInDefaults() {
		super.fillInDefaults();
		maxVelocity = Float.MAX_VALUE;
		velocity = 0f;
		acceleration = 0f;
		setType(XMovementEffect.type);
		applyOnlyIfInjuredStaggered = false;
	}

	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue("velocity", velocity);
		json.writeValue("acceleration", acceleration);
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		velocity = json.readValue("velocity", Float.class, jsonData);
		acceleration = json.readValue("acceleration", Float.class, jsonData);
		
		Float maxVelocity = json.readValue("maxVelocityX", Float.class, jsonData);
		if (maxVelocity != null) {
			this.maxVelocity = maxVelocity;
		}
		else {
			this.maxVelocity = Float.MAX_VALUE;
		}

		this.setType(XMovementEffect.type);
		Boolean applyOnlyIfInjuredStaggered = json.readValue("applyOnlyIfInjuredStaggered", Boolean.class, jsonData);
		if (applyOnlyIfInjuredStaggered != null) {
			this.applyOnlyIfInjuredStaggered = applyOnlyIfInjuredStaggered.booleanValue();
		}
		else {
			this.applyOnlyIfInjuredStaggered = false;
		}
		if (this.applyOnlyIfInjuredStaggered) {
			this.passiveConditions.add(new InjuredStaggerConditionSettings());
		}
	}

	@Override
	public XMovementEffectSettings deepCopy() {
		XMovementEffectSettings copy = new XMovementEffectSettings();
		this.setBaseFieldsForSettings(copy);
		copy.velocity = this.velocity;
		copy.acceleration = this.acceleration;
		copy.maxVelocity = this.maxVelocity;
		copy.applyOnlyIfInjuredStaggered = this.applyOnlyIfInjuredStaggered;
		return copy;
	}
	
	
	public float getEstimatedDistance() {
		float xDistance = (velocity * getDuration()) + (0.5f * acceleration * getDuration() * getDuration());
		return xDistance;
	}

	public float getVelocity() {
		return velocity;
	}

	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}

	public float getAcceleration() {
		return acceleration;
	}

	public void setAcceleration(float acceleration) {
		this.acceleration = acceleration;
	}
	
}
