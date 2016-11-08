package com.mygdx.game.model.worldObjects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.events.ObjectListener;
import com.mygdx.game.model.world.WorldModel;

public class WorldLever extends WorldObject {

	public WorldLever(String name, MapProperties properties, ObjectListener objListener) {
		super(name, properties, objListener);
		//get global lever properties from global json file
		
		//get specific lever properties from uuid
	}

	@Override
	public Vector2 getDimensions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void activateObjectOnWorld(WorldModel world) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean shouldDeleteIfActivated() {
		return false;
	}

}
