package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class TensionAddEffectSettings extends EntityEffectSettings{

	@Override
	public EffectSettings deepCopy() {
		TensionAddEffectSettings copy = new TensionAddEffectSettings();
		this.setBaseFieldsForSettings(copy);
		return copy;
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		this.setType(TensionAddEffect.type);
	}
}
