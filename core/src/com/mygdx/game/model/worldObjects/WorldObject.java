package com.mygdx.game.model.worldObjects;

import java.util.UUID;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.world.WorldModel;

public abstract class WorldObject {
	
//	public enum WorldObjectState implements State {
//		Idle;
//
//		@Override
//		public String getState() {
//			return toString();
//		}
//	}
	
	final String idleState = "Idle";
	
	Rectangle bounds;
	EntityUIModel itemUIModel;
	String uuid;
	boolean shouldDeleteOnActivation;
	String state;
	
	public WorldObject(String name, MapProperties properties) {
		itemUIModel = new EntityUIModel(name);
		bounds = new Rectangle(0, 0, this.getDimensions().x, this.getDimensions().y);
		uuid = UUID.randomUUID().toString();
		shouldDeleteOnActivation = false;
		state = idleState;
	}
	
	public abstract Vector2 getDimensions();
	public abstract void activateObjectOnWorld(WorldModel world);
	
	@Override
	public boolean equals(Object other){ 
		if (other instanceof WorldObject) {
			return ((WorldObject) other).getUuid().equals(this.uuid);
		}
		return false;
	}
	
	public void update(float delta, TiledMapTileLayer collisionLayer) {
		itemUIModel.setCurrentFrame(this, delta);
	}

	public String getUuid() {
		return uuid;
	}

	public Rectangle getBounds() {
		return bounds;
	}

	public EntityUIModel getItemUIModel() {
		return itemUIModel;
	}

	public String getState() {
		return state;
	}

	public boolean shouldDeleteOnActivation() {
		return shouldDeleteOnActivation;
	}

	public void setShouldDeleteOnActivation(boolean shouldDeleteOnActivation) {
		this.shouldDeleteOnActivation = shouldDeleteOnActivation;
	}
		
}
