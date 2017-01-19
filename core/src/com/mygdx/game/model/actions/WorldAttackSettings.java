package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.globalEffects.WorldEffectSettings;

public class WorldAttackSettings implements Serializable{
	String abilitySettingKey;
	Array <WorldEffectSettings> worldEffectSettings;
	AbilitySettings abilitySettings;
	
	public WorldAttackSettings() {
		
	}
	
	@Override
	public void write(Json json) {
		json.writeValue("abilitySettingKey", abilitySettingKey);
		json.writeValue("worldEffectSettings", worldEffectSettings);		
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		abilitySettingKey = json.readValue("abilitySettingKey", String.class, jsonData);
		worldEffectSettings = json.readValue("worldEffectSettings", Array.class, jsonData);
		if (abilitySettingKey != null) {
			abilitySettings = JSONController.abilities.get(abilitySettingKey).deepCopy();
		}
		
	}

	public AbilitySettings getAbilitySettings() {
		return abilitySettings;
	}

	public WorldAttackSettings deepCopy() {
		WorldAttackSettings copy = new WorldAttackSettings();
		copy.abilitySettings = this.abilitySettings.deepCopy();
		copy.abilitySettingKey = this.abilitySettingKey;
		Array <WorldEffectSettings> newCopy = new Array <WorldEffectSettings>();
		for (WorldEffectSettings worldEffect : this.worldEffectSettings) {
			newCopy.add((WorldEffectSettings) worldEffect.deepCopy());
		}
		copy.worldEffectSettings = newCopy;
		return copy;
	}
}
