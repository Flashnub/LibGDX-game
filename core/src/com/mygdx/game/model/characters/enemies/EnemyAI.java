package com.mygdx.game.model.characters.enemies;

import java.util.ArrayDeque;
import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.actions.ActionSequence;
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
	ArrayList <ActionSequence> possibleActionsToTake;
	ArrayDeque <ActionSequence> nextActionSequences;
	CharacterProperties properties;
	CharacterModel currentTarget;
	
	public EnemyAI(CharacterProperties properties, EnemyModel source) {
		this.properties = properties;
	}
	
	public void process(WorldModel world, float delta) {
//		observationBlocks.add(observation);
		this.pollWorldForObservations(world);
	}
	
	public abstract void setNextActionSequences();
	
	
	public void figureOutPossibleMoves(float delta) {
		ArrayList <ActionSequence> possibleActionsToTake = new ArrayList <ActionSequence>();
		DistanceObservation distanceObservation = null;
		for (ObservationBlock observationBlock : this.observationBlocks) {
			//find closest enemy and shoot.
			DistanceObservation currentObservation = (DistanceObservation) observationBlock.observations.get(DistanceObservation.classKey);
			if (distanceObservation == null || (distanceObservation != null && currentObservation.isCloserThanOtherObservation(distanceObservation))) {
				distanceObservation = currentObservation;
			}
		}
		if (distanceObservation != null) {
			float rawDistance = distanceObservation.getHypotenuse();
			//shoot if far, attack if close.
			
			for (ActionSequence actionSequence : this.properties.getActions().values()) {
				if (actionSequence.getEffectiveRange() >= rawDistance) {
					possibleActionsToTake.add(actionSequence);
				}
			}
		}
	}
	
	public float AILoopRate() {
		return 0.2f;
	}
	
	protected void pollWorldForObservations(WorldModel world) {
		observationBlocks.clear();
		Array <Enemy> enemies = world.getEnemies();
		Player player = world.getPlayer();
		
		for (Enemy enemy : enemies) {
			this.observationBlocks.add(this.createObservationBlockFromCharacter(enemy.getCharacterData()));
		}
		this.observationBlocks.add(this.createObservationBlockFromCharacter(player.getCharacterData()));
	}
	
	protected ObservationBlock createObservationBlockFromCharacter(CharacterModel characterModel) {
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
