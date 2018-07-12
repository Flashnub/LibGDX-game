package com.mygdx.game.model.characters.player;

import com.mygdx.game.model.worldObjects.Item;

public interface PlayerListener {
	public void handleAcquiredItem(Item item, Player player);
	public void handleStandingOverItem(Item item, Player player);
}
