package com.mygdx.game.model.conditions;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class AerialCondition extends PassiveCondition{
	
	public static final String type = "AerialCondition";


	public AerialCondition(PassiveConditionSettings settings) {
		super(settings);
	}

	@Override
	public boolean isConditionMet(CharacterModel target) {
		return target.isInAir();
	}

}
