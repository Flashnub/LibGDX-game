package com.mygdx.game.model.characters.enemies;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.CharacterProperties;
import com.mygdx.game.model.characters.enemies.Enemy.EnemyModel;
import com.mygdx.game.model.characters.enemies.Enemy.PlayerObserver;
import com.mygdx.game.model.characters.player.Player;
import com.mygdx.game.model.world.WorldModel;

public abstract class EnemyAI implements PlayerObserver {
	
	float currentTime;
	EnemyModel source;
	ArrayList <ObservationBlock> observationBlocks;
	ArrayList <ActionSequence> possibleActionsToTake;
	ArrayList <ActionSequence> nextActionSequences;
	CharacterProperties properties;
	public Random rand;
	float currentLoopRate;
	WorldModel world;
	
	public EnemyAI(CharacterProperties properties, EnemyModel source, WorldModel world) {
		this.properties = properties;
		this.source = source;
		rand = new Random();
		currentLoopRate = this.AILoopRate();
		currentTime = 0f; 
		this.world = world;
		this.observationBlocks = new ArrayList <ObservationBlock> ();
		this.possibleActionsToTake = new ArrayList <ActionSequence> ();
		this.nextActionSequences = new ArrayList <ActionSequence> ();
	}
	
	public void process(float delta) {
//		observationBlocks.add(observation);
		currentTime += delta;
		if (currentTime > currentLoopRate) {
			this.clearAll();
			this.currentTime = 0;
			this.currentLoopRate = this.AILoopRate();
			this.pollWorldForObservations();
			this.setNextActionSequences(this.figureOutPossibleMoves());
			if (nextActionSequences.size() > 0) {
				for (ActionSequence sequence : nextActionSequences) {
					source.addActionSequence(sequence);
				}
			}
		}
	}
	
	private void clearAll() {
		this.possibleActionsToTake.clear();
		this.nextActionSequences.clear();
		this.observationBlocks.clear();
	}
	
	public abstract void setNextActionSequences(ArrayList <ActionSequence> possibleActionSequences);
	
	
	public ArrayList <ActionSequence> figureOutPossibleMoves() {
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
				ActionSequence clonedSequence = actionSequence.cloneSequenceWithSourceAndTarget(this.source, distanceObservation.sourceOfObservation);
				if (clonedSequence.getEffectiveRange() >= rawDistance) {
					possibleActionsToTake.add(clonedSequence);
				}
			}
		}
		return possibleActionsToTake;
	}
	
	public float AILoopRate() {
		float loopRate = rand.nextFloat();
		while (loopRate <= 0.5f) {
			loopRate = rand.nextFloat();
		}
		return loopRate;
	}
	
	protected void pollWorldForObservations() {
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
