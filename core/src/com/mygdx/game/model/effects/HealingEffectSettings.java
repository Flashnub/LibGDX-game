package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class HealingEffectSettings extends EntityEffectSettings {
	
	public void fillInDefaults() {
		super.fillInDefaults();
		this.setType(HealingEffect.type);
	}
	
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		this.setType(HealingEffect.type);
	}
	
	@Override
	public HealingEffectSettings deepCopy() {
		HealingEffectSettings copy = new HealingEffectSettings();
		this.setBaseFieldsForSettings(copy);
		return copy;
	}
	
}
