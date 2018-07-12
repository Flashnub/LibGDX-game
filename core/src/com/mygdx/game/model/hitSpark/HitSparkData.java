package com.mygdx.game.model.hitSpark;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class HitSparkData implements Serializable{
	String type;
	String size;
	
	public HitSparkData (String type, String size) {
		this.type = type;
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public String getSize() {
		return size;
	}

	@Override
	public void write(Json json) {
		json.writeValue("size", size);
		json.writeValue("type", type);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		size = json.readValue("size", String.class, jsonData);
		type = json.readValue("type", String.class, jsonData);
	}
}
