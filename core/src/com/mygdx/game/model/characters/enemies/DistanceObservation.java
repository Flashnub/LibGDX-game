package com.mygdx.game.model.characters.enemies;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class DistanceObservation extends Observation {
	
	public float xDelta;
	public float yDelta;
	public boolean isTargetToLeft;
	public boolean isBelowTarget;

	public DistanceObservation(CharacterModel observedSource) {
		super(observedSource);
		this.dataType = ObservableDataType.Distance;
	}
	
	@Override
	public int getWeight() {
		// TODO Auto-generated method stub
		return 10;
	}

	
}
