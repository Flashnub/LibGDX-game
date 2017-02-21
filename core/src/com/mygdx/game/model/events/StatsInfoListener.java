package com.mygdx.game.model.events;

import com.mygdx.game.model.worldObjects.Item;

public interface StatsInfoListener {
	public void onSwitchItem(Item item, int numberOfItems);
}
