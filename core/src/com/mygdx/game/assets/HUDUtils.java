package com.mygdx.game.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class HUDUtils {
	
	public static TextureAtlas masterAtlas;
	public static Skin HUDSkin = loadHUDStuff();
	
	public static Color redColor() {
		return new Color(255, 0, 0, 1);
	}
	
	public static Color greenColor() {
		return new Color(0, 255, 0, 1);
	}
	
	public static Color blueColor() {
		return new Color(0, 0, 255, 1);
	}
	
	private static Skin loadHUDStuff() {
		HUDUtils.masterAtlas =  new TextureAtlas(Gdx.files.internal("Sprites/UI/textures.atlas"));

		AssetManager manager = new AssetManager();
		manager.load("Sprites/UI/hudskin.json", Skin.class, new SkinLoader.SkinParameter("Sprites/UI/textures.atlas"));
		manager.finishLoading();
		Skin skin = manager.get("Sprites/UI/hudskin.json", Skin.class);

//		skin.add("text-background", masterAtlas.findRegion("text-background"));
//		skin.load(Gdx.files.internal("Sprites/UI/hudskin.json"));
		return skin;
	}
}
