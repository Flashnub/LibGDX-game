package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.EntityModel;
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
	protected void completion(EntityModel target) {
		super.completion(target);
		
		target.acceleration.y = oldAccel;

	}
	
	@Override
	protected void initialProcess(EntityModel target) {
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
		return EntityEffect.MediumPriority;
	}
	
	public boolean shouldUseFor(EntityModel target) {
		return target instanceof CharacterModel;
	}

	@Override
	public boolean shouldReciprocateToSource(EntityModel target, ActionListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void flipValues() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flipValuesIfNecessary(EntityModel target, EntityModel source) {
		
	}

	@Override
	public boolean shouldAddIfIntercepted() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public boolean onlyForCharacters() {
		return true;
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
