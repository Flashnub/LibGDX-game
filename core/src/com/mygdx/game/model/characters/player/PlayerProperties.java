package com.mygdx.game.model.characters.player;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.items.Item;

public class PlayerProperties implements Serializable{
    ArrayList<Item> inventory;
    HashMap<String, ActionSequence> playerSpecificActions;
    
    
	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("inventory", inventory);
		json.writeValue("playerActions", playerSpecificActions);
	}
	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		inventory = json.readValue("inventory", ArrayList.class, jsonData);
		playerSpecificActions = json.readValue("playerActions", HashMap.class, jsonData);
	}
	
	public ArrayList<Item> getInventory() {
		return inventory;
	}
	public HashMap<String, ActionSequence> getPlayerSpecificActions() {
		return playerSpecificActions;
	}

}
