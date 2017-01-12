package com.mygdx.game.model.events;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.model.characters.EntityModel;

public interface CollisionChecker {
	public EntityModel checkIfEntityCollidesWithOthers(EntityModel entity, Rectangle tempGameplayBounds);
	public boolean checkIfEntityCollidesWithObjects(EntityModel entity, Rectangle tempGameplayBounds);
	public TiledMapTileLayer getCollisionLayer();
}
