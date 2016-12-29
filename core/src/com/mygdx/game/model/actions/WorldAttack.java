package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.effects.EntityEffect;
import com.mygdx.game.model.effects.MovementEffectSettings;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.CollisionChecker;
import com.mygdx.game.model.globalEffects.WorldEffect;
import com.mygdx.game.model.globalEffects.WorldEffectSettings;


public class WorldAttack extends ActionSegment{
	
	Array<WorldEffect> worldEffects;
	ActionSegment potentialAbility;
	CharacterModel target;
	CollisionChecker collisionChecker;
	WorldAttackSettings worldAttackSettings;
	Array <EntityEffect> sourceEffects;
	
	public WorldAttack() {
		super();
	}
	
	public WorldAttack(CharacterModel source, CharacterModel target, ActionListener actionListener, CollisionChecker collisionChecker, WorldAttackSettings settings) {
		super(actionListener);
		this.source = source;
		this.target = target;
		worldEffects = new Array<WorldEffect>();
		this.worldAttackSettings = settings;
		for (WorldEffectSettings worldEffectSettings : this.worldAttackSettings.worldEffectSettings) {
			WorldEffect effect = EffectInitializer.initializeWorldEffect(worldEffectSettings, this, collisionChecker, source, target);
			worldEffects.add(effect);
		}
		if (settings.getAbilitySettings() != null) {
			if (settings.getAbilitySettings() instanceof AbilitySettings) {
				potentialAbility = new Ability(source, settings.getAbilitySettings(), actionListener);
			}
			else if (settings.getAbilitySettings() instanceof AttackSettings) {
				AttackSettings atkSettings = (AttackSettings) settings.getAbilitySettings();
				Attack attack = new Attack(source, atkSettings, actionListener);
				potentialAbility = attack;
			}
		}
		this.sourceEffects = new Array <EntityEffect>();
	}
	
	@Override
	public float getEffectiveRange() {
		float range = 0f;
		for (WorldEffect worldEffect : worldEffects) {
			range += worldEffect.getEffectiveRange();
		}
		return range;
	}
	
	@Override 
	public float getWindUpPlusActionTime() {
		return this.worldAttackSettings.getAbilitySettings().windupTime + this.worldAttackSettings.getAbilitySettings().duration;
	}
	
	@Override
	public float getWindUpTime() {
		return this.worldAttackSettings.getAbilitySettings().windupTime;
	}
	
	@Override
	public float getTotalTime() {
		if (this.forceCooldownState) {
			return this.worldAttackSettings.getAbilitySettings().windupTime + this.activeTime + this.worldAttackSettings.getAbilitySettings().cooldownTime;
		}
		return this.worldAttackSettings.getAbilitySettings().windupTime + this.worldAttackSettings.getAbilitySettings().cooldownTime + this.worldAttackSettings.getAbilitySettings().duration;
	}

	@Override
	public void sendActionToListener(ActionListener actionListener, float delta) {
		if (this.potentialAbility != null) {
			this.potentialAbility.sendActionToListener(actionListener, delta);
		}
	}
	
	public void sourceActiveProcessWithoutSuper(CharacterModel source) {
		if (this.potentialAbility != null) {
			this.potentialAbility.sourceActiveProcessWithoutSuper(source);
		}
		for (WorldEffect worldEffect : worldEffects) {
			this.actionListener.addWorldEffect(worldEffect);
		}
	}
	
	@Override
	public void sourceWindupProcessWithoutSuper(CharacterModel source) {
		if (this.potentialAbility != null) {
			this.potentialAbility.sourceWindupProcessWithoutSuper(source);
		}		
	}

	@Override
	public ActionSegment cloneActionSegmentWithSourceAndTarget(CharacterModel source, CharacterModel target) {
		WorldAttack projAttack = new WorldAttack();
		projAttack.source = source;
		projAttack.target = target;
		projAttack.actionListener = actionListener;
		projAttack.collisionChecker = collisionChecker;
		worldEffects = new Array<WorldEffect>();
		projAttack.worldAttackSettings = worldAttackSettings;
		for (WorldEffectSettings worldEffectSettings : this.worldAttackSettings.worldEffectSettings) {
			WorldEffect effect = EffectInitializer.initializeWorldEffect(worldEffectSettings, this, collisionChecker, source, target);
			worldEffects.add(effect);
		}
		if (worldAttackSettings.getAbilitySettings() != null) {
			if (worldAttackSettings.getAbilitySettings() instanceof AbilitySettings) {
				projAttack.potentialAbility = new Ability(source, worldAttackSettings.getAbilitySettings(), this.actionListener);
			}
			else if (worldAttackSettings.getAbilitySettings() instanceof AttackSettings) {
				AttackSettings atkSettings = (AttackSettings) worldAttackSettings.getAbilitySettings();
				Attack attack = new Attack(source, atkSettings, this.actionListener);
				projAttack.potentialAbility = attack;
			}
		}

		
		return projAttack;
	}

	@Override
	public void interruptionBlock() {
		for(EntityEffect effect : this.sourceEffects) {
			effect.setForceEnd(true);
		}
	}
	
	@Override
	public MovementEffectSettings getReplacementMovementForStagger() {
		return potentialAbility.getReplacementMovementForStagger();
	}


}
