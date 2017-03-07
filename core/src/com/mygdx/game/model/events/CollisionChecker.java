package com.mygdx.game.model.events;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.model.characters.EntityCollisionData;
import com.mygdx.game.model.characters.EntityModel;

public interface CollisionChecker {
	public EntityCollisionData checkIfEntityCollidesWithOthers(EntityModel entity, Rectangle tempGameplayBounds, boolean useRespect);
	public EntityCollisionData checkIfEntityCollidesWithOthers(EntityModel entity, Rectangle tempGameplayBounds);
	public boolean checkIfEntityCollidesWithObjects(EntityModel entity, Rectangle tempGameplayBounds);
	public TiledMapTileLayer getCollisionLayer();
}
