package com.mygdx.game.model.effects;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.characters.Character.CharacterModel;

public class MovementEffect extends Effect {

	Vector2 oldAccel;
	MovementEffectSettings mSettings;	
	
	public MovementEffect(EffectSettings settings) {
		super(settings);
		oldAccel = new Vector2();
		if (settings instanceof MovementEffectSettings) {
			this.mSettings = (MovementEffectSettings) settings;
		}
	}

	@Override
	protected void initialProcess(CharacterModel target) {
		super.initialProcess(target);
		target.getVelocity().x = target.isFacingLeft() ? -this.mSettings.velocity.x : this.mSettings.velocity.x;
		target.getVelocity().y = this.mSettings.velocity.y;	
		
		oldAccel.x = target.acceleration.x;
		oldAccel.y = target.acceleration.y;
		
		target.acceleration.x = target.isFacingLeft() ? -this.mSettings.acceleration.x : this.mSettings.acceleration.x;
		if (!this.mSettings.useGravity) {
			target.acceleration.y = this.mSettings.acceleration.y;
		}
	}
	
	@Override
	protected void completion(CharacterModel target) {
		super.completion(target);
		target.acceleration.x = oldAccel.x;
		target.acceleration.y = oldAccel.y;
		
		target.velocity.x = mSettings.finishVelocityX;
	}
	
	public Vector2 getOldAccel() {
		return oldAccel;
	}

	public void setOldAccel(Vector2 oldAccel) {
		this.oldAccel = oldAccel;
	}
	
	public Vector2 getMaxVelocity() {
		return mSettings.maxVelocity;
	}

}
