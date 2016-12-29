package com.mygdx.game.model.globalEffects;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EffectController;
import com.mygdx.game.model.events.CollisionChecker;
import com.mygdx.game.model.projectiles.Explosion;

public class ExplosionCreateEffect extends WorldEffect {
	
	public static final String type = "Explosion";
	ExplosionCreateEffectSettings pSettings;
	Explosion explosion;
	
	
	public ExplosionCreateEffect(WorldEffectSettings settings, EffectController retriever, CollisionChecker collisionChecker, CharacterModel source) {
		super(settings, retriever, collisionChecker, source);
		if (settings instanceof ExplosionCreateEffectSettings) {
			this.pSettings = (ExplosionCreateEffectSettings) settings;
		}
//		Explosion explosion = new Explosion(this.getSettings().getExplosionName(), this.getSettings().getExplosionSettings(),  this.actionListener, this);

	}



	@Override
	protected void initialProcess() {
		super.initialProcess();	
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
