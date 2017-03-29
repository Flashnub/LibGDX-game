package com.mygdx.game.model.characters;

import com.mygdx.game.model.characters.EntityModel.SlopeSide;

public class SlopeInfo {
	SlopeSide slopeSide;
	float pointOfReturn;
	
	public SlopeInfo(SlopeSide slopeSide, float pointOfReturn) {
		this.slopeSide = slopeSide;
		this.pointOfReturn = pointOfReturn;
	}

	public SlopeSide getSlopeSide() {
		return slopeSide;
	}

	public float getPointOfReturn() {
		return pointOfReturn;
	}
}
