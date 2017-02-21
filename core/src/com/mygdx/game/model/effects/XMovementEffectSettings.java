package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class XMovementEffectSettings extends EntityEffectSettings{
	float maxVelocity;
	float velocity;
	float acceleration;
	boolean startWithStagger;

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
	}

	@Override
	public EffectSettings deepCopy() {
		XMovementEffectSettings copy = new XMovementEffectSettings();
		this.setBaseFieldsForSettings(copy);
		copy.velocity = this.velocity;
		copy.acceleration = this.acceleration;
		copy.maxVelocity = this.maxVelocity;
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
