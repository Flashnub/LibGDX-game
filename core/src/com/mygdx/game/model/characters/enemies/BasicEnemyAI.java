package com.mygdx.game.model.characters.enemies;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.enemies.Enemy.EnemyModel;
import com.mygdx.game.model.world.WorldModel;

public class BasicEnemyAI extends EnemyAI {

	public static final String name = "Basic";
	
	public BasicEnemyAI(EnemyModel source, WorldModel world) {
		super(source, world);
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
	public void setNextActionSequences(Array<ActionSequence> possibleActionSequences) {
		// TODO Auto-generated method stub
		if (!this.source.isActionLock() && !isDocile()) {
			ActionSequence nextSeq = null;
			float maxProbability = 0f;
			for (ActionSequence sequence : possibleActionSequences) {
				maxProbability += sequence.getProbabilityToActivate();
			}
			float randomProbability = (maxProbability + this.probabilityToRun()) * rand.nextFloat();
			float currentIndexProbability = 0f;
			for (ActionSequence sequence : possibleActionSequences) {
				currentIndexProbability += sequence.getProbabilityToActivate();
				if (currentIndexProbability > randomProbability) {
					nextSeq = sequence;
					break;
				}
			}
			if (nextSeq != null) {
//				this.source.stopHorizontalMovement();
//				this.nextActionSequences.add(currentSeq);
				this.source.addActionSequence(nextSeq);
			}
			else if (!this.source.isProcessingActiveSequences()){ 
				//walk towards or away nearest enemy
				source.horizontalMove(rand.nextBoolean());
			}
		}

	}

	@Override
	public float probabilityToRun() {
		return 1f;
	}
}
