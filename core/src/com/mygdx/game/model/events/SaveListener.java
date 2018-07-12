package com.mygdx.game.model.events;

import com.mygdx.game.model.characters.player.GameSave.UUIDType;

public interface SaveListener {
	public void triggerSave();
	public void addUUIDToSave(Integer UUID, UUIDType type);
}
