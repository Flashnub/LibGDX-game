package com.mygdx.game.model.characters;

public class CollisionCheck {
	
	public enum CollisionType {
		World, Object, Entity, None
	}
	
	boolean doesCollide;
	CollisionType collisionType;
	float timeUntilCollision;
	float pointOfReturn;
	
	public CollisionCheck(boolean doesCollide, float timeUntilCollision, CollisionType collisionType, float pointOfReturn) {
		this.doesCollide = doesCollide;
		this.timeUntilCollision = timeUntilCollision;
		this.collisionType = collisionType;
		this.pointOfReturn = pointOfReturn;
	}

	public boolean doesCollide() {
		return doesCollide;
	}

	public float getTimeUntilCollision() {
		return timeUntilCollision;
	}

	public CollisionType getCollisionType() {
		return collisionType;
	}

	public float getPointOfReturn() {
		return pointOfReturn;
	}
}
