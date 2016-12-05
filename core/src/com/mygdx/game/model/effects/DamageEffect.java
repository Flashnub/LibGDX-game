package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class DamageEffect extends Effect{
	DamageEffectSettings dSettings;
	
	public DamageEffect(EffectSettings settings) {
		super(settings);
		if (settings instanceof DamageEffectSettings) {
			this.dSettings = (DamageEffectSettings) settings;
		}
	}

	protected void processDuringActive(CharacterModel target, float delta) {
		if (dSettings.isInstantaneous) {
			target.removeFromCurrentHealth(dSettings.value);
		}
		else {
			target.removeFromCurrentHealth((int) (dSettings.value * (delta / dSettings.duration)));
		}
	}



}
