package com.mygdx.game.model.actions;

import java.util.ArrayList;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.Effect;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.projectiles.Projectile;
import com.mygdx.game.model.projectiles.ProjectileSettings;


public class ProjectileAttack extends ActionSegment{
	
	ArrayList<Projectile> projectiles;
	ActionSegment potentialAbility;
	CharacterModel target;
	ActionListener actionListener;
	ProjectileAttackSettings projAttackSettings;
	
	public ProjectileAttack() {
		
	}
	
	public ProjectileAttack(CharacterModel source, CharacterModel target, ActionListener actionListener, ProjectileAttackSettings settings) {
		this.source = source;
		this.target = target;
		this.actionListener = actionListener;
		projectiles = new ArrayList<Projectile>();
		this.projAttackSettings = settings;
		for (ProjectileSettings projSettings : settings.getProjectileSettings()) {
			Projectile projectile = new Projectile(projSettings.getName(), source, target, actionListener);
			projectiles.add(projectile);
		}
		if (settings.getAbilitySettings() != null) {
			if (settings.getAbilitySettings() instanceof AbilitySettings) {
				potentialAbility = new Ability(source, settings.getAbilitySettings());
			}
			else if (settings.getAbilitySettings() instanceof AttackSettings) {
				AttackSettings atkSettings = (AttackSettings) settings.getAbilitySettings();
				Attack attack = new Attack(source, target, atkSettings);
				potentialAbility = attack;
			}
		}
	}
	
	@Override
	public float getEffectiveRange() {
		float range = 0f;
		for (Projectile projectile : projectiles) {
			range += projectile.getEffectiveRange();
		}
		return range;
	}
	
	@Override 
	public float getWindUpPlusActionTime() {
		return this.projAttackSettings.getAbilitySettings().windupTime + this.projAttackSettings.getAbilitySettings().duration;
	}
	
	@Override
	public float getWindUpTime() {
		return this.projAttackSettings.getAbilitySettings().windupTime;
	}
	
	@Override
	public float getTotalTime() {
		return this.projAttackSettings.getAbilitySettings().windupTime + this.projAttackSettings.getAbilitySettings().cooldownTime + this.projAttackSettings.getAbilitySettings().duration;
	}

	
	@Override
	public void sendActionToListener(ActionListener actionListener) {
//		for (Projectile projectile : projectiles) {
//			actionListener.processProjectile(projectile);
//		}
		if (this.potentialAbility instanceof Attack) {
			actionListener.processAttack((Attack) this.potentialAbility);
		}
	}

	@Override
	public void sourceProcess(CharacterModel source) {
		super.sourceProcess(source);
		sourceProcessWithoutSuper(source);
	}
	
	public void sourceProcessWithoutSuper(CharacterModel source) {
		if (this.potentialAbility != null) {
			this.potentialAbility.sourceProcessWithoutSuper(source);
		}
		for (Projectile projectile : projectiles) {
			this.actionListener.addProjectile(projectile);
		}
	}

	@Override
	public ActionSegment cloneActionSegmentWithSourceAndTarget(CharacterModel source, CharacterModel target) {
		ProjectileAttack projAttack = new ProjectileAttack();
		projAttack.source = source;
		projAttack.target = target;
		projAttack.actionListener = actionListener;
		projectiles = new ArrayList<Projectile>();
		projAttack.projAttackSettings = projAttackSettings;
		for (ProjectileSettings projSettings : projAttackSettings.getProjectileSettings()) {
			Projectile projectile = new Projectile(projSettings.getName(), source, target, actionListener);
			projectiles.add(projectile);
		}
		if (projAttackSettings.getAbilitySettings() != null) {
			if (projAttackSettings.getAbilitySettings() instanceof AbilitySettings) {
				projAttack.potentialAbility = new Ability(source, projAttackSettings.getAbilitySettings());
			}
			else if (projAttackSettings.getAbilitySettings() instanceof AttackSettings) {
				AttackSettings atkSettings = (AttackSettings) projAttackSettings.getAbilitySettings();
				Attack attack = new Attack(source, target, atkSettings);
				projAttack.potentialAbility = attack;
			}
		}

		
		return projAttack;
	}

	public void setTarget(CharacterModel target) {
		this.target = target;
		for (Projectile projectile : projectiles) {
			projectile.setTarget(target);
		}
	}

}
