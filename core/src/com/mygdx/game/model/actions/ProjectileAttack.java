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
	CharacterModel source;
	CharacterModel target;
	ActionListener actionListener;
	AbilitySettings settings;
	
	public ProjectileAttack(CharacterModel source, CharacterModel target, ActionListener actionListener, ArrayList<ProjectileSettings> projectileSettingsKeys, AbilitySettings settings) {
		super();
		this.source = source;
		this.target = target;
		this.actionListener = actionListener;
		projectiles = new ArrayList<Projectile>();
		this.settings = settings;
		this.isConcurrent = settings.isConcurrent;
		for (ProjectileSettings projSettings : projectileSettingsKeys) {
			Projectile projectile = new Projectile(projSettings.getName(), source, target, actionListener);
			projectiles.add(projectile);
		}
	}
	
	@Override
	public boolean isFinished() {
		return true;
	}
	
	@Override
	public CharacterModel getSource() {
		return source;
	}

	@Override
	public float getDelayToActivate() {
		return this.settings.delayToActivate;
	}
	
	@Override
	public void sendActionToListener(ActionListener actionListener) {
		for (Projectile projectile : projectiles) {
			actionListener.processProjectile(projectile);
		}
	}

	@Override
	public void sourceProcess(CharacterModel source) {
		super.sourceProcess(source);
		for (EffectSettings effectSettings : settings.sourceEffectSettings) {
			Effect effect = EffectInitializer.initializeEffect(effectSettings);
			source.addEffect(effect);
		}
	}

	@Override
	public ActionSegment cloneActionSegment() {
		ArrayList <ProjectileSettings> projSettings = new ArrayList<ProjectileSettings>();
		for (Projectile projectile : projectiles) {
			projSettings.add(projectile.getSettings());
		}
		ProjectileAttack projAttack = new ProjectileAttack(source, target, actionListener, projSettings, this.settings);
		return projAttack;
	}

}
