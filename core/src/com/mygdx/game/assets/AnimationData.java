package com.mygdx.game.assets;

import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class AnimationData implements Serializable {
	String name;
	int numberOfWindupFrames;
	int numberOfActiveFrames;
	int numberOfCooldownFrames;
	float frameRate;
	PlayMode playMode;
	
	public AnimationData() {
		
	}

	public String getName() {
		return name;
	}

	public int getNumberOfWindupFrames() {
		return numberOfWindupFrames;
	}
	
	public int getNumberOfActiveFrames() {
		return numberOfActiveFrames;
	}

	public int getNumberOfCooldownFrames() {
		return numberOfCooldownFrames;
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
		json.writeValue("numberOfWindupFrames", numberOfWindupFrames);
		json.writeValue("numberOfActiveFrames", numberOfActiveFrames);
		json.writeValue("numberOfCooldownFrames", numberOfCooldownFrames);
		json.writeValue("frameRate", frameRate);
		json.writeValue("playMode", playMode);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		name = json.readValue("name", String.class, jsonData);
		
		Integer numberOfWindupFrames = json.readValue("numberOfWindupFrames", Integer.class, jsonData);
		Integer numberOfActiveFrames = json.readValue("numberOfActiveFrames", Integer.class, jsonData);
		Integer numberOfCooldownFrames = json.readValue("numberOfCooldownFrames", Integer.class, jsonData);

		this.numberOfWindupFrames = numberOfWindupFrames != null ? numberOfWindupFrames.intValue() : 0;
		this.numberOfActiveFrames = numberOfActiveFrames != null ? numberOfActiveFrames.intValue() : 0;
		this.numberOfCooldownFrames = numberOfCooldownFrames != null ? numberOfCooldownFrames.intValue() : 0;

		Float frameRate = json.readValue("frameRate", Float.class, jsonData);
		if (frameRate != null) {
			this.frameRate = 1f / frameRate;
		}
		else {
			this.frameRate = 1f / 24f;
		}
		playMode = json.readValue("playMode", PlayMode.class, jsonData);
	}
	
}
