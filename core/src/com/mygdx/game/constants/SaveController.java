package com.mygdx.game.constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.game.model.characters.player.GameSave;

public class SaveController {
	public GameSave currentGameSave;
	public int saveIndex;
	
	public SaveController(GameSave gameSave) {
		this.currentGameSave = gameSave;
	}
	
	
	public void save() {
		FileHandle fileHandle = Gdx.files.local("Saves/currentSave.json");
		Json json = new Json(OutputType.json);
		json.toJson(currentGameSave, GameSave.class, fileHandle);
	}
}
