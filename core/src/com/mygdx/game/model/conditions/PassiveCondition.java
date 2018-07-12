package com.mygdx.game.model.conditions;

import com.mygdx.game.model.characters.EntityModel;

public abstract class PassiveCondition {
	PassiveConditionSettings settings;
	
	public PassiveCondition (PassiveConditionSettings settings) {
		this.settings = settings;
	}
	
	public abstract boolean isConditionMet(EntityModel target);
	
}
