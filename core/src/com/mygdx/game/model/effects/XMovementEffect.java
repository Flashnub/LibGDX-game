package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.CollisionCheck;
import com.mygdx.game.model.events.ActionListener;

public class XMovementEffect  extends EntityEffect{

	float oldAccel;
	boolean didApplyAccel;
	XMovementEffectSettings mSettings;
	public static final String type = "XMovementEffect";

	public XMovementEffect(EffectSettings settings, EffectController controller) {
		super(settings, controller);
		if (settings instanceof XMovementEffectSettings) {
			this.mSettings = (XMovementEffectSettings) settings;
		}
		didApplyAccel = false;
	}
	
	@Override
	protected void completion(CharacterModel target) {
		super.completion(target);
		if (didApplyAccel)
			target.acceleration.x = oldAccel;
		
		if (!target.isInAir()) {
			target.velocity.x = 0;
		}
	}
	
	@Override
	protected void initialProcess(CharacterModel target) {
		super.initialProcess(target);
		
		target.getVelocity().x = target.isFacingLeft() ? -this.mSettings.velocity : this.mSettings.velocity;
		
		XMovementEffect targetMovement = target.getXMove();
		if (targetMovement != null && !targetMovement.equals(this)) {
			oldAccel = targetMovement.oldAccel;
		}
		else {
			oldAccel = target.acceleration.x;
		}
		if (target.isInAir()) {
			didApplyAccel = false;
		}
		else {
			target.acceleration.x = target.isFacingLeft() ? -this.mSettings.acceleration : this.mSettings.acceleration;
			didApplyAccel = true;
		}
	}

	@Override
	public boolean shouldReciprocateToSource(CharacterModel target, ActionListener listener) {
		//Miniscule amount of time, should only reciprocate if target is next to wall.
		CollisionCheck collisionCheck = target.checkForXCollision(0.1f, listener.getCollisionLayer(), this.mSettings.velocity, this.mSettings.acceleration, false);
		return collisionCheck.doesCollide();
	}

	@Override
	public void flipValues() {
		this.mSettings.velocity = -this.mSettings.velocity;
		this.mSettings.acceleration = -this.mSettings.acceleration;		
	}

	@Override
	public void flipValuesIfNecessary(CharacterModel target, CharacterModel source) {
		if (target.gameplayHitBox.x <= source.gameplayHitBox.x && source.isFacingLeft()) {
			flipValues();
		}		
	}

	@Override
	public boolean shouldAddIfIntercepted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUniqueEffect() {
		return true;
	}

	@Override
	public String getType() {
		return type;
	}

	public float getMaxVelocity() {
		return this.mSettings.maxVelocity;
	}

}
