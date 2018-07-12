package com.mygdx.game.model.characters.player;

import com.mygdx.game.model.worldObjects.Item;

public class ItemInfo {
	
	Item item;
	int numberInInventory;
	
	public ItemInfo(Item item, int numberInInventory) {
		this.numberInInventory = numberInInventory;
		this.item = item;
	}
	
}
