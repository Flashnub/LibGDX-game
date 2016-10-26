package com.mygdx.game.model.characters.enemies;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.CharacterProperties;
import com.mygdx.game.model.characters.enemies.Enemy.EnemyModel;
import com.mygdx.game.model.characters.enemies.Enemy.PlayerObserver;
import com.mygdx.game.model.characters.player.Player;
import com.mygdx.game.model.world.WorldModel;

public abstract class EnemyAI implements PlayerObserver {
	
	float stateTime;
	EnemyModel source;
	ArrayList <ObservationBlock> observationBlocks;
	CharacterProperties properties;
	CharacterModel currentTarget;
	
	public EnemyAI(CharacterProperties properties, EnemyModel source) {
		this.properties = properties;
	}
	
	protected abstract void adaptIfNeeded(float delta);
	protected abstract void executePattern(float delta);
	
	public void handleObservation(Observation observation) {
//		observationBlocks.add(observation);
	}
	
	public void pollWorldForObservations(WorldModel world) {
		observationBlocks.clear();
		Array <Enemy> enemies = world.getEnemies();
		Player player = world.getPlayer();
		
		for (Enemy enemy : enemies) {
			this.observationBlocks.add(this.createObservationBlockFromCharacter(enemy.getCharacterData()));
		}
		this.observationBlocks.add(this.createObservationBlockFromCharacter(player.getCharacterData()));
	}
	
	private ObservationBlock createObservationBlockFromCharacter(CharacterModel characterModel) {
		ObservationBlock observationBlock = new ObservationBlock(characterModel);
		if (characterModel.getAllegiance() != source.getAllegiance()) {
			//Get Distance.
			DistanceObservation distanceObservation = new DistanceObservation(characterModel);
			distanceObservation.xDelta = source.getGameplayHitBox().x - characterModel.getGameplayHitBox().x;
			distanceObservation.yDelta = source.getGameplayHitBox().y - characterModel.getGameplayHitBox().y;
			distanceObservation.isTargetToLeft = distanceObservation.xDelta <= 0;
			distanceObservation.isBelowTarget = distanceObservation.yDelta <= 0;
			observationBlock.addObservation(distanceObservation);
			
			//Others.
			
		}
		return observationBlock;
	}
}
