package com.mygdx.game.model.globalEffects;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EffectController;
import com.mygdx.game.model.events.CollisionChecker;
import com.mygdx.game.model.projectiles.Projectile;

public class ProjectileCreateEffect extends WorldEffect {

	public static final String type = "ProjectileCreateEffect";
	ProjectileCreateEffectSettings pSettings;
	Projectile projectile;
	CharacterModel target;
	Vector2 originOverride;	

	public ProjectileCreateEffect(WorldEffectSettings settings, EffectController retriever, CollisionChecker collisionChecker, CharacterModel source, CharacterModel target, Vector2 originOverride) {
		super(settings, retriever, collisionChecker, source);
		if (settings instanceof ProjectileCreateEffectSettings) {
			this.pSettings = (ProjectileCreateEffectSettings) settings;
		}
		Projectile projectile = new Projectile(pSettings.projectileSettingKey, source, target, actionListener, collisionChecker);
		this.projectile = projectile;
		this.originOverride = originOverride;
		this.target = target;
	}

	@Override
	public String getType() {
		return ProjectileCreateEffect.type;
	}
	
	@Override
	protected void initialProcess() {
		super.initialProcess();	
		projectile.setStartingPosition(originOverride, pSettings.getOrigin(source.isFacingLeft()));
		this.actionListener.addProjectile(projectile);
	}

	@Override
	public float getEffectiveRange() {
		return projectile.getEffectiveRange();
	}
	
	

}
