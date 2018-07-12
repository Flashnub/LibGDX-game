package com.mygdx.game.assets;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json.Serializable;

public class EntityUIData implements Serializable {
	protected ArrayList <AnimationData> animations;
	protected String masterAtlasPath;
	private TextureAtlas masterAtlas;
	
	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("animations", animations);
		json.writeValue("masterAtlasPath", masterAtlasPath);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		animations = json.readValue("animations", ArrayList.class, jsonData);
		masterAtlasPath = json.readValue("masterAtlasPath", String.class, jsonData);
	}
	
	public TextureAtlas getMasterAtlas() {
		if (masterAtlas == null) {
			masterAtlas = new TextureAtlas(Gdx.files.internal(masterAtlasPath));
		}
		return masterAtlas;
	}

	public ArrayList<AnimationData> getAnimations() {
		return animations;
	}

	public String getMasterAtlasPath() {
		return masterAtlasPath;
	}
}
