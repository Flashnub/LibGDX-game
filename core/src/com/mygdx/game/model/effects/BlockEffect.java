package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.actions.Attack;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.events.AssaultInterceptor;
import com.mygdx.game.model.projectiles.Projectile;

public class BlockEffect extends Effect implements AssaultInterceptor{
	
	boolean isPerfect;
	BlockEffectSettings bSettings;

	public BlockEffect(EffectSettings settings, EffectDataRetriever retriever) {
		super(settings, retriever);
		if (settings instanceof BlockEffectSettings) {
			bSettings = (BlockEffectSettings) settings;
		}
		this.isPerfect = true;
	}

	@Override
	protected void processDuringActive(CharacterModel target, float delta) {
		if (isPerfect && isActive && this.getActiveTime() > this.bSettings.perfectBlockTime) {
			isPerfect = false;
		}
	}
	
	@Override
	public boolean didInterceptAttack(CharacterModel target, Attack attack) {
		if (this.isActive) {
			float potentialWill = this.calculateWillFromEffects(attack.getAttackSettings().getTargetEffectSettings());
			target.addToCurrentWill(potentialWill);
			attack.getAlreadyHitCharacters().add(target);
			if (!isPerfect) {
				//calculate damage
				target.removeFromCurrentHealth(1);
			}
			return true;
		}
		return false;
	}
	
	@Override
	public boolean didInterceptProjectile(CharacterModel target, Projectile projectile) {
		if (this.isActive) {
			float potentialWill = this.calculateWillFromEffects(projectile.getSettings().getTargetEffects());
			target.addToCurrentWill(potentialWill);
			projectile.explosionCheck();
			if (!isPerfect) {
				//calculate damage
				target.removeFromCurrentHealth(1);
			}
			return true;
		}
		return false;
	}
	
	public float calculateWillFromEffects(Array <EffectSettings> targetEffects) {
		float totalWill = 0f;
		for (EffectSettings eSettings : targetEffects) {
			if (eSettings instanceof WillGenerator) {
				totalWill += ((WillGenerator) eSettings).getPotentialWill();
			}
		}
		return totalWill;
	}


	
}
