package com.mygdx.game.model.characters.enemies;

import com.mygdx.game.model.characters.CharacterProperties;
import com.mygdx.game.model.characters.enemies.Enemy.EnemyModel;
import com.mygdx.game.model.world.WorldModel;

public class EnemyAIInitializer {
	public static EnemyAI initializeAIWithKey(String enemyAIKey, CharacterProperties properties, EnemyModel source, WorldModel world) {
		EnemyAI enemyAI = null;
		switch (enemyAIKey) {
			case "Basic":
			enemyAI = new BasicEnemyAI(properties, source, world);
		}
		return enemyAI;
	}
}
