package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.actions.Attack;
import com.mygdx.game.model.actions.HitTracker;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.AssaultInterceptor;
import com.mygdx.game.model.projectiles.Explosion;
import com.mygdx.game.model.projectiles.Projectile;

public class BlockEffect extends EntityEffect implements AssaultInterceptor{
	
	boolean isPerfect;
	BlockEffectSettings bSettings;
	public static final String type = "Block";

	public BlockEffect(EffectSettings settings, EffectController retriever) {
		super(settings, retriever);
		if (settings instanceof BlockEffectSettings) {
			bSettings = (BlockEffectSettings) settings;
		}
		this.isPerfect = true;
	}

	@Override
	protected void processDuringActive(CharacterModel target, float delta) {
		super.processDuringActive(target, delta);
		if (isPerfect && isActive && this.getActiveTime() > this.bSettings.perfectBlockTime) {
			isPerfect = false;
		}
	}
	
	@Override
	public boolean didInterceptAttack(CharacterModel target, Attack attack) {
		if (this.isActive) {
			float potentialWill = this.calculateWillFromEffects(attack.getAttackSettings().getTargetEffectSettings());
			target.addToCurrentWill(potentialWill);
			attack.getAlreadyHitCharacters().add(new HitTracker(target));
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
			projectile.collisionCheck();
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

	@Override
	public boolean didInterceptExplosion(CharacterModel target, Explosion explosion) {
		if (this.isActive) {
			float potentialWill = this.calculateWillFromEffects(explosion.getExplosionSettings().getTargetEffects());
			target.addToCurrentWill(potentialWill);
			explosion.getAlreadyHitCharacters().add(target);
			if (!isPerfect) {
				//calculate damage
				target.removeFromCurrentHealth(1);
			}
			return true;
		}
		return false;
	}

	@Override
	public String getType() {
		return BlockEffect.type;
	}

	@Override
	public boolean shouldReciprocateToSource(CharacterModel target, ActionListener listener) {
		return false;
	}

	@Override
	public void flipValues() {
		
	}

	@Override
	public boolean shouldAddIfIntercepted() {
		return false;
	}

	@Override
	public void flipValuesIfNecessary(CharacterModel target, CharacterModel source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isUniqueEffect() {
		return true;
	}


	
}
