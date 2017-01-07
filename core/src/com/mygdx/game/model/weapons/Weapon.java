package com.mygdx.game.model.weapons;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.constants.JSONController;

public class Weapon {
	
	WeaponProperties wProperties;
	
	public Weapon(String name) {
		this.wProperties = JSONController.loadWeaponProperties(name);
	}
	
	public WeaponAction getRandomChainableWeaponAction(WeaponAction action) {
		WeaponAction nextAction = null;
		
		return nextAction;
	}
	
	public WeaponAction getSpecificWeaponAction(Array <Integer> inputs) {
		WeaponAction action = null;
		
		return action;
	}
}
