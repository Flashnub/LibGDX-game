package com.mygdx.game.model.worldObjects;

import java.util.UUID;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.characters.EntityUIDataType;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.events.ObjectListener;
import com.mygdx.game.model.world.WorldModel;

public abstract class WorldObject {
	
	final String idleState = "Idle";
	public static final String WorldObjUUIDKey = "WorldObjUUIDKey";
	private final String WorldObjUIKey = "WorldObjUIKey";
	
	Rectangle bounds;
	EntityUIModel itemUIModel;
	Integer uuid;
	boolean isActivated;
	String state;
	ObjectListener listener;
	
	public WorldObject(String typeOfObject, MapProperties properties, ObjectListener listener) {
		itemUIModel = new EntityUIModel((String) properties.get(WorldObjUIKey), EntityUIDataType.WORLDOBJECT);
		bounds = new Rectangle(0, 0, this.getDimensions().x, this.getDimensions().y);
		this.uuid = (Integer) properties.get(WorldObjUUIDKey);
		this.listener = listener;
		state = idleState;
	}
	
	public static boolean shouldAddIfActivated(String typeOfObject) {
		switch (typeOfObject) {
		case "WorldItem":
			return false;
		case "WorldLever":
			return true;
		}
		return false;
	}
	public abstract boolean shouldDeleteIfActivated();
	public abstract Vector2 getDimensions();
	public void activateObjectOnWorld(WorldModel world) {
		this.isActivated = true;
		if (shouldDeleteIfActivated()) {
			listener.deleteObjectFromWorld(this);
		}
	}
	
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

	public Integer getUuid() {
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

}
