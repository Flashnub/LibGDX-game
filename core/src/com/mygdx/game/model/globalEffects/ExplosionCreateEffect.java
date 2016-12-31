package com.mygdx.game.model.globalEffects;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EffectController;
import com.mygdx.game.model.events.CollisionChecker;
import com.mygdx.game.model.projectiles.Explosion;

public class ExplosionCreateEffect extends WorldEffect {
	
	public static final String type = "ExplosionCreateEffect";
	ExplosionCreateEffectSettings pSettings;
	Explosion explosion;
	
	
	public ExplosionCreateEffect(WorldEffectSettings settings, EffectController retriever, CollisionChecker collisionChecker, CharacterModel source, Vector2 originOverride) {
		super(settings, retriever, collisionChecker, source, originOverride);
		if (settings instanceof ExplosionCreateEffectSettings) {
			this.pSettings = (ExplosionCreateEffectSettings) settings;
		}
//		Explosion explosion = new Explosion(this.getSettings().getExplosionName(), this.getSettings().getExplosionSettings(),  this.actionListener, this);
		this.explosion = new Explosion(this.pSettings.explosionKey, this.actionListener, this.pSettings.origin, source.getAllegiance(), originOverride);
		

	}



	@Override
	protected void initialProcess() {
		super.initialProcess();	
		this.actionListener.addExplosion(this.explosion);
	}
	
	@Override
	public String getType() {
		return ExplosionCreateEffect.type;
	}



	@Override
	public float getEffectiveRange() {
		return 0;
	}



}
