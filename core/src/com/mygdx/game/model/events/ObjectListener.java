package com.mygdx.game.model.events;

import com.mygdx.game.model.worldObjects.WorldObject;

public interface ObjectListener {
	void addObjectToWorld(WorldObject object);
	void objectToActOn(WorldObject object);
}
