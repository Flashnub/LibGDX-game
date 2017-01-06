package com.mygdx.game.model.characters.enemies;

import java.util.ArrayList;

import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.characters.enemies.Enemy.EnemyModel;
import com.mygdx.game.model.world.WorldModel;

public class DummyAI extends EnemyAI {
	
	public static final String name = "Dummy";

	public DummyAI(EnemyModel source, WorldModel world) {
		super(source, world);
	}

	@Override
	public void handleObservation(Observation data) {
		
	}

	@Override
	public void setNextActionSequences(ArrayList<ActionSequence> possibleActionSequences) {
		// TODO Auto-generated method stub
		
	}

}
