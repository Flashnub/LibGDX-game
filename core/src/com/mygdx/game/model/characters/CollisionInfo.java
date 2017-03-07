package com.mygdx.game.model.characters;

public class CollisionInfo {
	float pointOfReturn;
	boolean didCollide;
	
	public CollisionInfo(float pointOfReturn, boolean didCollide) {
		this.pointOfReturn = pointOfReturn;
		this.didCollide = didCollide;
	}

	public float getPointOfReturn() {
		return pointOfReturn;
	}

	public boolean isDidCollide() {
		return didCollide;
	}
}
