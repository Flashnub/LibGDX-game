package com.mygdx.game.model.worldObjects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.characters.EntityUIDataType;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.characters.player.GameSave.UUIDType;
import com.mygdx.game.model.events.ObjectListener;
import com.mygdx.game.model.events.SaveListener;
import com.mygdx.game.model.world.WorldModel;

public abstract class WorldObject {
	
	final String deactivatedState = "Deactivated";
	final String activatingState = "Activating";
	final String activatedState = "Activated";
	public static final String WorldObjUUIDKey = "UUID";
	private final String WorldObjUIKey = "UIKey";
	static float defaultGravity = 980f;
	
	Rectangle bounds;
	EntityUIModel itemUIModel;
	Integer uuid;
	boolean isActivated, isFacingLeft, didChangeState;
	String state;
	ObjectListener objListener;
	SaveListener saveListener;
	Vector2 acceleration;
	Vector2 velocity;
	
	public WorldObject(String typeOfObject, MapProperties properties, ObjectListener objListener, SaveListener saveListener) {
		isFacingLeft = false;
		didChangeState = false;
		itemUIModel = new EntityUIModel((String) properties.get(WorldObjUIKey), EntityUIDataType.WORLDOBJECT);
		bounds = new Rectangle(0, 0, 0, 0);
		this.uuid = (Integer) properties.get(WorldObjUUIDKey);
		this.objListener = objListener;
		this.saveListener = saveListener;
		state = deactivatedState;
	}
	
	public static boolean shouldAddIfActivated(String typeOfObject) {
		switch (typeOfObject) {
		case "WorldItem":
			return false;
		case "WorldLever":
		case "WorldGate":
			return true;
		}
		return false;
	}
	public abstract boolean shouldDeleteIfActivated();
	public abstract boolean shouldHaveCollisionDetection();
	public abstract UUIDType getUUIDType();
	public void activateObjectOnWorld(WorldModel world) {
		this.setState(this.activatingState);
		if (shouldDeleteIfActivated()) {
			objListener.deleteObjectFromWorld(this);
		}
		saveListener.addUUIDToSave(this.uuid, this.getUUIDType());
		saveListener.triggerSave();
	}
	
	public void activateAlreadyActivatedObject(WorldModel world) {
		this.setState(this.activatedState);
		this.isActivated = true;
		if (shouldDeleteIfActivated()) {
			objListener.deleteObjectFromWorld(this);
		}
	}
	
	public void update(float delta) {
		if (this.didChangeState) {
			this.itemUIModel.setAnimationTime(0f);
			this.didChangeState = false; 
		}
		boolean isFinishedWithAnimation = this.itemUIModel.setCurrentFrame(this, delta);
		if (this.state.equals(this.activatingState) && isFinishedWithAnimation) {
			this.setState(this.activatedState);
			this.isActivated = true;
		}
	}
	
	@Override
	public boolean equals(Object other){ 
		if (other instanceof WorldObject) {
			return ((WorldObject) other).getUuid().equals(this.uuid);
		}
		return false;
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

	public boolean isFacingLeft() {
		return isFacingLeft;
	}
	
	public boolean isActivated() {
		return isActivated;
	}

	public void setState(String state) {
		if (!this.state.equals(state)) {
			this.state = state;
			this.didChangeState = true;
		}
	}

}
