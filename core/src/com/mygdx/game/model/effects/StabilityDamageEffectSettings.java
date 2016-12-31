package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class StabilityDamageEffectSettings extends EntityEffectSettings implements WillGenerator{
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		this.setType(StabilityDamageEffect.type);
	}
	
	@Override
	public StabilityDamageEffectSettings deepCopy() {
		StabilityDamageEffectSettings copy = new StabilityDamageEffectSettings();
		this.setBaseFieldsForSettings(copy);
		return copy;
	}

	@Override
	public float getPotentialWill() {
		return this.value / 4;
	}
	
	
}

