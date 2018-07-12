package com.mygdx.game.model.characters;

public class CollisionCheck {
	
	public enum CollisionType {
		World, Object, Entity, Slope, None
	}
	
	boolean doesCollide;
	CollisionType collisionType;
	float timeUntilCollision;
	float pointOfReturn;
	boolean isVelocityPositive;
	EntityCollisionData entityCollisionData;
	
	public CollisionCheck(boolean doesCollide, boolean isVelocityPositive, float timeUntilCollision, CollisionType collisionType, float pointOfReturn) {
		this.doesCollide = doesCollide;
		this.isVelocityPositive = isVelocityPositive;
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
