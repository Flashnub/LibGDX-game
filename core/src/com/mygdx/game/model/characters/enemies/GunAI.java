package com.mygdx.game.model.characters.enemies;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.characters.enemies.Enemy.EnemyModel;
import com.mygdx.game.model.world.WorldModel;

public class GunAI extends EnemyAI {
	
	public static final String name = "Gun";

	public GunAI(EnemyModel source, WorldModel world) {
		super(source, world);
		this.shouldRotateTowardsTarget = true;
	}

	@Override
	public void handleObservation(Observation data) {
		
	}

	@Override
	public void setNextActionSequences(Array<ActionSequence> possibleActionSequences) {
		if (!this.source.isActionLock() && !isDocile() && !this.source.isAlreadyDead()) {
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
				this.source.addActionSequence(nextSeq);
			}
		}
	}

	@Override
	public float probabilityToRun() {
		return 0f;
	}

}
