package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.events.ActionListener;

public class HealingEffect extends EntityEffect {
	HealingEffectSettings hSettings;
	public static final String type = "Healing";
	
	public HealingEffect(EffectSettings settings, EffectController retriever) {
		super(settings, retriever);
		if (settings instanceof HealingEffectSettings) {
			this.hSettings = (HealingEffectSettings) settings;
		}
	}
	
	@Override
	protected void processDuringActive(EntityModel target, float delta) {
		super.processDuringActive(target, delta);
		float healing = hSettings.isInstantaneous().booleanValue() ? hSettings.value : (hSettings.value * Math.min((delta / hSettings.getDuration()), 1f));
		target.addToCurrentHealth(healing);

	}

	@Override
	public String getType() {
		return HealingEffect.type;
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
}
