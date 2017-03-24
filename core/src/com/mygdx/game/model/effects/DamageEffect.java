package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.characters.enemies.Enemy.EnemyModel;
import com.mygdx.game.model.events.ActionListener;

public class DamageEffect extends EntityEffect{
	DamageEffectSettings dSettings;
	public static final String type = "Damage";
	
	public DamageEffect(EffectSettings settings, EffectController retriever) {
		super(settings, retriever);
		if (settings instanceof DamageEffectSettings) {
			this.dSettings = (DamageEffectSettings) settings;
		}
	}

	protected void processDuringActive(EntityModel target, float delta) {
		super.processDuringActive(target, delta);
		float damage = dSettings.isInstantaneous().booleanValue() ? dSettings.value : (dSettings.value * Math.min((delta / dSettings.getDuration()), 1f));
		target.removeFromCurrentHealth(damage);
		if (target instanceof EnemyModel) {
			EnemyModel enemyTarget = (EnemyModel) target;
			enemyTarget.checkIfShouldAggroTarget(this.getController().getSource(), damage);
		}
	}
	
	public int getPriority() {
		return EntityEffect.UltraPriority;
	}
	
	@Override
	public String getType() {
		return DamageEffect.type;
	}

	@Override
	public boolean shouldReciprocateToSource(EntityModel target, ActionListener listener) {
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
	public void flipValuesIfNecessary(EntityModel target, EntityModel source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isUniqueEffect() {
		return false;
	}

}
