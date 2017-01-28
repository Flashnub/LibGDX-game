package com.mygdx.game.model.actions;

import com.mygdx.game.model.characters.Character.CharacterModel;

public interface ActionSegmentListener {
	void onWindup(CharacterModel source);
	void onActive(CharacterModel source);
	void onCooldown(CharacterModel source);
}
