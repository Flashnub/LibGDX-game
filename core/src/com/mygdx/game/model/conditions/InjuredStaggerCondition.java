package com.mygdx.game.model.conditions;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.EntityModel;

public class InjuredStaggerCondition extends PassiveCondition{
	
	public static final String type = "InjuredStaggerCondition";

	public InjuredStaggerCondition(PassiveConditionSettings settings) {
		super(settings);
	}

	@Override
	public boolean isConditionMet(EntityModel target) {
		if (target instanceof CharacterModel)
		{
			return ((CharacterModel)target).isInjuredStaggering();
		}
		return false;
	}

}
