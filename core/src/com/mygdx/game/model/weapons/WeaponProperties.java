package com.mygdx.game.model.weapons;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class WeaponProperties implements Serializable {
	
	String name;
	String description;
	Array <WeaponAction> weaponActions;
	
	
	@Override
	public void write(Json json) {
		json.writeValue("name", name);
		json.writeValue("description", description);
		json.writeValue("weaponActions", weaponActions);		
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		json.readValue("weaponActions", Array.class, jsonData);
		json.readValue("name", String.class, jsonData);
		json.readValue("description", String.class, jsonData);		
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public Array<WeaponAction> getWeaponActions() {
		return weaponActions;
	}
	
	
	
}
