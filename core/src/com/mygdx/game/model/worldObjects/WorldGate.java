package com.mygdx.game.model.worldObjects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.model.characters.EntityCollisionData;
import com.mygdx.game.model.characters.player.GameSave.UUIDType;
import com.mygdx.game.model.characters.player.Player.PlayerModel;
import com.mygdx.game.model.events.ObjectListener;
import com.mygdx.game.model.events.SaveListener;

public class WorldGate extends WorldObject {
	final String keyNameKey = "keyName";
	String keyName;

	public WorldGate(String typeOfObject, MapProperties properties, ObjectListener objListener,
			SaveListener saveListener) {
		super(typeOfObject, properties, objListener, saveListener);
		if (properties.containsKey(keyNameKey)) {
			keyName = (String) properties.get(keyNameKey);
		}
		else {
			keyName = "";
		}
	}

	@Override
	public boolean shouldDeleteIfActivated() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean shouldCollideWithEntity() {
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
	public EntityCollisionData handleEntityXCollisionLogic(Rectangle tempGameplayBounds, boolean alreadyCollided) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EntityCollisionData handleEntityYCollisionLogic(Rectangle tempGameplayBounds, boolean alreadyCollided) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override 
	public void actOnThis(PlayerModel player) {
		if (this.canBeActedOn()) {
			if ((!this.keyName.equals("") && player.hasItem(keyName)) || this.keyName.equals("")) {
				this.objListener.objectToActOn(this);
			}
		}
	}

	@Override
	public void refreshHurtBoxesX() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refreshHurtBoxesY() {
		// TODO Auto-generated method stub
		
	}
}
