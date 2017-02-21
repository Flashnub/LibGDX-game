package com.mygdx.game.model.worldObjects;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.characters.player.GameSave.UUIDType;
import com.mygdx.game.model.characters.player.Player;
import com.mygdx.game.model.characters.player.Player.PlayerModel;
import com.mygdx.game.model.events.ObjectListener;
import com.mygdx.game.model.events.SaveListener;
import com.mygdx.game.model.world.WorldModel;

public class WorldItem extends WorldObject {
	
	private final String typeName = "itemName";

	final String fadingState = "Fading";
	
	public static String name = "WorldItem";
	Item item;
	
	public WorldItem(String name, MapProperties properties, ObjectListener objListener, SaveListener saveListener) {
		super(name, properties, objListener, saveListener);
		this.item = JSONController.items.get(properties.get(typeName));
	}
	
	@Override
	public void activateObjectOnWorld(WorldModel world) {
		Player player = world.getPlayer();
		PlayerModel model = (PlayerModel) player.getCharacterData();
		model.addItemToInventory(item);
		super.activateObjectOnWorld(world);
	}

	@Override
	public boolean shouldDeleteIfActivated() {
		return true;
	}

	@Override
	public boolean shouldCollideWithCharacter() {
		return false;
	}
	
	@Override
	public UUIDType getUUIDType() {
		return UUIDType.ITEM;
	}

	@Override
	public boolean shouldMove() {
		return true;
	}

	@Override
	public boolean handleAdditionalXCollisionLogic(Rectangle tempGameplayBounds, Rectangle tempImageBounds, boolean alreadyCollided) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean handleAdditionalYCollisionLogic(Rectangle tempGameplayBounds, Rectangle tempImageBounds, boolean alreadyCollided) {
		// TODO Auto-generated method stub
		return false;
	}

	public Item getItem() {
		return item;
	}

}
