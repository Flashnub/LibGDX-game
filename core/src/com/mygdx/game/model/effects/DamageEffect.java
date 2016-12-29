package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class DamageEffect extends EntityEffect{
	DamageEffectSettings dSettings;
	public static final String type = "Damage";
	
	public DamageEffect(EffectSettings settings, EffectController retriever) {
		super(settings, retriever);
		if (settings instanceof DamageEffectSettings) {
			this.dSettings = (DamageEffectSettings) settings;
		}
	}

	protected void processDuringActive(CharacterModel target, float delta) {
		if (dSettings.isInstantaneous()) {
			target.removeFromCurrentHealth(dSettings.value);
		}
		else {
			target.removeFromCurrentHealth((int) (dSettings.value * (delta / dSettings.getDuration())));
		}
	}

	@Override
	public String getType() {
		return DamageEffect.type;
	}



}
