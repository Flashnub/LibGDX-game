package com.mygdx.game.model.worldObjects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class LeverSettings implements Serializable {
	Integer objectToModifyUUID;
	
	
	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		objectToModifyUUID = json.readValue("objectToModifyUUID", Integer.class, jsonData);

	}
	
	
}
