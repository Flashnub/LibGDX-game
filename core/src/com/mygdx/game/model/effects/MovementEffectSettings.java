package com.mygdx.game.model.effects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.Effect.EffectType;

public class MovementEffectSettings extends EffectSettings {		
	Vector2 velocity;
	Vector2 acceleration;

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
		this.type = EffectType.MOVEMENT;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public Vector2 getAcceleration() {
		return acceleration;
	}
	
}