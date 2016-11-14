package com.mygdx.game.model.worldObjects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.characters.player.GameSave.UUIDType;
import com.mygdx.game.model.events.ObjectListener;
import com.mygdx.game.model.events.SaveListener;

public class WorldGate extends WorldObject {

	public WorldGate(String typeOfObject, MapProperties properties, ObjectListener objListener,
			SaveListener saveListener) {
		super(typeOfObject, properties, objListener, saveListener);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean shouldDeleteIfActivated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shouldHaveCollisionDetection() {
		return !isActivated;
	}

	@Override
	public UUIDType getUUIDType() {
		return UUIDType.OBJECT;
	}
}
