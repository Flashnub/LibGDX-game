package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.Effect.EffectType;

public class StabilityDamageEffect extends Effect {
		
	private StabilityDamageEffectSettings sEffectSettings;
	
	public StabilityDamageEffect(EffectSettings settings, EffectDataRetriever retriever) {
		super(settings, retriever);
		
		if (settings instanceof StabilityDamageEffectSettings) {
			sEffectSettings = (StabilityDamageEffectSettings) settings;
		}
	}
	
	protected void processDuringActive(CharacterModel target, float delta) {
		if (sEffectSettings.isInstantaneous) {
			target.removeFromCurrentStability(sEffectSettings.value, retriever.getReplacementMovement());
		}
		else {
			target.removeFromCurrentStability((int) (sEffectSettings.value * (delta / sEffectSettings.duration)), retriever.getReplacementMovement());
		}
	}

}
