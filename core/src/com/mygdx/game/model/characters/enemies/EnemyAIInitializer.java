package com.mygdx.game.model.characters.enemies;

import com.mygdx.game.model.characters.enemies.Enemy.EnemyModel;
import com.mygdx.game.model.world.WorldModel;

public class EnemyAIInitializer {
	public static EnemyAI initializeAIWithKey(String enemyAIKey, EnemyModel source, WorldModel world) {
		EnemyAI enemyAI = null;
		switch (enemyAIKey) {
			case BasicEnemyAI.name:
			enemyAI = new BasicEnemyAI(source, world);
			break;
			case DummyAI.name:
			enemyAI = new DummyAI(source, world);
			break;
		}
		return enemyAI;
	}
}
