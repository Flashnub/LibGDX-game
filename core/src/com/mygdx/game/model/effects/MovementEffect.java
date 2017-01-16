package com.mygdx.game.model.effects;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.characters.CollisionCheck;

public class MovementEffect extends EntityEffect {

	Vector2 oldAccel;
	MovementEffectSettings mSettings;	
	public static final String type = "Movement";
	
	public MovementEffect(EffectSettings settings, EffectController retriever) {
		super(settings, retriever);
		oldAccel = new Vector2();
		if (settings instanceof MovementEffectSettings) {
			this.mSettings = (MovementEffectSettings) settings;
			this.staggered = mSettings.startWithStagger;
		}
	}

	@Override
	protected void initialProcess(CharacterModel target) {
		super.initialProcess(target);
		target.getVelocity().x = target.isFacingLeft() ? -this.mSettings.velocity.x : this.mSettings.velocity.x;
		target.getVelocity().y = this.mSettings.velocity.y;	
		
		MovementEffect targetMovement = target.getCurrentMovement();
		if (targetMovement != null && !targetMovement.equals(this)) {
			oldAccel.x = targetMovement.oldAccel.x;
			oldAccel.y = targetMovement.oldAccel.y;
		}
		else {
			oldAccel.x = target.acceleration.x;
			oldAccel.y = target.acceleration.y;
		}

		
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
		
		target.velocity.x = 0;
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

	@Override
	public String getType() {
		return MovementEffect.type;
	}

	@Override
	public boolean shouldReciprocateToSource(CharacterModel target, ActionListener listener) {
		//Miniscule amount of time, should only reciprocate if target is next to wall.
		CollisionCheck collisionCheck = target.checkForXCollision(0.1f, listener.getCollisionLayer(), this.mSettings.velocity.x, this.mSettings.acceleration.x, false);
		return collisionCheck.doesCollide();
	}

	@Override
	public void flipValues() {
		this.mSettings.velocity.x = -this.mSettings.velocity.x;
		this.mSettings.acceleration.x = -this.mSettings.acceleration.x;
	}
	
	@Override
	public boolean shouldAddIfIntercepted() {
		return true;
	}

	@Override
	public void flipValuesIfNecessary(CharacterModel target, CharacterModel source) {
		if (target.gameplayHitBox.x <= source.gameplayHitBox.x && source.isFacingLeft()) {
			flipValues();
		}

	}

	@Override
	public boolean isUniqueEffect() {
		return true;
	}
}
