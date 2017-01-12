package com.mygdx.game.model.characters.enemies;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.characters.Character.CharacterModel;
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
	public Random rand;
	float currentLoopRate;
	WorldModel world;
	CharacterModel currentTarget;
	
	public EnemyAI(EnemyModel source, WorldModel world) {
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
		if (isDocile()) {
			this.source.handlePatrol(delta);
		}

		if (currentTime > currentLoopRate) {
			this.clearAll();
			this.currentTime = 0;
			this.currentLoopRate = this.AILoopRate();
			this.pollWorldForObservations();
			checkIfShouldDeAggro();
			findTargetIfDocile();
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
	
	public abstract void setNextActionSequences(Array <ActionSequence> possibleActionSequences);
	public abstract float probabilityToRun();
	
	
	public Array <ActionSequence> figureOutPossibleMoves() {
		Array <ActionSequence> possibleActionsToTake = new Array <ActionSequence>();
		if (!this.source.isActionLock() && this.currentTarget != null) {
			PositionalObservation distanceObservation = null;
			for (ObservationBlock observationBlock : this.observationBlocks) {
				//find closest enemy and shoot.
				PositionalObservation currentObservation = (PositionalObservation) observationBlock.observations.get(PositionalObservation.classKey);
				if (currentObservation != null && currentObservation.sourceOfObservation.equals(this.currentTarget)) {
					distanceObservation = currentObservation;
					break;
				}
			}
			if (distanceObservation != null) {
				float rawDistance = distanceObservation.getHypotenuse();
				//shoot if far, attack if close.
				ActionSequence currentSequence = this.source.getCurrentActiveActionSeq();
				for (ActionSequence actionSequence : this.source.getCharacterProperties().getActions().values()) {
					ActionSequence clonedSequence = actionSequence.cloneSequenceWithSourceAndTarget(this.source, distanceObservation.sourceOfObservation, world, world);
					if (clonedSequence.getEffectiveRange() >= rawDistance) {
						if (currentSequence != null && currentSequence.isActionChainableWithThis(clonedSequence)) {
							clonedSequence.increaseProbability();
						}
						possibleActionsToTake.add(clonedSequence);
					}
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
	
	protected void checkIfShouldDeAggro() {
		if (!isDocile()) {
			for (ObservationBlock block : this.observationBlocks) {
				if (this.currentTarget.equals(block.sourceOfObservation)) {
					for (Observation observation : block.observations.values()) {
						if (observation instanceof PositionalObservation) {
							PositionalObservation positionalObservation = (PositionalObservation) observation;
							float distance = positionalObservation.getHypotenuse();
							if (distance > source.enemyProperties.distanceToRecognize * source.enemyProperties.deAggroFactor) {
								this.currentTarget = null;
								this.source.resetPatrolFields();
								return;
							}
						}
					}
				}
			}
		}
	}
	
	protected void findTargetIfDocile() {
		if (isDocile()) {
			for (ObservationBlock block : this.observationBlocks) {
				for (Observation observation : block.observations.values()) {
					if (observation instanceof PositionalObservation && !observation.sourceOfObservation.equals(this.source)) {
						PositionalObservation positionalObservation = (PositionalObservation) observation;
						float distance = positionalObservation.getHypotenuse();
						if (!positionalObservation.isFacingTarget) {
							distance = distance * source.enemyProperties.awarenessFactor;
						}
						if (distance < source.enemyProperties.distanceToRecognize && this.source.getAllegiance() != positionalObservation.sourceOfObservation.getAllegiance()) {
							currentTarget = positionalObservation.sourceOfObservation;
							this.source.resetPatrolFields();
							return;
						}
					}
				}
			}
		}
	}
	
	protected ObservationBlock createObservationBlockFromCharacter(CharacterModel characterModel) {
		ObservationBlock observationBlock = new ObservationBlock(characterModel);
		//Get Distance.
		PositionalObservation positionalObservation = new PositionalObservation(characterModel);
		positionalObservation.xDelta = source.getGameplayHitBox().x - characterModel.getGameplayHitBox().x;
		positionalObservation.yDelta = source.getGameplayHitBox().y - characterModel.getGameplayHitBox().y;
		positionalObservation.isTargetToLeft = positionalObservation.xDelta <= 0;
		positionalObservation.isBelowTarget = positionalObservation.yDelta <= 0;
		positionalObservation.isFacingTarget = (positionalObservation.isTargetToLeft && source.isFacingLeft()) || (!positionalObservation.isTargetToLeft && !source.isFacingLeft());
		positionalObservation.sourceOfObservation = characterModel;
		observationBlock.addObservation(positionalObservation);
			
		return observationBlock;
	}
	
	public boolean isDocile() {
		return this.currentTarget == null;
	}
}
