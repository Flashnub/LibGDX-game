package com.mygdx.game.model.globalEffects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class DeleteCharacterEffectSettings extends WorldEffectSettings {

	@Override
	public WorldEffectSettings deepCopy() {
		DeleteCharacterEffectSettings copy = new DeleteCharacterEffectSettings();
		this.setBaseFieldsForSettings(copy);
		return copy;
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		this.setType(DeleteCharacterEffect.type);
	}
}
