package com.mygdx.game.model.world;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.characters.NPCCharacter;
import com.mygdx.game.model.characters.enemies.Enemy;
import com.mygdx.game.model.worldObjects.Item;
import com.mygdx.game.model.worldObjects.WorldObject;

public interface WorldListener {
	public void handleDeletedEnemy(Enemy enemy);
	public void handleAddedEnemy(Enemy enemy);
	public void handleAddedObjectToWorld(WorldObject object);
	public void updateWithNearbyObjects(Array <WorldObject> objects);
	public void updateWithNearbyNPCs(Array <NPCCharacter> npcs);
	public void handlePlayerInteractionWithObject(WorldObject object);
	public void handleSuperAction(ActionSequence action);
	public void handleSwitchedItem(Item item, int numberOfItems);
}	

