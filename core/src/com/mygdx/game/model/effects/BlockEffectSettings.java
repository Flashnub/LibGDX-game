package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class BlockEffectSettings extends EffectSettings{

	float perfectBlockTime;
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		perfectBlockTime = json.readValue("perfectBlockTime", Float.class, jsonData);
		this.setType(BlockEffect.type);
	}
	
	@Override
	public EffectSettings deepCopy() {
		BlockEffectSettings copy = new BlockEffectSettings();
		this.setBaseFieldsForSettings(copy);
		copy.perfectBlockTime = this.perfectBlockTime;
		return copy;	
	}

}
