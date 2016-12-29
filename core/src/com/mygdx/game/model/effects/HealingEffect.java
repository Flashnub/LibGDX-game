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
		if (hSettings.isInstantaneous()) {
			target.addToCurrentHealth(hSettings.value);
		}
		else {
			target.addToCurrentHealth((int) (hSettings.value * (delta / hSettings.getDuration())));
		}
	}

	@Override
	public String getType() {
		return HealingEffect.type;
	}

}
