package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class HealingEffect extends EntityEffect {
	HealingEffectSettings hSettings;
	public static final String type = "Healing";
	
	public HealingEffect(EffectSettings settings, EffectController retriever) {
		super(settings, retriever);
		if (settings instanceof DamageEffectSettings) {
			this.hSettings = (HealingEffectSettings) settings;
		}
	}
	
	@Override
	protected void processDuringActive(CharacterModel target, float delta) {
		super.processDuringActive(target, delta);
		float healing = hSettings.isInstantaneous().booleanValue() ? hSettings.value : (hSettings.value * Math.min((delta / hSettings.getDuration()), 1f));
		target.addToCurrentHealth(healing);

	}

	@Override
	public String getType() {
		return HealingEffect.type;
	}

}
