package com.mygdx.game.model.conditions;

import com.mygdx.game.model.characters.Character.CharacterModel;

public abstract class PassiveCondition {
	CharacterModel source;
	PassiveConditionSettings settings;
	
	public PassiveCondition (CharacterModel source, PassiveConditionSettings settings) {
		this.source = source;
		this.settings = settings;
	}
	
	public abstract boolean isConditionMet();
	
	public boolean endIfNotMet() {
		return settings.endIfNotMet;
	}
}
