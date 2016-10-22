package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

import java.util.ArrayList;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class DesktopLauncher {
	
	public static void main (String[] arg) {
        Settings settings = new Settings();
        settings.maxWidth = 2048;
        settings.maxHeight = 8192;
        settings.minHeight = 1024;
        settings.minWidth = 1024;
        
    	String parentOutputDir = "Sprites/";
    	String parentInputDir = "Sprites/";
    	
//        Json json = new Json();
//        ArrayStringWrapper arrayWrapper = json.fromJson(ArrayStringWrapper.class, Gdx.files.internal("Json/Global/characterNames.json"));
//        ArrayList<StringWrapper> characterNames = arrayWrapper.array;
        ArrayList <String> characterNames = new ArrayList<String>();
        characterNames.add("Player");
        characterNames.add("BasicEnemy");
    	
    	for (String characterName : characterNames) {
        	String inputDir = parentInputDir + characterName;
        	String outputDir = parentOutputDir + characterName;
        	
            TexturePacker.process(settings, inputDir, outputDir, "textures");
    	}
    	
    	parentOutputDir = "Sprites/Projectiles/";
    	parentInputDir = "Sprites/Projectiles/";
    	
//    	ArrayStringWrapper arrayWrapper2 = json.fromJson(ArrayStringWrapper.class, Gdx.files.internal("Json/Global/projectileNames.json"));
//        ArrayList<StringWrapper> projectileNames = arrayWrapper2.array;
        ArrayList <String> projectileNames = new ArrayList<String>();
        projectileNames.add("Basic");

    	for (String projectileName : projectileNames) {
        	String inputDir = parentInputDir + projectileName;
        	String outputDir = parentOutputDir + projectileName;
        	
            TexturePacker.process(settings, inputDir, outputDir, "textures");
    	}

    	parentOutputDir = "Sprites/UI/";
    	parentInputDir = "Sprites/UI/";
        TexturePacker.process(settings, parentInputDir, parentOutputDir, "textures");
        
        parentOutputDir = "Sprites/WorldObjects/";
        parentInputDir = "Sprites/WorldObjects/";
        TexturePacker.process(settings, parentInputDir, parentOutputDir, "textures");

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height = 600;
		new LwjglApplication(new MyGdxGame(), config);
	}
}