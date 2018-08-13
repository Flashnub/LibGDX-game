//package com.mygdx.game.model.weapons;
//
//import com.badlogic.gdx.utils.Array;
//import com.badlogic.gdx.utils.Json;
//import com.badlogic.gdx.utils.Json.Serializable;
//import com.badlogic.gdx.utils.JsonValue;
//import com.mygdx.game.model.actions.ActionSequence;
//
//public class WeaponProperties implements Serializable {
//	
//	String name;
//	String description;
//	Array <ActionSequence> weaponActions;
//	
//	
//	@Override
//	public void write(Json json) {
//		json.writeValue("name", name);
//		json.writeValue("description", description);
//		json.writeValue("weaponActions", weaponActions);		
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public void read(Json json, JsonValue jsonData) {
//		Array <ActionSequence> weaponActions = json.readValue("weaponActions", Array.class, jsonData);
//		name = json.readValue("name", String.class, jsonData);
//		description = json.readValue("description", String.class, jsonData);	
//		Array <ActionSequence> sortedWeaponActions = new Array<ActionSequence>();
//		for (int i = 0; i < weaponActions.size; i++) {
//			ActionSequence.addSequenceToSortedArray(sortedWeaponActions, weaponActions.get(i));
//		}
//		this.weaponActions = sortedWeaponActions;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public String getDescription() {
//		return description;
//	}
//
//	public Array<ActionSequence> getWeaponActions() {
//		return weaponActions;
//	}
//	
//	
//	
//}
