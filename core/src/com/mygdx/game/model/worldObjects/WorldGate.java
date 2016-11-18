package com.mygdx.game.model.worldObjects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
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
	public boolean shouldCollideWithCharacter() {
		return !isActivated;
	}

	@Override
	public UUIDType getUUIDType() {
		return UUIDType.OBJECT;
	}

	@Override
	public boolean shouldMove() {
		return false;
	}

	@Override
	public boolean handleAdditionCollisionLogic(Rectangle tempGameplayBounds) {
		return false;
	}
}
