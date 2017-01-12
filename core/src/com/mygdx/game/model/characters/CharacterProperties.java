package com.mygdx.game.model.characters;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.wrappers.StringWrapper;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Queue;

public class CharacterProperties implements Serializable {
	float maxHealth;
	float maxWill;
	float attack;
	float maxStability;
	float staggerAllowanceTime;
	float heightCoefficient;
	float widthCoefficient;
	Float gravity;
	Float horizontalSpeed;
	Float horizontalAcceleration;
	Float jumpSpeed;
	Float injuryImmunityTime;
	Integer allegiance;
	CharacterModel source;
	HashMap<String, ActionSequence> actions;
	Array <StringWrapper> weaponKeys;
	Array <ActionSequence> sequencesSortedByInputSize;
	
	public CharacterProperties() {
		
	}

	@Override
	public void write(Json json) {
		json.writeValue("maxHealth", maxHealth);
		json.writeValue("maxStability", maxStability);
		json.writeValue("maxWill", maxWill);
		json.writeValue("attack", attack);
		json.writeValue("actions", actions);
		json.writeValue("horizontalSpeed", horizontalSpeed);
		json.writeValue("jumpSpeed", jumpSpeed);
		json.writeValue("gravity", gravity);
		json.writeValue("injuryImmunityTime", injuryImmunityTime);
		json.writeValue("allegiance", allegiance);
		json.writeValue("widthCoefficient", widthCoefficient);
		json.writeValue("heightCoefficient", heightCoefficient);
		json.writeValue("weaponKeys", weaponKeys);

	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		maxHealth = json.readValue("maxHealth", Float.class, jsonData);
		maxWill = json.readValue("maxWill", Float.class, jsonData);
		attack = json.readValue("attack", Float.class, jsonData);
		maxStability = json.readValue("maxStability", Float.class, jsonData);
		actions = json.readValue("actions", HashMap.class, jsonData);
		Array <StringWrapper> weaponKeys = json.readValue("weaponKeys", Array.class, jsonData);
		if (weaponKeys == null) {
			this.weaponKeys = new Array <StringWrapper>();
		}
		else {
			this.weaponKeys = weaponKeys;
		}

		
		Float horizontalSpeed = json.readValue("horizontalSpeed", Float.class, jsonData);
		if (horizontalSpeed != null) {
			this.horizontalSpeed = horizontalSpeed;
		}
		else {
			this.horizontalSpeed = 300f;
		}
		
		Float horizontalAcceleration = json.readValue("horizontalAcceleration", Float.class, jsonData);
		if (horizontalAcceleration != null) {
			this.horizontalAcceleration = horizontalAcceleration;
		}
		else {
			this.horizontalAcceleration = 300f;
		}
		
		Float jumpSpeed = json.readValue("jumpSpeed", Float.class, jsonData);
		if (jumpSpeed != null) {
			this.jumpSpeed = jumpSpeed;
		}
		else {
			this.jumpSpeed = 700f;
		}
		
		Float gravity = json.readValue("gravity", Float.class, jsonData);
		if (gravity != null) {
			this.gravity = gravity;
		}
		else {
			this.gravity = 980f;
		}
		
		Float injuryImmunityTime = json.readValue("injuryImmunityTime", Float.class, jsonData);
		if (injuryImmunityTime != null) {
			this.injuryImmunityTime = injuryImmunityTime;
		}
		else {
			this.injuryImmunityTime = 2f;
		}
		
		Float staggerAllowanceTime = json.readValue("staggerAllowanceTime", Float.class, jsonData);
		if (staggerAllowanceTime != null) {
			this.staggerAllowanceTime = staggerAllowanceTime;
		}
		else {
			this.staggerAllowanceTime = 5f;
		}
		
		Integer allegiance = json.readValue("allegiance", Integer.class, jsonData); 
		if (allegiance != null) {
			this.allegiance = allegiance;
		}
		
		Float widthCoefficient = json.readValue("widthCoefficient", Float.class, jsonData);
		if (widthCoefficient != null) {
			this.widthCoefficient = widthCoefficient;
		}
		else {
			this.widthCoefficient = 1f;
		}
		
		Float heightCoefficient = json.readValue("heightCoefficient", Float.class, jsonData);
		if (heightCoefficient != null) {
			this.heightCoefficient = heightCoefficient;
		}
		else {
			this.heightCoefficient = 1f;
		}
		
		this.sequencesSortedByInputSize = new Array <ActionSequence> ();
		for (ActionSequence sequence : this.actions.values()) {
			ActionSequence.addSequenceToSortedArray(sequencesSortedByInputSize, sequence);
		}
		
	}

