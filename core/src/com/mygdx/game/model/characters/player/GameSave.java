package com.mygdx.game.model.characters.player;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class GameSave implements Serializable{
	
	public enum UUIDType {
		OBJECT, ITEM;
	}

	Array <Integer> objectsInteractedHistory;
	Array <Integer> acquiredItemsHistory;
	Array <String> inventoryItemKeys;
	
	public GameSave() {
		
	}

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("objectsInteractedHistory", objectsInteractedHistory);
		json.writeValue("acquiredItemsHistory", acquiredItemsHistory);
		json.writeValue("inventoryItemKeys", inventoryItemKeys);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		objectsInteractedHistory = json.readValue("objectsInteractedHistory", Array.class, jsonData);
		acquiredItemsHistory = json.readValue("acquiredItemsHistory", Array.class, jsonData);
		inventoryItemKeys = json.readValue("inventoryItemKeys", Array.class, jsonData);

	}
	
	public boolean isUUIDInSave(Integer UUID) {
		return this.acquiredItemsHistory.contains(UUID, false) || this.objectsInteractedHistory.contains(UUID, false);
	}
	
	public void addUUIDToSave(Integer UUID, UUIDType uuidType) {
		switch (uuidType) {
		case OBJECT:
			if (!objectsInteractedHistory.contains(UUID, false))
				objectsInteractedHistory.add(UUID);
			break;
		case ITEM:			
			if (!acquiredItemsHistory.contains(UUID, false))
				acquiredItemsHistory.add(UUID);
			break;
		}
	}

	public Array<Integer> getObjectsInteractedHistory() {
		return objectsInteractedHistory;
	}

	public Array<Integer> getAcquiredItemsHistory() {
		return acquiredItemsHistory;
	}

	public Array<String> getInventoryItemKeys() {
		return inventoryItemKeys;
	}
	
	public static GameSave testSave() {
		GameSave gameSave = new GameSave();
		gameSave.acquiredItemsHistory = new Array<Integer>();
		gameSave.inventoryItemKeys = new Array<String>();
		gameSave.objectsInteractedHistory = new Array<Integer>();
		return gameSave;
	}
}
