package com.mygdx.game.model.characters.enemies;

import com.mygdx.game.model.characters.CharacterProperties;

import java.util.ArrayList;

import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.enemies.Enemy.EnemyModel;

public class BasicEnemyAI extends EnemyAI {

	public BasicEnemyAI(CharacterProperties properties, EnemyModel source) {
		super(properties, source);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ObservationBlock createObservationBlockFromCharacter(CharacterModel characterModel) {
		ObservationBlock observationBlock = super.createObservationBlockFromCharacter(characterModel);
		
		return observationBlock;
	}

	@Override
	public void handleObservation(Observation data) {
		// TODO Auto-generated method stub
		// From PlayerObserver
	}

	@Override
	public void setNextActionSequences() {
		// TODO Auto-generated method stub
		
	}
}