	public CharacterProperties cloneProperties() {
		CharacterProperties properties = new CharacterProperties();
		properties.maxHealth = this.maxHealth;
		properties.maxWill = this.maxWill;
		properties.maxStability = this.maxStability;
		properties.attack = this.attack;
		properties.horizontalSpeed = this.horizontalSpeed;
		properties.jumpSpeed = this.jumpSpeed;
		properties.allegiance = this.allegiance;
		properties.gravity = this.gravity;
		properties.injuryImmunityTime = this.injuryImmunityTime;
		properties.widthCoefficient = this.widthCoefficient;
		properties.heightCoefficient = this.heightCoefficient;
		properties.horizontalAcceleration = this.horizontalAcceleration;
		properties.weaponKeys = this.weaponKeys;
		//iterate through actions.
		HashMap <String, ActionSequence> clonedActions = new HashMap<String, ActionSequence> ();
		for (Map.Entry<String, ActionSequence> entry : actions.entrySet()) {
			clonedActions.put(entry.getKey(), entry.getValue().cloneBareSequence());
		}
		properties.setActions(clonedActions);
		properties.sequencesSortedByInputSize = new Array <ActionSequence>();
		for (ActionSequence sequence : clonedActions.values()) {
			ActionSequence.addSequenceToSortedArray(properties.sequencesSortedByInputSize, sequence);
		}
		properties.setSource(this.source);
//		Iterator<Map.Entry<String, ActionSequence>> iterator = actions.entrySet().iterator();
//		while (iterator.hasNext()) {
//			Map.Entry<String, ActionSequence> pair = (Map.Entry<String, ActionSequence>) iterator.next();
//			clonedActions.put(pair.getKey(), pair.getValue().cloneSequence());
//		}
		return properties;
	}
	
	public void setSource (CharacterModel source) {
		this.source = source;
		for (ActionSequence action : actions.values()) {
			action.setSource(source);
		}
	}
	
	public ActionSequence getSequenceGivenInputs(Queue<String> inputs, boolean useLeftInputs) {
		ActionSequence result = null;
		for (ActionSequence sequence : this.sequencesSortedByInputSize) {
			if (sequence.doInputsMatch(inputs, useLeftInputs)) {
				result = sequence;
				break;
			}
		}
		
		return result;
	}

	public float getHeightCoefficient() {
		return heightCoefficient;
	}

	public float getWidthCoefficient() {
		return widthCoefficient;
	}

	public Float getInjuryImmunityTime() {
		return injuryImmunityTime;
	}

	public Float getGravity() {
		return gravity;
	}

	public Float getHorizontalSpeed() {
		return horizontalSpeed;
	}

	public Float getHorizontalAcceleration() {
		return horizontalAcceleration;
	}

	public Float getJumpSpeed() {
		return jumpSpeed;
	}

	public float getMaxStability() {
		return maxStability;
	}

	public float getMaxHealth() {
		return maxHealth;
	}

	public float getMaxWill() {
		return maxWill;
	}

	public float getAttack() {
		return attack;
	}

	public HashMap<String, ActionSequence> getActions() {
		return actions;
	}

	public void setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
	}

	public void setMaxWill(int maxWill) {
		this.maxWill = maxWill;
	}

	public void setAttack(int attack) {
		this.attack = attack;
	}

	public void setActions(HashMap<String, ActionSequence> actions) {
		this.actions = actions;
	}

	public Integer getAllegiance() {
		return allegiance;
	}

	public Array <StringWrapper> getWeaponKeys() {
		return weaponKeys;
	}
}
