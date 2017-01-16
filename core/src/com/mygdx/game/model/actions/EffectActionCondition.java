package com.mygdx.game.model.actions;

import java.util.ArrayList;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EntityEffect;

public class EffectActionCondition extends ActionCondition {
	
	public static final String type = "EffectActionCondition";
	EffectActionConditionSettings eSettings;

	public EffectActionCondition(ActionConditionSettings settings, CharacterModel source) {
		super(settings, source);
		if (settings instanceof EffectActionConditionSettings) {
			this.eSettings = (EffectActionConditionSettings) settings;
		}
	}

	@Override
	public boolean isConditionMet() {
		ArrayList <EntityEffect> effects = source.getCurrentEffects();
		for (EntityEffect effect : effects) {
			if (effect.getSpecificID().intValue() == this.eSettings.effectID) {
				return true;
			}
		}
		return false;
	}

}
