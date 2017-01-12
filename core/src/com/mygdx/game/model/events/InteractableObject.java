package com.mygdx.game.model.events;

import com.mygdx.game.model.characters.player.Player.PlayerModel;

public interface InteractableObject {
	public void actOnThis(PlayerModel player);
}
