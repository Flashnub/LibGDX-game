package com.mygdx.game.model.worldObjects;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class LeverSettings implements Serializable {
	Array <Vector2> tileCoordinatesToModify;
	String uniqueID;
	boolean isActivated;
	
	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		tileCoordinatesToModify = json.readValue("tileCoordinatesToModify", Array.class, jsonData);
		uniqueID = json.readValue("uniqueID", String.class, jsonData);

	}
	
	
}
