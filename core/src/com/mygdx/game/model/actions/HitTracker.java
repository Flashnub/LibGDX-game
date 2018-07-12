package com.mygdx.game.model.actions;

import com.mygdx.game.model.characters.EntityModel;

public class HitTracker {
	float timeSinceHit;
	EntityModel entityHit;
	
	public HitTracker(EntityModel characterHit)
	{
		timeSinceHit = 0f;
		this.entityHit = characterHit;
	}
	
	public boolean update(float delta, float sourceHitRate) {
		timeSinceHit += delta;
		return this.isExpired(sourceHitRate);
	}
	
	public boolean isExpired(float sourceHitRate) {
		return timeSinceHit > sourceHitRate;
	}
}
