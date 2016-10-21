package com.mygdx.game.renderer;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.characters.Character;

public interface CoordinatesHelper {
	public Vector2 getScreenCoordinatesForCharacter(Character character);
}
