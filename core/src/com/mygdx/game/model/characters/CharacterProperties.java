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
	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		maxHealth = json.readValue("maxHealth", Float.class, jsonData);
		maxWill = json.readValue("maxWill", Float.class, jsonData);
		attack = json.readValue("attack", Float.class, jsonData);
		maxStability = json.readValue("maxStability", Float.class, jsonData);
		actions = json.readValue("actions", HashMap.class, jsonData);
	}
	
	public CharacterProperties cloneProperties() {
		CharacterProperties properties = new CharacterProperties();
		properties.maxHealth = this.maxHealth;
		properties.maxWill = this.maxWill;
		properties.maxStability = this.maxStability;
		properties.attack = this.attack;
		//iterate through actions.
		HashMap <String, ActionSequence> clonedActions = new HashMap<String, ActionSequence> ();
		Iterator<Map.Entry<String, ActionSequence>> iterator = actions.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<String, ActionSequence> pair = (Map.Entry<String, ActionSequence>) iterator.next();
			clonedActions.put(pair.getKey(), pair.getValue().cloneSequence());
		}
		return properties;
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
