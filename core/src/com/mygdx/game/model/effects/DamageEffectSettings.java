package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class DamageEffectSettings extends EffectSettings implements WillGenerator {
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		this.setType(DamageEffect.type);
	}

	@Override
	public DamageEffectSettings deepCopy() {
		DamageEffectSettings copy = new DamageEffectSettings();
		this.setBaseFieldsForSettings(copy);
		return copy;
	}

	@Override
	public float getPotentialWill() {
		return this.value / 2;
	}
} 