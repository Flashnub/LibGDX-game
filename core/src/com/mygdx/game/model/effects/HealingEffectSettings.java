package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.effects.Effect.EffectType;

public class HealingEffectSettings extends EffectSettings {
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		this.type = EffectType.HEALING;
	}
}
