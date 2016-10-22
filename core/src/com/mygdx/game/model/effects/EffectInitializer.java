package com.mygdx.game.model.effects;

import com.mygdx.game.model.effects.EffectSettings;

public class EffectInitializer {
	public static Effect initializeEffect(EffectSettings settings) {
		Effect effect = null;
		switch (settings.type) {
		case DAMAGE:
			effect = new DamageEffect(settings);
			break;
		case HEALING:
			effect = new HealingEffect(settings);
			break;
		case MOVEMENT:
			effect = new MovementEffect(settings);
			break;
		}
		return effect;
	}
}
