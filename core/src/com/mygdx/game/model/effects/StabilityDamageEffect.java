package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.events.ActionListener;

public class StabilityDamageEffect extends EntityEffect {
		
	private StabilityDamageEffectSettings sEffectSettings;
	public static final String type = "StabilityDmg";
	
	public StabilityDamageEffect(EffectSettings settings, EffectController retriever) {
		super(settings, retriever);
		
		if (settings instanceof StabilityDamageEffectSettings) {
			sEffectSettings = (StabilityDamageEffectSettings) settings;
		}
	}
	
	protected void processDuringActive(EntityModel target, float delta) {
		super.processDuringActive(target, delta);
		float stabDamage = sEffectSettings.isInstantaneous().booleanValue() ? sEffectSettings.value : (sEffectSettings.value * Math.min((delta / sEffectSettings.getDuration()), 1f));
		target.removeFromCurrentStability(stabDamage, getController().getYReplacementMovementForStagger());
	}

	@Override
	public String getType() {
		return StabilityDamageEffect.type;
	}

	@Override
	public boolean shouldReciprocateToSource(EntityModel target, ActionListener listener) {
		return false;
	}

	@Override
	public void flipValues() {
		
	}
	
	@Override
	public boolean shouldAddIfIntercepted() {
		return false;
	}

	@Override
	public void flipValuesIfNecessary(EntityModel target, EntityModel source) {
		
	}

	@Override
	public boolean isUniqueEffect() {
		return false;
	}

	public int getPriority() {
		return EntityEffect.HighPriority;
	}
}
