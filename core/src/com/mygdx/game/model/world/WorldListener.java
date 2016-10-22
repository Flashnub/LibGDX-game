package com.mygdx.game.model.world;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.enemies.Enemy;
import com.mygdx.game.model.worldObjects.WorldObject;

public interface WorldListener {
	public void handleDeletedEnemy(Enemy enemy);
	public void handleAddedEnemy(Enemy enemy);
	public void handleAddedObjectToWorld(WorldObject object);
	public void updateWithNearbyObjects(Array <WorldObject> objects);
	public void handlePlayerInteractionWithObject(WorldObject object);
}	

