package com.mygdx.game.model.characters.enemies;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class DistanceObservation extends Observation {
	
	public float xDelta;
	public float yDelta;
	public boolean isTargetToLeft;
	public boolean isBelowTarget;
	
	public static String classKey = "DistanceObservation";

	public DistanceObservation(CharacterModel observedSource) {
		super(observedSource);
		this.dataType = ObservableDataType.Distance;
	}
	
	@Override
	public int getWeight() {
		// TODO Auto-generated method stub
		return 10;
	}
	
	public float getHypotenuse() {
		return (float) Math.sqrt((xDelta * xDelta) + (yDelta * yDelta));
	}

	
	public boolean isCloserThanOtherObservation(DistanceObservation observation) {
		return this.getHypotenuse() <= observation.getHypotenuse();
	}
	
}
