package com.mygdx.game.model.worldObjects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class ItemDrop implements Serializable {
	float chanceToDrop;
	Item itemDrop;
	
	public ItemDrop() {
		
	}
	
	@Override
	public void write(Json json) {
		json.writeValue("chanceToDrop", chanceToDrop);
		json.writeValue("itemDrop", itemDrop);
	}
	@Override
	public void read(Json json, JsonValue jsonData) {
		chanceToDrop = json.readValue("chanceToDrop", Float.class, jsonData);
		itemDrop = json.readValue("itemDrop", Item.class, jsonData);
	}
	
	
}
