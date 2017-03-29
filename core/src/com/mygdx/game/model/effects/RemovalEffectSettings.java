package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class RemovalEffectSettings extends EntityEffectSettings{

	Integer idToRemove;
	
	public void fillInDefaults() {
		super.fillInDefaults();
		this.setType(RemovalEffect.type);
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		json.readValue("idToRemove", Integer.class, jsonData);
		this.setDuration(0.01f);
		this.setDelayToActivate(0f);
		this.setType(RemovalEffect.type);
	}
	
	@Override
	public RemovalEffectSettings deepCopy() {
		RemovalEffectSettings copy = new RemovalEffectSettings();
		this.setBaseFieldsForSettings(copy);
		return copy;
	}

	public Integer getIdToRemove() {
		return idToRemove;
	}
}
