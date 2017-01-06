package com.mygdx.game.model.worldObjects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.game.model.characters.CollisionCheck;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.characters.EntityUIDataType;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.characters.player.GameSave.UUIDType;
import com.mygdx.game.model.events.ObjectListener;
import com.mygdx.game.model.events.SaveListener;
import com.mygdx.game.model.world.WorldModel;

public abstract class WorldObject extends EntityModel {
	
	final String deactivatedState = "Deactivated";
	final String activatingState = "Activating";
	final String activatedState = "Activated";
	public static final String WorldObjUUIDKey = "UUID";
	private final String WorldObjUIKey = "UIKey";
	static float defaultGravity = 300f;
	public static int allegiance = 0;
	
	EntityUIModel itemUIModel;
	Integer uuid;
	boolean isActivated, isFacingLeft, didChangeState;
	String state;
	SaveListener saveListener;
	ObjectListener objListener;
	
	public WorldObject(String typeOfObject, MapProperties properties, ObjectListener objListener, SaveListener saveListener) {
		super();
		isFacingLeft = false;
		didChangeState = false;
		itemUIModel = new EntityUIModel((String) properties.get(WorldObjUIKey), EntityUIDataType.WORLDOBJECT);
		this.uuid = (Integer) properties.get(WorldObjUUIDKey);
		this.setObjListener(objListener);
		this.saveListener = saveListener;
		this.acceleration.y = -defaultGravity;
		this.gameplayHitBox = this.imageHitBox;
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
	public abstract boolean shouldMove();
	public abstract boolean shouldCollideWithCharacter();
	public abstract UUIDType getUUIDType();
	
	public void activateObjectOnWorld(WorldModel world) {
		this.setState(this.activatingState);
		if (shouldDeleteIfActivated()) {
			this.getObjListener().deleteObjectFromWorld(this);
		}
		saveListener.addUUIDToSave(this.uuid, this.getUUIDType());
		saveListener.triggerSave();
	}
	
	public void activateAlreadyActivatedObject(WorldModel world) {
		this.setState(this.activatedState);
		this.isActivated = true;
		if (shouldDeleteIfActivated()) {
			this.getObjListener().deleteObjectFromWorld(this);
		}
	}
	
	public void movementWithCollisionDetection(float delta, TiledMapTileLayer collisionLayer) {
		if (this.shouldMove()) {
			CollisionCheck collisionX = this.checkForXCollision(delta, collisionLayer, this.velocity.x, true);
			if (collisionX.isDoesCollide()) {
				this.getVelocity().x = 0;
				this.getAcceleration().x = 0;
			}
			CollisionCheck collisionY = this.checkForYCollision(delta, collisionLayer, this.velocity.y, true, true);
			if (collisionY.isDoesCollide()) {
				this.getVelocity().y = 0;
			}
		}
	}
	
	public void update(float delta, TiledMapTileLayer collisionLayer) {
		this.handleOverlapCooldown(delta);
		this.setGameplaySize(delta);
		this.movementWithCollisionDetection(delta, collisionLayer);
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

	public ObjectListener getObjListener() {
		return objListener;
	}

	public void setObjListener(ObjectListener objListener) {
		this.objListener = objListener;
	}
	
	@Override
	public int getAllegiance() {
		return WorldObject.allegiance;
	}

}
