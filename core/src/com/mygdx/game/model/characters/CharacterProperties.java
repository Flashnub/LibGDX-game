package com.mygdx.game.model.characters;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.mygdx.game.model.actions.ActionSequence;
import com.badlogic.gdx.utils.JsonValue;

public class CharacterProperties implements Serializable {
	float maxHealth;
	float maxWill;
	float attack;
	float maxStability;
	Float gravity;
	Float walkingSpeed;
	Float jumpSpeed;
	Float injuryImmunityTime;
	HashMap<String, ActionSequence> actions;
	
	public CharacterProperties() {
		
	}

	@Override
	public void write(Json json) {
		json.writeValue("maxHealth", maxHealth);
		json.writeValue("maxStability", maxStability);
		json.writeValue("maxWill", maxWill);
		json.writeValue("attack", attack);
		json.writeValue("actions", actions);
		json.writeValue("walkingSpeed", walkingSpeed);
		json.writeValue("jumpSpeed", jumpSpeed);
		json.writeValue("gravity", gravity);
		json.writeValue("injuryImmunityTime", injuryImmunityTime);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		maxHealth = json.readValue("maxHealth", Float.class, jsonData);
		maxWill = json.readValue("maxWill", Float.class, jsonData);
		attack = json.readValue("attack", Float.class, jsonData);
		maxStability = json.readValue("maxStability", Float.class, jsonData);
		actions = json.readValue("actions", HashMap.class, jsonData);
		
		Float walkingSpeed = json.readValue("walkingSpeed", Float.class, jsonData);
		if (walkingSpeed != null) {
			this.walkingSpeed = walkingSpeed;
		}
		else {
			this.walkingSpeed = 200f;
		}
		
		Float jumpSpeed = json.readValue("jumpSpeed", Float.class, jsonData);
		if (jumpSpeed != null) {
			this.jumpSpeed = walkingSpeed;
		}
		else {
			this.jumpSpeed = 120f;
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
	}
	
	public CharacterProperties cloneProperties() {
		CharacterProperties properties = new CharacterProperties();
		properties.maxHealth = this.maxHealth;
		properties.maxWill = this.maxWill;
		properties.maxStability = this.maxStability;
		properties.attack = this.attack;
		properties.walkingSpeed = this.walkingSpeed;
		properties.jumpSpeed = this.jumpSpeed;
		//iterate through actions.
		HashMap <String, ActionSequence> clonedActions = new HashMap<String, ActionSequence> ();
		Iterator<Map.Entry<String, ActionSequence>> iterator = actions.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, ActionSequence> pair = (Map.Entry<String, ActionSequence>) iterator.next();
			clonedActions.put(pair.getKey(), pair.getValue().cloneSequence());
		}
		return properties;
	}

	public Float getInjuryImmunityTime() {
		return injuryImmunityTime;
	}

	public Float getGravity() {
		return gravity;
	}

	public Float getWalkingSpeed() {
		return walkingSpeed;
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
	
	
	
}
