package com.mygdx.game.model.characters.enemies;

import java.util.ArrayList;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class ObservationBlock {
	ArrayList <Observation> observations;
	CharacterModel sourceOfObservation;
	
	public ObservationBlock(CharacterModel sourceOfObservation) {
		observations = new ArrayList<Observation>();
		this.sourceOfObservation = sourceOfObservation; 
	}
	
	public void addObservation(Observation observation) {
		if (observation.sourceOfObservation.getUuid().equals(this.sourceOfObservation.getUuid())) {
			observations.add(observation);
		}
	}
}
