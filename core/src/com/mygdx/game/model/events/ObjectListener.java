package com.mygdx.game.model.events;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.worldObjects.WorldObject;
import com.mygdx.game.utils.CellWrapper;

public interface ObjectListener {
	void addObjectToWorld(WorldObject object);
	void deleteObjectFromWorld(WorldObject object);
	void objectToActOn(WorldObject object);
	boolean doTilesCollideWithObjects(Array<CellWrapper> nearbyTiles, TiledMapTileLayer collisionLayer);
}
