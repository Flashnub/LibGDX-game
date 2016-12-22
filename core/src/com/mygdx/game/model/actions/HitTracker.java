package com.mygdx.game.model.actions;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class HitTracker {
	float timeSinceHit;
	CharacterModel characterHit;
	
	public HitTracker(CharacterModel characterHit)
	{
		timeSinceHit = 0f;
		this.characterHit = characterHit;
	}
	
	public boolean update(float delta, float sourceHitRate) {
		timeSinceHit += delta;
		return this.isExpired(sourceHitRate);
	}
	
	public boolean isExpired(float sourceHitRate) {
		return timeSinceHit > sourceHitRate;
	}
}
