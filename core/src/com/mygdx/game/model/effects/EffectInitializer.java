package com.mygdx.game.model.effects;

import com.mygdx.game.model.effects.EffectSettings;

public class EffectInitializer {
	public static Effect initializeEffect(EffectSettings settings, EffectDataRetriever retriever) {
		Effect effect = null;
		switch (settings.type) {
		case DAMAGE:
			effect = new DamageEffect(settings, retriever);
			break;
		case HEALING:
			effect = new HealingEffect(settings, retriever);
			break;
		case MOVEMENT:
			effect = new MovementEffect(settings, retriever);
			break;
		case STABILITYDMG:
			effect = new StabilityDamageEffect(settings, retriever);
			break;
		case ITEMGIVE:
			effect = new ItemEffect(settings, retriever);
			break;
		}
		return effect;
	}
}
