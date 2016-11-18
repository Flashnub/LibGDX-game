package com.mygdx.game.model.characters.enemies;

import com.mygdx.game.model.characters.CharacterProperties;

import java.util.ArrayList;

import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.enemies.Enemy.EnemyModel;
import com.mygdx.game.model.world.WorldModel;

public class BasicEnemyAI extends EnemyAI {

	public BasicEnemyAI(CharacterProperties properties, EnemyModel source, WorldModel world) {
		super(properties, source, world);
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
	public void setNextActionSequences(ArrayList<ActionSequence> possibleActionSequences) {
		// TODO Auto-generated method stub
		ArrayList <ActionSequence> actualActionSequences = new ArrayList <ActionSequence> ();
		for (ActionSequence sequence : possibleActionSequences) {
			float randomFloat = rand.nextFloat();
			if (randomFloat < 0.9) {
				actualActionSequences.add(sequence);
			}
		}
		if (actualActionSequences.size() > 0) {
			this.source.stopWalk();
			ActionSequence actionSequence = actualActionSequences.get(rand.nextInt(actualActionSequences.size()));
			this.nextActionSequences.add(actionSequence);
		}
		else if (!this.source.isProcessingAction()){ 
			//walk towards or away nearest enemy
			source.walk(rand.nextBoolean());
		}
	}
}
