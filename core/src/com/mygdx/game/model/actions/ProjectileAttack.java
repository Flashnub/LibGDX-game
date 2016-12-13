package com.mygdx.game.model.actions;

import java.util.ArrayList;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.Effect;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.MovementEffectSettings;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.CollisionChecker;
import com.mygdx.game.model.projectiles.Projectile;
import com.mygdx.game.model.projectiles.ProjectileSettings;


public class ProjectileAttack extends ActionSegment{
	
	ArrayList<Projectile> projectiles;
	ActionSegment potentialAbility;
	CharacterModel target;
	ActionListener actionListener;
	CollisionChecker collisionChecker;
	ProjectileAttackSettings projAttackSettings;
	float projectileIterateTime;
	Array <Effect> sourceEffects;
	
	public ProjectileAttack() {
		
	}
	
	public ProjectileAttack(CharacterModel source, CharacterModel target, ActionListener actionListener, CollisionChecker collisionChecker, ProjectileAttackSettings settings) {
		super();
		this.source = source;
		this.target = target;
		this.actionListener = actionListener;
		projectiles = new ArrayList<Projectile>();
		this.projAttackSettings = settings;
		for (ProjectileSettings projSettings : settings.getProjectileSettings()) {
			Projectile projectile = new Projectile(projSettings.getName(), source, target, actionListener, collisionChecker);
			projectiles.add(projectile);
		}
		if (settings.getAbilitySettings() != null) {
			if (settings.getAbilitySettings() instanceof AbilitySettings) {
				potentialAbility = new Ability(source, settings.getAbilitySettings());
			}
			else if (settings.getAbilitySettings() instanceof AttackSettings) {
				AttackSettings atkSettings = (AttackSettings) settings.getAbilitySettings();
				Attack attack = new Attack(source, atkSettings);
				potentialAbility = attack;
			}
		}
		this.projectileIterateTime = 0f;
		this.sourceEffects = new Array <Effect>();
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
		if (this.forceCooldownState) {
			return this.projAttackSettings.getAbilitySettings().windupTime + this.stateTime + this.projAttackSettings.getAbilitySettings().cooldownTime;
		}
		return this.projAttackSettings.getAbilitySettings().windupTime + this.projAttackSettings.getAbilitySettings().cooldownTime + this.projAttackSettings.getAbilitySettings().duration;
	}

	@Override 
	public void update(float delta, ActionListener actionListener) {
		super.update(delta, actionListener);
		if (this.getActionState().equals(ActionState.ACTION)) {
			this.projectileIterateTime += delta;
		}
	}
	
	@Override
	public void sendActionToListener(ActionListener actionListener, float delta) {
//		for (Projectile projectile : projectiles) {
//			actionListener.processProjectile(projectile);
//		}
		if (this.projectileIterateTime > this.projAttackSettings.projectilesOverTime) {
			for (Projectile projectile : projectiles) {
				this.actionListener.addProjectile(projectile);
			}
			this.projectileIterateTime = 0f;
		}
		if (this.potentialAbility != null) {
			this.potentialAbility.sendActionToListener(actionListener, delta);
		}
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
		projAttack.collisionChecker = collisionChecker;
		projectiles = new ArrayList<Projectile>();
		projAttack.projAttackSettings = projAttackSettings;
		for (ProjectileSettings projSettings : projAttackSettings.getProjectileSettings()) {
			Projectile projectile = new Projectile(projSettings.getName(), source, target, actionListener, collisionChecker);
			projectiles.add(projectile);
		}
		if (projAttackSettings.getAbilitySettings() != null) {
			if (projAttackSettings.getAbilitySettings() instanceof AbilitySettings) {
				projAttack.potentialAbility = new Ability(source, projAttackSettings.getAbilitySettings());
			}
			else if (projAttackSettings.getAbilitySettings() instanceof AttackSettings) {
				AttackSettings atkSettings = (AttackSettings) projAttackSettings.getAbilitySettings();
				Attack attack = new Attack(source, atkSettings);
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
	
	@Override
	public void interruptionBlock() {
		for(Effect effect : this.sourceEffects) {
			effect.setForceInterrupt(true);
		}
	}
	
	@Override
	public MovementEffectSettings getReplacementMovement() {
		return potentialAbility.getReplacementMovement();
	}
}
