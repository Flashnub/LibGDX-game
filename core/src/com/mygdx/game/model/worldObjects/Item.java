package com.mygdx.game.model.worldObjects;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.characters.Character;
import com.mygdx.game.model.effects.Effect;

public class Item implements Serializable{
	ArrayList<Effect> effects;
	String name;
	String desc;
	
	public Item () {

	}
	
	
	public void use(Character owner) {
		for (Effect effect : effects) {
			owner.getCharacterData().addEffect(effect);
		}
	}


	@Override
	public void write(Json json) {
		json.writeValue("effects", effects);
		json.writeValue("name", name);
		json.writeValue("desc", desc);

	}


	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		effects = json.readValue("effects", ArrayList.class, jsonData);
		name = json.readValue("name", String.class, jsonData);
		desc = json.readValue("desc", String.class, jsonData);
	}
}
