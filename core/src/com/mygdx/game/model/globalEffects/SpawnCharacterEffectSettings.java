package com.mygdx.game.model.globalEffects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class SpawnCharacterEffectSettings extends WorldEffectSettings {

	String characterName;
	String characterType;

	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue("characterName", characterName);
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		this.setType(SpawnCharacterEffect.type);
		this.characterName = json.readValue("characterName", String.class, jsonData);
		this.characterType = json.readValue("characterType", String.class, jsonData);
	}
	
	@Override
	public WorldEffectSettings deepCopy() {
		SpawnCharacterEffectSettings copy = new SpawnCharacterEffectSettings();
		this.setBaseFieldsForSettings(copy);
		copy.characterName = this.characterName;
		copy.characterType = this.characterType;
		return copy;
	}

}
