package com.mygdx.game.model.characters.enemies;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class PositionalObservation extends Observation {
	
	public float xDelta;
	public float yDelta;
	public boolean isFacingTarget;
	
	public static String classKey = "PositionalObservation";

	public PositionalObservation(CharacterModel observer, CharacterModel observedSource) {
		super(observer, observedSource);
		this.dataType = ObservableDataType.Distance;
		xDelta = observer.getGameplayHitBox().x - observedSource.getGameplayHitBox().x;
		yDelta = observer.getGameplayHitBox().y - observedSource.getGameplayHitBox().y;
		isFacingTarget = (isTargetToLeft() && observer.isFacingLeft()) || (!isTargetToLeft() && !observer.isFacingLeft());
	}
	
	@Override
	public int getWeight() {
		return 10;
	}
	
	public boolean isTargetToLeft() {
		return xDelta >= 0;
	}
	
	public boolean isBelowTarget() {
		return yDelta <= 0;
	}
	
	public float getAngleTowardsTarget() {
		if (xDelta == 0) {
			return this.isBelowTarget() ? 90f : -90f;
		}
		else if (yDelta == 0) {
			return this.isTargetToLeft() ? 180f : 0f;
		}
		float angle = (float) Math.toDegrees(Math.acos((Math.pow(xDelta, 2) + Math.pow(this.getHypotenuse(), 2) - Math.pow(yDelta, 2)) / (2 * this.getHypotenuse() * xDelta)));
		if (!this.isTargetToLeft()) {
			angle = 180 - angle;
		}
		if (!this.isBelowTarget()) {
			angle = -angle;
		}

		return angle;
	}

	public float getHypotenuse() {
		return (float) Math.sqrt((xDelta * xDelta) + (yDelta * yDelta));
	}

	
	public boolean isCloserThanOtherObservation(PositionalObservation observation) {
		return this.getHypotenuse() <= observation.getHypotenuse();
	}
	
}
