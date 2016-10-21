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
	public boolean process(CharacterModel target, float delta) {
		boolean isFinished = super.process(target, delta);
		if (hSettings.isInstantaneous) {
			target.addToCurrentHealth(hSettings.value);
		}
		else if (isActive){
			target.addToCurrentHealth((int) (hSettings.value * (delta / hSettings.duration)));
		}
		return isFinished;
	}


}
