package com.mygdx.game.model.weapons;

import com.badlogic.gdx.utils.Queue;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.actions.ActionSequence;

public class Weapon {
	
	WeaponProperties wProperties;
	
	public Weapon(String weaponName, String characterName) {
		if (weaponName != null && characterName != null) {
			this.wProperties = JSONController.loadWeaponProperties(weaponName, characterName);
		}
	}
	
	public ActionSequence getRandomChainableWeaponAction(ActionSequence previousAction, float probabilityToChain) {
		ActionSequence nextAction = null;
		if (wProperties != null && probabilityToChain > Math.random()) {
			for (ActionSequence weaponAction : wProperties.getWeaponActions()) {
				if (previousAction.isActionChainableWithThis(weaponAction)) {
					nextAction = weaponAction;
					break;
				}
			}
		}

		return nextAction;
	}
	
	public ActionSequence getSpecificWeaponAction(Queue <String> inputs, boolean useLeftInputs) {
		ActionSequence nextAction = null;
		if (wProperties != null) {
			for (ActionSequence weaponAction : wProperties.getWeaponActions()) {
				if (weaponAction.doInputsMatch(inputs, useLeftInputs)) {
					nextAction = weaponAction;
					break;
				}
			}
		}
		return nextAction;
	}
}
