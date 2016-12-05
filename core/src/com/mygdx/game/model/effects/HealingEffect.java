package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class HealingEffect extends Effect {
	HealingEffectSettings hSettings;
	
	public HealingEffect(EffectSettings settings) {
		super(settings);
		if (settings instanceof DamageEffectSettings) {
			this.hSettings = (HealingEffectSettings) settings;
		}
	}
	
	@Override
	protected void processDuringActive(CharacterModel target, float delta) {
		if (hSettings.isInstantaneous) {
			target.addToCurrentHealth(hSettings.value);
		}
		else {
			target.addToCurrentHealth((int) (hSettings.value * (delta / hSettings.duration)));
		}
	}

}
