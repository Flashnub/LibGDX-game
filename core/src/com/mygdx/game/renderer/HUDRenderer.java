package com.mygdx.game.renderer;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.game.model.actions.ActionSegment;
import com.mygdx.game.model.characters.NPCCharacter;
import com.mygdx.game.model.characters.enemies.Enemy;
import com.mygdx.game.model.characters.player.Player.PlayerModel;
import com.mygdx.game.model.world.WorldListener;
import com.mygdx.game.model.world.WorldModel;
import com.mygdx.game.model.worldObjects.Item;
import com.mygdx.game.model.worldObjects.WorldItem;
import com.mygdx.game.model.worldObjects.WorldObject;
import com.mygdx.game.views.AdditionalInfoView;
import com.mygdx.game.views.ButtonPromptOverlay;
import com.mygdx.game.views.DialogueView;
import com.mygdx.game.views.ResourceBar;
import com.mygdx.game.views.ResourceBar.ResourceBarType;

public class HUDRenderer implements WorldListener{
	
	Stage stage;
	WorldModel worldModel;
	ObjectMap <String, ResourceBar> enemyHealthBars;
	ButtonPromptOverlay prompt;
	DialogueView dialogueView;
	CoordinatesHelper helper;
	AdditionalInfoView infoView;
	
	public HUDRenderer(WorldModel worldModel, CoordinatesHelper helper) {
		this.worldModel = worldModel;
		this.enemyHealthBars = new ObjectMap <String, ResourceBar>();
		this.helper = helper;
		this.prompt = new ButtonPromptOverlay();
		this.dialogueView = new DialogueView();
		this.infoView = new AdditionalInfoView();
	    stage = new Stage(new ScreenViewport());
	    ResourceBar healthBar = new ResourceBar(ResourceBarType.PlayerHealth, worldModel.getPlayer(), helper);
	    ResourceBar tensionBar = new ResourceBar(ResourceBarType.PlayerTension, worldModel.getPlayer(), helper);
	    stage.addActor(healthBar);
	    stage.addActor(tensionBar);
	    stage.addActor(prompt);
	    stage.addActor(dialogueView);
	    stage.addActor(infoView);
		worldModel.addWorldListener(this);
		worldModel.getDialogueController().addDialogueListener(dialogueView);
		
		//Needs to update itemview for first time setup
		PlayerModel player = (PlayerModel) worldModel.getPlayer().getCharacterData();
		this.handleSwitchedItem(player.getSelectedItemType(), player.getNumberOfItemsForSelected());
	}
	
    public void render (float delta) {
    	stage.act(delta);
    	stage.draw();
    }
    
    public void resize(int width, int height) {
    	stage.getViewport().update(width, height, true);
    }

	@Override
	public void handleDeletedEnemy(Enemy enemy) {
		// TODO Auto-generated method stub
		ResourceBar enemyHealthBar = enemyHealthBars.get(enemy.getCharacterData().getUuid());
		if (enemyHealthBar != null) {
			//delete 
			stage.getActors().removeValue(enemyHealthBar, true);
			enemyHealthBars.remove(enemy.getCharacterData().getUuid());
		}
	}

	@Override
	public void handleAddedEnemy(Enemy enemy) {
		// TODO Auto-generated method stub
		if (!enemyHealthBars.containsKey(enemy.getCharacterData().getUuid())) {
			ResourceBar enemyHealthBar = new ResourceBar(ResourceBarType.EnemyHealth, enemy, helper);
			enemyHealthBars.put(enemy.getCharacterData().getUuid(), enemyHealthBar);
			stage.addActor(enemyHealthBar);
		}
	}
	
	@Override
	public void handleSwitchedItem(Item item, int numberOfItems) {
		infoView.onSwitchItem(item, numberOfItems);
	}
	
	@Override
	public void handlePlayerInteractionWithObject(WorldObject object) {
		if (object instanceof WorldItem) {
			WorldItem wItem = (WorldItem) object;
			PlayerModel playerModel = (PlayerModel) worldModel.getPlayer().getCharacterData();
			if (wItem.getItem().equals(playerModel.getSelectedItemType())) {
				infoView.onSwitchItem(playerModel.getSelectedItemType(), playerModel.getNumberOfItemsForSelected());
			}
		}
	}

	@Override
	public void handleAddedObjectToWorld(WorldObject object) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateWithNearbyObjects(Array<WorldObject> objects) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateWithNearbyNPCs(Array<NPCCharacter> npcs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleSuperAction(ActionSegment action) {
		// TODO Auto-generated method stub
		
	}



	
}
