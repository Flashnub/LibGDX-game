package com.mygdx.game.model.characters.enemies;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.mygdx.game.model.characters.NPCProperties;
import com.mygdx.game.model.worldObjects.ItemDrop;
import com.badlogic.gdx.utils.JsonValue;

public class EnemyProperties extends NPCProperties implements Serializable {
	
	String enemyAIKey;
	float damageForAggro;
	float distanceToRecognize;
	float deAggroFactor;
	float awarenessFactor;
    ArrayList<ItemDrop> drops;
    boolean shouldDeleteOnDeath;
    
	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue("enemyAIKey", enemyAIKey);
		json.writeValue("drops", drops);
		json.writeValue("distanceToRecognize", distanceToRecognize);
		json.writeValue("damageForAggro", damageForAggro);
		json.writeValue("awarenessFactor", awarenessFactor);
		json.writeValue("deAggroFactor", deAggroFactor);

	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		enemyAIKey = json.readValue("enemyAIKey", String.class, jsonData);
		drops = json.readValue("drops", ArrayList.class, jsonData);
		Float damageForAggro = json.readValue("damageForAggro", Float.class, jsonData);
		if (damageForAggro != null) {
			this.damageForAggro = damageForAggro.floatValue();
		}
		else {
			this.damageForAggro = 1f;
		}
		Float distanceToRecognize = json.readValue("distanceToRecognize", Float.class, jsonData);
		if (distanceToRecognize != null) {
			this.distanceToRecognize = distanceToRecognize.floatValue();
		}
		else {
			this.distanceToRecognize = 200f;
		}
		
		Float awarenessFactor = json.readValue("awarenessFactor", Float.class, jsonData);
		if (awarenessFactor != null) {
			this.awarenessFactor = awarenessFactor.floatValue();
		}
		else {
			this.awarenessFactor = 0.25f;
		}
		
		Float deAggroFactor = json.readValue("deAggroFactor", Float.class, jsonData);
		if (deAggroFactor != null) {
			this.deAggroFactor = deAggroFactor.floatValue();
		}
		else {
			this.deAggroFactor = 3f;
		}
		
	}

}
