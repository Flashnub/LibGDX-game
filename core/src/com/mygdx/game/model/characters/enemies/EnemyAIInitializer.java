package com.mygdx.game.model.characters.enemies;

import com.mygdx.game.model.characters.CharacterProperties;
import com.mygdx.game.model.characters.enemies.Enemy.EnemyModel;

public class EnemyAIInitializer {
	public static EnemyAI initializeAIWithKey(String enemyAIKey, CharacterProperties properties, EnemyModel source) {
		EnemyAI enemyAI = null;
		switch (enemyAIKey) {
			case "Basic":
			enemyAI = new BasicEnemyAI(properties, source);
		}
		return enemyAI;
	}
}
