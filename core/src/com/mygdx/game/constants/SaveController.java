package com.mygdx.game.constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.model.characters.player.GameSave;

public class SaveController {
	public GameSave currentGameSave;
	public int saveIndex;
	
	public SaveController() {
		
	}
	
	
	public void save() {
		FileHandle fileHandle = Gdx.files.local("Saves/currentSave.json");
		Json json = new Json();
		String jsonString = json.prettyPrint(currentGameSave);
		fileHandle.writeString(jsonString, false);
	}


	public GameSave getCurrentGameSave() {
		return currentGameSave;
	}


	public void setCurrentGameSave(GameSave currentGameSave) {
		this.currentGameSave = currentGameSave;
	}
	
	
}
