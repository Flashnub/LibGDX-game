package com.mygdx.game.model.effects;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.events.CollisionChecker;
import com.mygdx.game.model.globalEffects.DeleteCharacterEffect;
import com.mygdx.game.model.globalEffects.ExplosionCreateEffect;
import com.mygdx.game.model.globalEffects.ProjectileCreateEffect;
import com.mygdx.game.model.globalEffects.SpawnCharacterEffect;
import com.mygdx.game.model.globalEffects.WorldEffect;
import com.mygdx.game.model.globalEffects.WorldEffectSettings;

public class EffectInitializer {
	public static EntityEffect initializeEntityEffect(EffectSettings settings, EffectController controller) {
		EntityEffect effect = null;
		switch (settings.getType()) {
		case DamageEffect.type:
			effect = new DamageEffect(settings, controller);
			break;
		case HealingEffect.type:
			effect = new HealingEffect(settings, controller);
			break;
		case MovementEffect.type:
			effect = new MovementEffect(settings, controller);
			break;
		case StabilityDamageEffect.type:
			effect = new StabilityDamageEffect(settings, controller);
			break;
		case ItemEffect.type:
			effect = new ItemEffect(settings, controller);
			break;
		case BlockEffect.type:
			effect = new BlockEffect(settings, controller);
			break;
		case TensionAddEffect.type:
			effect = new TensionAddEffect(settings, controller);
			break;
		}
		return effect;
	}
	
	public static WorldEffect initializeWorldEffect(WorldEffectSettings settings, EffectController controller, CollisionChecker collisionChecker, CharacterModel source, CharacterModel target, Vector2 originOverride) {
		WorldEffect effect = null;
		switch (settings.getType()) {
		case ExplosionCreateEffect.type:
			effect = new ExplosionCreateEffect(settings, controller, collisionChecker, source, originOverride);
			break;
		case ProjectileCreateEffect.type:
			effect = new ProjectileCreateEffect(settings, controller, collisionChecker, source, target, originOverride);
			break;
		case SpawnCharacterEffect.type:
			effect = new SpawnCharacterEffect(settings, controller, collisionChecker, source, originOverride);
			break;
		case DeleteCharacterEffect.type:
			effect = new DeleteCharacterEffect(settings, controller, collisionChecker, source, originOverride);
			break;
		}
		return effect;
	}
}
