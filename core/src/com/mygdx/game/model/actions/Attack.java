package com.mygdx.game.model.actions;

import java.util.Iterator;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EntityEffect;
import com.mygdx.game.model.effects.MovementEffect;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.MovementEffectSettings;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.events.ActionListener;

public class Attack extends ActionSegment {
	 
	Rectangle hitBox;
	int allegiance;
	AttackSettings attackSettings;
	Array <EntityEffect> activeSourceEffects;
	Array <EntityEffect> windupSourceEffects;
	Array <HitTracker> alreadyHitCharacters;
	
	public Attack(CharacterModel source, AttackSettings settings, ActionListener listener) {
		super(listener);
		this.source = source;
		this.allegiance = source.getAllegiance();
		this.attackSettings = settings.deepCopy();
		this.activeSourceEffects = new Array <EntityEffect>();
		this.windupSourceEffects = new Array <EntityEffect>();
		this.alreadyHitCharacters = new Array<HitTracker>();
		if (source.isFacingLeft()) {
			this.hitBox = new Rectangle(source.getGameplayHitBox().x + settings.originX - 10, source.getGameplayHitBox().y + settings.originY, -settings.width, settings.height);
		}
		else {
			this.hitBox = new Rectangle(source.getGameplayHitBox().x + settings.originX + 10, source.getGameplayHitBox().y + settings.originY, settings.width, settings.height);
		}
	}
	
	public void processAttackOnCharacter(CharacterModel target) {
		for (HitTracker tracker : this.alreadyHitCharacters) {
			if (tracker.characterHit.equals(target)){
				return;
			}
		}
		if (!target.checkIfIntercepted(this)) {
			for (EffectSettings effectSettings : this.attackSettings.targetEffectSettings) {
				EntityEffect effect = EffectInitializer.initializeEntityEffect(effectSettings, this);
				target.addEffect(effect);
			}
		}
		target.actionStagger();
		source.actionStagger();
		
		//Stop movementEffect if attack respects collision with target
		if (this.shouldRespectEntityCollisions()) {
			MovementEffect mEffect = source.getCurrentMovement();
			if (mEffect != null) {
				mEffect.setForceEnd(true);
			}
		}
		
		this.alreadyHitCharacters.add(new HitTracker(target));
	}
	
	@Override
	public ActionSegment cloneActionSegmentWithSourceAndTarget(CharacterModel source, CharacterModel target) {
		Attack attack = new Attack(source, attackSettings, this.actionListener);
		return attack;
	}
	
	@Override
	public void sendActionToListener(ActionListener actionListener, float delta) {
		Iterator <HitTracker> iterator = this.alreadyHitCharacters.iterator();
		while(iterator.hasNext()) {
			HitTracker tracker = iterator.next();
			boolean isFinished = tracker.update(delta, this.attackSettings.hitRate);
			if (isFinished) {
				iterator.remove();
			}
		}
		//Need to limit this to X hits per attack.
		actionListener.processAttack(this);
	}
	
	public void sourceActiveProcessWithoutSuper(CharacterModel source) {
		source.setTempIgnoreEntityCollision(this.shouldRespectEntityCollisions());
		for (EffectSettings effectSettings : attackSettings.sourceEffectSettings) {
			EntityEffect effect = EffectInitializer.initializeEntityEffect(effectSettings, this);
			source.addEffect(effect);
			activeSourceEffects.add(effect);
		}
	}
	
	public void sourceWindupProcessWithoutSuper(CharacterModel source) {
		for (EffectSettings effectSettings : attackSettings.windupEffectSettings) {
			EntityEffect effect = EffectInitializer.initializeEntityEffect(effectSettings, this);
			source.addEffect(effect);
			windupSourceEffects.add(effect);
		}
	}
	
	@Override
	public float getEffectiveRange() {
		float range = 0f;
		for (EffectSettings effectSettings : this.attackSettings.sourceEffectSettings) {
			if (effectSettings instanceof MovementEffectSettings) {
				MovementEffectSettings mEffectSettings = (MovementEffectSettings) effectSettings;
				range += mEffectSettings.getEstimatedDistance();
			}
		}
		return range + hitBox.width;
	}

	@Override
	public float getWindUpTime() {
		return this.attackSettings.windupTime;
	}
	
	@Override 
	public float getWindUpPlusActionTime() {
		return this.attackSettings.windupTime + this.attackSettings.duration;
	}
	
	@Override
	public float getTotalTime() {
		if (this.forceCooldownState) {
			return this.attackSettings.windupTime + this.activeTime + this.attackSettings.cooldownTime;
		}
		return this.attackSettings.windupTime + this.attackSettings.duration + this.attackSettings.cooldownTime;
	}
	
	@Override
	public void interruptionBlock() {
		for(EntityEffect effect : this.activeSourceEffects) {
			effect.setForceEnd(true);
		}
		for (EntityEffect effect : this.windupSourceEffects) {
			effect.setForceEnd(true);
		}
	}
	
	public Rectangle getHitBox() {
		return hitBox;
	}

	public int getAllegiance() {
		return allegiance;
	}

	public void setHitBox(Rectangle hitBox) {
		this.hitBox = hitBox;
	}

	public AttackSettings getAttackSettings() {
		return attackSettings;
	}

	public void setSource(CharacterModel source) {
		this.source = source;
	}

	public void setAllegiance(int allegiance) {
		this.allegiance = allegiance;
	}

	public Array<HitTracker> getAlreadyHitCharacters() {
		return alreadyHitCharacters;
	}

	@Override
	public MovementEffectSettings getReplacementMovementForStagger() {
		MovementEffectSettings mSettings = null;
		for (EffectSettings settings : this.attackSettings.targetEffectSettings) {
			if (settings instanceof MovementEffectSettings) {
				mSettings = (MovementEffectSettings) settings;
			}
		}
		return mSettings;
	}

	public boolean shouldStagger() {
		return this.attackSettings.shouldStagger;
	}

	@Override
	public boolean shouldRespectEntityCollisions() {
		return this.attackSettings.sourceRespectEntityCollisions;
	}

}
