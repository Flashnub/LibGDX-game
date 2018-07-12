package com.mygdx.game.model.actions;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class SprintActionCondition extends ActionCondition{
	
	SprintActionConditionSettings sSettings;
	public static final String type = "SprintActionCondition";

	public SprintActionCondition(ActionConditionSettings settings, CharacterModel source) {
		super(settings, source);
		if (settings instanceof SprintActionConditionSettings) {
			this.sSettings = (SprintActionConditionSettings) settings;
		}
	}

	@Override
	public boolean isConditionMet() {
		return source.isSprinting() == sSettings.onlyIfSprinting;
	}

}
