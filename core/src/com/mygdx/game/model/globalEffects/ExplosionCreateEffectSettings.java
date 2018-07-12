package com.mygdx.game.model.globalEffects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class ExplosionCreateEffectSettings extends WorldEffectSettings{

	String explosionKey;
	Vector2 origin;
	
	@Override
	public WorldEffectSettings deepCopy() {
		ExplosionCreateEffectSettings copy = new ExplosionCreateEffectSettings();
		this.setBaseFieldsForSettings(copy);
		copy.explosionKey = this.explosionKey;
		copy.origin = this.origin;
		return copy;
	}
	
	
	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue("origin", origin);
		json.writeValue("explosionKey", explosionKey);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		explosionKey = json.readValue("explosionKey", String.class, jsonData);
		Vector2 origin = json.readValue("origin", Vector2.class, jsonData);
		if (origin != null) {
			this.origin = origin; 
		}
		else {
			this.origin = new Vector2();
		}
		setType(ExplosionCreateEffect.type);
	}

}
