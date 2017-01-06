package com.mygdx.game.model.characters.enemies;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class PositionalObservation extends Observation {
	
	public float xDelta;
	public float yDelta;
	public boolean isTargetToLeft;
	public boolean isBelowTarget;
	public boolean isFacingTarget;
	
	public static String classKey = "PositionalObservation";

	public PositionalObservation(CharacterModel observedSource) {
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

	
	public boolean isCloserThanOtherObservation(PositionalObservation observation) {
		return this.getHypotenuse() <= observation.getHypotenuse();
	}
	
}
