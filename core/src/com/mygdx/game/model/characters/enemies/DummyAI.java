package com.mygdx.game.model.characters.enemies;

import java.util.ArrayList;

import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.characters.CharacterProperties;
import com.mygdx.game.model.characters.enemies.Enemy.EnemyModel;
import com.mygdx.game.model.world.WorldModel;

public class DummyAI extends EnemyAI {

	public DummyAI(CharacterProperties properties, EnemyModel source, WorldModel world) {
		super(properties, source, world);
	}

	@Override
	public void handleObservation(Observation data) {
		
	}

	@Override
	public void setNextActionSequences(ArrayList<ActionSequence> possibleActionSequences) {
		// TODO Auto-generated method stub
		
	}

}
