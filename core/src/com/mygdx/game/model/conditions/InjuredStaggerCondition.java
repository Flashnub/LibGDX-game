package com.mygdx.game.model.conditions;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class InjuredStaggerCondition extends PassiveCondition{
	
	public static final String type = "InjuredStaggerCondition";

	public InjuredStaggerCondition(PassiveConditionSettings settings) {
		super(settings);
	}

	@Override
	public boolean isConditionMet(CharacterModel target) {
		return target.isInjuredStaggering();
	}

}
