package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.conditions.InjuredStaggerConditionSettings;

public class XMovementEffectSettings extends EntityEffectSettings{
	float maxVelocity;
	float velocity;
	float acceleration;
	boolean startWithStagger;
	boolean applyOnlyIfInjuredStaggered;

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
		startWithStagger = false;
		Boolean applyOnlyIfInjuredStaggered = json.readValue("applyOnlyIfInjuredStaggered", Boolean.class, jsonData);
		if (applyOnlyIfInjuredStaggered != null) {
			this.applyOnlyIfInjuredStaggered = applyOnlyIfInjuredStaggered.booleanValue();
		}
		else {
			this.applyOnlyIfInjuredStaggered = true;
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

	public boolean isStartWithStagger() {
		return startWithStagger;
	}

	public void setStartWithStagger(boolean startWithStagger) {
		this.startWithStagger = startWithStagger;
	}
	
	
}
