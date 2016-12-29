package com.mygdx.game.model.effects;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.events.ActionListener;

//Use this to get information from the attack/projectile.
public interface EffectController {
	public MovementEffectSettings getReplacementMovementForStagger();
	public ActionListener getActionListener();
	public Vector2 spawnOriginForChar();
}
