package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.events.ActionListener;

public class YMovementEffect extends EntityEffect{
	
	public static final String type = "YMovementEffect";
	float oldAccel;
	YMovementEffectSettings mSettings;
	
	public YMovementEffect(EffectSettings settings, EffectController controller) {
		super(settings, controller);
		if (settings instanceof YMovementEffectSettings) {
			mSettings = (YMovementEffectSettings) settings;
		}
	}
	
	@Override
	protected void completion(CharacterModel target) {
		super.completion(target);
		target.acceleration.y = oldAccel;
		
	}
	
	@Override
	protected void initialProcess(CharacterModel target) {
		super.initialProcess(target);
		
		target.getVelocity().y = this.mSettings.velocity;
		if (this.mSettings.velocity > 0 && !target.isInAir()) {
			target.setIsInAir(true);
		}
		
		
		YMovementEffect targetMovement = target.getYMove();
		if (targetMovement != null && !targetMovement.equals(this)) {
			oldAccel = targetMovement.oldAccel;
		}
		else {
			oldAccel = target.acceleration.y;
		}
		
		if (!mSettings.useGravity) {
			target.acceleration.y = this.mSettings.acceleration;
		}
	}

	public int getPriority() {
		return EntityEffect.HighPriority;
	}

	@Override
	public boolean shouldReciprocateToSource(CharacterModel target, ActionListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void flipValues() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flipValuesIfNecessary(CharacterModel target, CharacterModel source) {
		// TODO Auto-generated method stub
		
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

}
