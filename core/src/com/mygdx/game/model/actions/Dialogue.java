package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.effects.EffectSettings;

public class Dialogue implements Serializable{
	String writtenDialogue;
	String sourceName;
	Array <EffectSettings> targetEffects;
	float duration;
	
	@Override
	public void write(Json json) {
		json.writeValue("writtenDialogue", writtenDialogue);
		json.writeValue("sourceName", sourceName);
		json.writeValue("targetEffects", targetEffects);
		json.writeValue("duration", duration);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		targetEffects = json.readValue("targetEffects", Array.class, jsonData);
		sourceName = json.readValue("sourceName", String.class, jsonData);
		writtenDialogue = json.readValue("writtenDialogue", String.class, jsonData);
		Float duration = json.readValue("duration", Float.class, jsonData);
		if (duration != null) {
			this.duration = duration;
		}
		else {
			this.duration = 8f;
		}
	}
}
