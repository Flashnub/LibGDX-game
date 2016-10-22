package com.mygdx.game.model.world;

import com.badlogic.gdx.math.Vector2;

public class SpawnPoint {
	String entityName;
	Vector2 coordinatePosition;
	String locationName;
	int id;
	
	public SpawnPoint(Vector2 coordinatePosition, String characterName, int id) {
		this.coordinatePosition = coordinatePosition;
		this.entityName = characterName;
		this.id = id;
	}
	
	public SpawnPoint(Vector2 coordinatePosition, String characterName, String locationName, int id) {
		this(coordinatePosition, characterName, id);
		this.locationName = locationName;
	}

	public String getEntityName() {
		return entityName;
	}

	public Vector2 getCoordinatePosition() {
		return coordinatePosition;
	}

	public String getLocationName() {
		return locationName;
	}

	public int getId() {
		return id;
	}
	
}
