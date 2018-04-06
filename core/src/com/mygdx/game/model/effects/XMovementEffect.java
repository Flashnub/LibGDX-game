package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.CollisionCheck;
import com.mygdx.game.model.characters.EntityModel;
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
	protected void completion(EntityModel target) {
		super.completion(target);

		if (didApplyAccel)
			target.acceleration.x = oldAccel;
		
		if (!target.isInAir()) {
			target.velocity.x = 0;
//			System.out.println("Completion: " + target.getVelocity().x);
		}
	}
	
	@Override
	protected void initialProcess(EntityModel target) {
		super.initialProcess(target);
		target.getVelocity().x = this.mSettings.velocity;
//		System.out.println("Initial Process: " + target.getVelocity().x );

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
			target.acceleration.x = this.mSettings.acceleration;
			didApplyAccel = true;
		}
		System.out.println(target.getVelocity().x);
	}

	@Override
	public boolean shouldReciprocateToSource(EntityModel target, ActionListener listener) {
		//Miniscule amount of time, should only reciprocate if target is next to wall.
		if (!(target instanceof CharacterModel)) 
			return false;
		CollisionCheck collisionCheck = target.checkForXCollision(0.1f, listener.getCollisionLayer(), this.mSettings.velocity, this.mSettings.acceleration, false);
		return collisionCheck.doesCollide();
	}

	@Override
	public void flipValues() {
		this.mSettings.velocity = -this.mSettings.velocity;
		this.mSettings.acceleration = -this.mSettings.acceleration;		
	}

	@Override
	public void flipValuesIfNecessary(EntityModel target, EntityModel source) {
		if (source.isFacingLeft()) {
			flipValues();
		}		
	}

	public boolean shouldUseFor(EntityModel target) {
		return target instanceof CharacterModel;
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
