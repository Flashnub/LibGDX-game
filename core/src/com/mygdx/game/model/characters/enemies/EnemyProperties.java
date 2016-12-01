package com.mygdx.game.model.characters.enemies;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.mygdx.game.model.worldObjects.ItemDrop;
import com.badlogic.gdx.utils.JsonValue;

public class EnemyProperties implements Serializable {
	
	String enemyAIKey;
    ArrayList<ItemDrop> drops;
    boolean shouldDeleteOnDeath;

	@Override
	public void write(Json json) {
		json.writeValue("enemyAIKey", enemyAIKey);
		json.writeValue("drops", drops);

	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		enemyAIKey = json.readValue("enemyAIKey", String.class, jsonData);
		drops = json.readValue("drops", ArrayList.class, jsonData);
	}

}
