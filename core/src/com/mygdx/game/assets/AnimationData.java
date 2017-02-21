package com.mygdx.game.assets;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class AnimationData implements Serializable {
	String name;
	int numberOfFrames;
	float frameRate;
	PlayMode playMode;
	
	public AnimationData() {
		
	}

	public String getName() {
		return name;
	}

	public int getNumberOfFrames() {
		return numberOfFrames;
	}
	
	public float getFrameRate() {
		return frameRate;
	}
	
	public static String animationString(String name, AnimationData animationData) {
		return name + "-" + animationData.getName();
	}

	public PlayMode getPlayMode() {
		return playMode;
	}

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("name", name);
		json.writeValue("numberOfFrames", numberOfFrames);
		json.writeValue("frameRate", frameRate);
		json.writeValue("playMode", playMode);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		name = json.readValue("name", String.class, jsonData);
		numberOfFrames = json.readValue("numberOfFrames", Integer.class, jsonData);
		Float frameRate = json.readValue("frameRate", Float.class, jsonData);
		if (frameRate != null) {
			this.frameRate = 1f / frameRate;
		}
		else {
			this.frameRate = 1f / 15f;
		}
		playMode = json.readValue("playMode", PlayMode.class, jsonData);
	}
	
}
