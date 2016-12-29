package com.mygdx.game.model.globalEffects;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EffectController;
import com.mygdx.game.model.events.CollisionChecker;
import com.mygdx.game.model.projectiles.Projectile;

public class ProjectileCreateEffect extends WorldEffect {

	public static String type = "Projectile";
	ProjectileCreateEffectSettings pSettings;
	Projectile projectile;

	public ProjectileCreateEffect(WorldEffectSettings settings, EffectController retriever, CollisionChecker collisionChecker, CharacterModel source, CharacterModel target) {
		super(settings, retriever, collisionChecker, source);
		if (settings instanceof ProjectileCreateEffectSettings) {
			this.pSettings = (ProjectileCreateEffectSettings) settings;
		}
		Projectile projectile = new Projectile(pSettings.projectileSettingKey.value, pSettings.origin, source, target, actionListener, collisionChecker);
		this.projectile = projectile;
	}

	@Override
	public String getType() {
		return ProjectileCreateEffect.type;
	}
	
	@Override
	protected void initialProcess() {
		super.initialProcess();	
		this.actionListener.addProjectile(projectile);
		
	}

	@Override
	public float getEffectiveRange() {
		return projectile.getEffectiveRange();
	}
	
	

}
