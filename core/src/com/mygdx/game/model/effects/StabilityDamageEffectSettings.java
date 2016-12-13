package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.effects.Effect.EffectType;

public class StabilityDamageEffectSettings extends EffectSettings{
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		this.type = EffectType.STABILITYDMG;
	}
	
	@Override
	public StabilityDamageEffectSettings deepCopy() {
		StabilityDamageEffectSettings copy = new StabilityDamageEffectSettings();
		this.setBaseFieldsForSettings(copy);
		return copy;
	}
}

