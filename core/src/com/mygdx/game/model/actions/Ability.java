package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EntityEffect;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.MovementEffectSettings;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.events.ActionListener;

public class Ability extends ActionSegment{
	AbilitySettings settings;
	Array <EntityEffect> activeSourceEffects;
	Array <EntityEffect> windupSourceEffects;

	
	public Ability(CharacterModel source, AbilitySettings settings, ActionListener listener) {
		super(listener);
		this.settings = settings.deepCopy();
		this.source = source;
		this.activeSourceEffects = new Array <EntityEffect>();
		this.windupSourceEffects = new Array <EntityEffect>();
	}
	
	public Ability(CharacterModel source, AbilitySettings settings, ActionListener listener, MovementEffectSettings replacementMovement) {
		this(source, settings, listener);
		this.settings.replaceMovementIfNecessary(replacementMovement);
	}
	
	@Override
	public void sendActionToListener(ActionListener actionListener, float delta) {
		// Do nothing.
		
	}
	
	@Override
	public void sourceCompletionWithoutSuper(CharacterModel source) {
//		source.setWidthCoefficient(source.getCharacterProperties().getWidthCoefficient());
//		source.setHeightCoefficient(source.getCharacterProperties().getHeightCoefficient());
//		source.setxOffsetModifier(0f);
//		source.setyOffsetModifier(0f);
		if (!this.shouldRespectEntityCollisions())
			source.setRespectEntityCollision(true);
	}

	
	public void sourceActiveProcessWithoutSuper(CharacterModel source) {
		if (this.settings.tempWidthModifier != null) {
			source.setWidthCoefficient(this.settings.tempWidthModifier.floatValue());
		}
		if (this.settings.tempHeightModifier != null) {
			source.setHeightCoefficient(this.settings.tempHeightModifier.floatValue());
		}
		if (this.settings.xOffsetModifier != null) {
			source.setxOffsetModifier(this.settings.xOffsetModifier.floatValue());
		}
		if (this.settings.yOffsetModifier != null) {
			source.setyOffsetModifier(this.settings.yOffsetModifier.floatValue());
		}
		if (!this.shouldRespectEntityCollisions())
			source.setRespectEntityCollision(false);
		
		for (EffectSettings effectSettings : settings.sourceEffectSettings) {
			EntityEffect effect = EffectInitializer.initializeEntityEffect(effectSettings, this);
			source.addEffect(effect);
			activeSourceEffects.add(effect);
		}
	}
	
	public void sourceWindupProcessWithoutSuper(CharacterModel source) {
		for (EffectSettings effectSettings : settings.windupEffectSettings) {
			EntityEffect effect = EffectInitializer.initializeEntityEffect(effectSettings, this);
			source.addEffect(effect);
			windupSourceEffects.add(effect);
		}
	}

	@Override
	public float getWindUpTime() {
		return this.settings.windupTime;
	}
	
	@Override 
	public float getWindUpPlusActionTime() {
		return this.settings.windupTime + this.settings.duration;
	}

	@Override
	public float getEffectiveRange() {
		return Float.MAX_VALUE;
	}

	@Override
	public ActionSegment cloneActionSegmentWithSourceAndTarget(CharacterModel source, CharacterModel target) {
		Ability ability = new Ability (source, settings, this.actionListener);
		return ability;
	}

	@Override
	public float getTotalTime() {
		if (this.forceCooldownState) {
			return this.settings.windupTime + this.activeTime + this.settings.cooldownTime;
		}
		return this.settings.windupTime + this.settings.duration + this.settings.cooldownTime;
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

	@Override
	public MovementEffectSettings getReplacementMovementForStagger() {
		return null;
	}

	@Override
	public ActionListener getActionListener() {
		return null;
	}

	@Override
	public boolean shouldRespectEntityCollisions() {
		return this.settings.sourceRespectEntityCollisions;
	}


}
