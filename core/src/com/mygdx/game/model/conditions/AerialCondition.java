package com.mygdx.game.model.conditions;

import com.mygdx.game.model.characters.EntityModel;

public class AerialCondition extends PassiveCondition{
	
	public static final String type = "AerialCondition";


	public AerialCondition(PassiveConditionSettings settings) {
		super(settings);
	}

	@Override
	public boolean isConditionMet(EntityModel target) {
		return target.isInAir();
	}

}
