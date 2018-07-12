package com.mygdx.game.model.characters;

public class SlopeInfo {
	boolean onSlope;
	float pointOfReturn;
	
	public SlopeInfo(boolean onSlope, float pointOfReturn) {
		this.onSlope = onSlope;
		this.pointOfReturn = pointOfReturn;
	}

	public boolean getSlopeSide() {
		return onSlope;
	}

	public float getPointOfReturn() {
		return pointOfReturn;
	}
}
