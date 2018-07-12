package com.mygdx.game.model.actions;

import com.mygdx.game.model.characters.Character.CharacterModel;

public abstract class ActionCondition {
	
	ActionConditionSettings settings;
	CharacterModel source;
	
	public ActionCondition(ActionConditionSettings settings, CharacterModel source) {
		this.settings = settings;
		this.source = source;
	}

	public abstract boolean isConditionMet();

	
}
