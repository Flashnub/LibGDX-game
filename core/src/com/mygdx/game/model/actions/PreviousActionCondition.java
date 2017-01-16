package com.mygdx.game.model.actions;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class PreviousActionCondition extends ActionCondition {

	PreviousActionConditionSettings pSettings;
	public static final String type = "PreviousActionCondition";
	
	public PreviousActionCondition(ActionConditionSettings settings, CharacterModel source) {
		super(settings, source);
		if (settings instanceof PreviousActionConditionSettings) {
			pSettings = (PreviousActionConditionSettings) settings;
		}
	}

	@Override
	public boolean isConditionMet() {
		ActionSequence currentSeq = source.getCurrentActiveActionSeq();
		if (currentSeq != null) {
			return currentSeq.getActionKey().key.equals(pSettings.prevActionKey.key);
		}
		return false;
	}

}
