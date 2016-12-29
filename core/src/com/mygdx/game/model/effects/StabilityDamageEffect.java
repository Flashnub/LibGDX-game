package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class StabilityDamageEffect extends EntityEffect {
		
	private StabilityDamageEffectSettings sEffectSettings;
	public static final String type = "StabilityDmg";
	
	public StabilityDamageEffect(EffectSettings settings, EffectController retriever) {
		super(settings, retriever);
		
		if (settings instanceof StabilityDamageEffectSettings) {
			sEffectSettings = (StabilityDamageEffectSettings) settings;
		}
	}
	
	protected void processDuringActive(CharacterModel target, float delta) {
		if (sEffectSettings.isInstantaneous()) {
			target.removeFromCurrentStability(sEffectSettings.value, getRetriever().getReplacementMovementForStagger());
		}
		else {
			target.removeFromCurrentStability((int) (sEffectSettings.value * (delta / sEffectSettings.getDuration())), getRetriever().getReplacementMovementForStagger());
		}
	}

	@Override
	public String getType() {
		return StabilityDamageEffect.type;
	}

}
