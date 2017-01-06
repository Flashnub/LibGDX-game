package com.mygdx.game.model.effects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class MovementEffectSettings extends EntityEffectSettings {	
	Vector2 maxVelocity;
	Vector2 velocity;
	Vector2 acceleration;
	boolean useGravity;
	boolean respectEntityCollision;

	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue("velocity", velocity);
		json.writeValue("acceleration", acceleration);
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		velocity = json.readValue("velocity", Vector2.class, jsonData);
		acceleration = json.readValue("acceleration", Vector2.class, jsonData);
		
		Boolean useGravity = json.readValue("useGravity", Boolean.class, jsonData);
		if (useGravity != null) {
			this.useGravity = useGravity.booleanValue();
		}
		else {
			this.useGravity = true;
		}
		
		Float maxVelocityX = json.readValue("maxVelocityX", Float.class, jsonData);
		Float maxVelocityY = json.readValue("maxVelocityY", Float.class, jsonData);
		
		if (maxVelocityX == null) {
			maxVelocityX = Float.MAX_VALUE;
		}
		if (maxVelocityY == null) {
			maxVelocityY = Float.MAX_VALUE;
		}
		maxVelocity = new Vector2(maxVelocityX, maxVelocityY);
		
		this.setType(MovementEffect.type);
		this.respectEntityCollision = true;
		
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public Vector2 getAcceleration() {
		return acceleration;
	}
	
	public Vector2 getMaxVelocity() {
		return maxVelocity;
	}
	
	public boolean shouldRespectEntityCollision() {
		return respectEntityCollision;
	}

	public float getEstimatedDistance() {
		float xDistance = (velocity.x * getDuration()) + (0.5f * acceleration.x * getDuration() * getDuration());
		float yDistance = (velocity.y * getDuration()) + (0.5f * acceleration.y * getDuration() * getDuration());
		
		return (float) Math.sqrt(((xDistance * xDistance) + (yDistance * yDistance)));
	}
	
	@Override
	public MovementEffectSettings deepCopy() {
		MovementEffectSettings copy = new MovementEffectSettings();
		this.setBaseFieldsForSettings(copy);
		copy.velocity = this.velocity;
		copy.acceleration = this.acceleration;
		copy.maxVelocity = this.maxVelocity;
		copy.useGravity = this.useGravity;
		copy.respectEntityCollision = this.respectEntityCollision;
		return copy;
	}

	public void setShouldRespectEntityCollision(boolean respectEntityCollision) {
		this.respectEntityCollision = respectEntityCollision;
	}

}