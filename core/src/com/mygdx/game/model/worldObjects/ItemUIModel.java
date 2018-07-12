package com.mygdx.game.model.worldObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;

public class ItemUIModel implements Serializable {
	
	public static TextureAtlas masterAtlas = new TextureAtlas(Gdx.files.internal("Sprites/Items/textures.atlas"));
	
	String itemIconName;
	TextureRegion itemIcon;

	@Override
	public void write(Json json) {
		json.writeValue(itemIconName);
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		itemIconName = json.readValue("itemIconName", String.class, jsonData);
		if (itemIconName != null) {
			itemIcon = ItemUIModel.masterAtlas.findRegion(itemIconName);
		}
	}

	public TextureRegion getItemIcon() {
		return itemIcon;
	}
	
}
