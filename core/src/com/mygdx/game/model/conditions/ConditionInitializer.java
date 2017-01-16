package com.mygdx.game.model.conditions;

import com.mygdx.game.model.actions.ActionCondition;
import com.mygdx.game.model.actions.ActionConditionSettings;
import com.mygdx.game.model.actions.EffectActionCondition;
import com.mygdx.game.model.actions.PreviousActionCondition;
import com.mygdx.game.model.characters.Character.CharacterModel;

public class ConditionInitializer {
	
	public static PassiveCondition initializePassiveCondition(CharacterModel source, PassiveConditionSettings settings) {
		PassiveCondition condition = null;

		return condition;
	}
	
	public static ActionCondition initializeActiveCondition(CharacterModel source, ActionConditionSettings settings) {
		ActionCondition condition = null;
		switch(settings.getType()) {
		case EffectActionCondition.type:
			condition = new EffectActionCondition(settings, source);
			break;
		case PreviousActionCondition.type:
			condition = new PreviousActionCondition(settings, source);
			break;
		}
		return condition;
	}
} 
