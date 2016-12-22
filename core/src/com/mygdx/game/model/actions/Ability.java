package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.Effect;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.MovementEffectSettings;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.events.ActionListener;

public class Ability extends ActionSegment{
	AbilitySettings settings;
	Array <Effect> activeSourceEffects;
	Array <Effect> windupSourceEffects;
	
	public Ability(CharacterModel source, AbilitySettings settings) {
		super();
		this.settings = settings.deepCopy();
		this.source = source;
		this.activeSourceEffects = new Array <Effect>();
		this.windupSourceEffects = new Array <Effect>();
	}
	
	public Ability(CharacterModel source, AbilitySettings settings, MovementEffectSettings replacementMovement) {
		this(source, settings);
		this.settings.replaceMovementIfNecessary(replacementMovement);
	}
	
	@Override
	public void sendActionToListener(ActionListener actionListener, float delta) {
		// Do nothing.
		
	}
	
	public void sourceActiveProcessWithoutSuper(CharacterModel source) {
		for (EffectSettings effectSettings : settings.sourceEffectSettings) {
			Effect effect = EffectInitializer.initializeEffect(effectSettings, this);
			source.addEffect(effect);
			activeSourceEffects.add(effect);
		}
	}
	
	public void sourceWindupProcessWithoutSuper(CharacterModel source) {
		for (EffectSettings effectSettings : settings.windupEffectSettings) {
			Effect effect = EffectInitializer.initializeEffect(effectSettings, this);
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
		Ability ability = new Ability (source, settings);
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
		for(Effect effect : this.activeSourceEffects) {
			effect.setForceEnd(true);
		}
		for (Effect effect : this.windupSourceEffects) {
			effect.setForceEnd(true);
		}
	}

	@Override
	public MovementEffectSettings getReplacementMovementForStagger() {
		return null;
	}

}
