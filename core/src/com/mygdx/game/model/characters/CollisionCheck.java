package com.mygdx.game.model.characters;

public class CollisionCheck {
	boolean doesCollide;
	float timeUntilCollision;
	
	public CollisionCheck(boolean doesCollide, float timeUntilCollision) {
		this.doesCollide = doesCollide;
		this.timeUntilCollision = timeUntilCollision;
	}

	public boolean isDoesCollide() {
		return doesCollide;
	}

	public float getTimeUntilCollision() {
		return timeUntilCollision;
	}
}
