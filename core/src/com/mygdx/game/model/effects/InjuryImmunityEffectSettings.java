package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class InjuryImmunityEffectSettings extends EntityEffectSettings{
	
	public void fillInDefaults() {
		super.fillInDefaults();
		this.setType(InjuryImmunityEffect.type);
	}
	
	@Override
	public EffectSettings deepCopy() {
		InjuryImmunityEffectSettings copy = new InjuryImmunityEffectSettings();
		this.setBaseFieldsForSettings(copy);
		return copy;
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		this.setType(InjuryImmunityEffect.type);
	}
}
