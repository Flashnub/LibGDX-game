package com.mygdx.game.model.characters.player;

import com.mygdx.game.model.items.Item;

public interface PlayerListener {
	public void handleAcquiredItem(Item item, Player player);
	public void handleStandingOverItem(Item item, Player player);
}
