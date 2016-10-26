package com.mygdx.game.model.characters.enemies;

import com.mygdx.game.model.characters.Character.CharacterModel;

public abstract class Observation {
	ObservableDataType dataType;
	CharacterModel sourceOfObservation;
	
	public Observation(CharacterModel sourceOfObservation) {
		this.sourceOfObservation = sourceOfObservation;
	}

	public ObservableDataType getDataType() {
		return dataType;
	}

	public abstract int getWeight();
	
	public enum ObservableDataType {
		Distance
//		, MeleeOffense, RangedOffense, Defend 
	}

}