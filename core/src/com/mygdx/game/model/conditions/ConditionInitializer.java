package com.mygdx.game.model.conditions;

import com.mygdx.game.model.actions.ActionCondition;
import com.mygdx.game.model.actions.ActionConditionSettings;
import com.mygdx.game.model.actions.EffectActionCondition;
import com.mygdx.game.model.actions.PreviousActionCondition;
import com.mygdx.game.model.actions.SprintActionCondition;
import com.mygdx.game.model.characters.Character.CharacterModel;

public class ConditionInitializer {
	
	public static PassiveCondition initializePassiveCondition(PassiveConditionSettings settings) {
		PassiveCondition condition = null;
		switch (settings.getType()) {
		case InjuredStaggerCondition.type:
			condition = new InjuredStaggerCondition(settings);
			break;
		case AerialCondition.type:
			condition = new AerialCondition(settings);
			break;
		}
			
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
		case SprintActionCondition.type:
			condition = new SprintActionCondition(settings, source);
			break;
		}
		return condition;
	}
} 
