package com.mygdx.game.model.characters.enemies;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.CharacterProperties;
import com.mygdx.game.model.characters.ObservableData;
import com.mygdx.game.model.characters.enemies.Enemy.PlayerObserver;

public abstract class EnemyAI implements PlayerObserver {
	
	float stateTime;
	Array <ObservableData> observations;
	CharacterProperties properties;
	
	public EnemyAI(String enemyName) {
	}
	
	public abstract float timeToExecuteAction();
	protected abstract void adaptIfNeeded(float delta);
	protected abstract void executePattern(float delta);
	
	public void handleObservation(ObservableData observation) {
		observations.add(observation);
	}
	
	public void gatherEnemyActionsForEnemy(String enemyName) {
		
	}
}
