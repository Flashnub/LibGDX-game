package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.Effect;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.events.ActionListener;

public class Ability extends ActionSegment{
	AbilitySettings settings;
	Array <Effect> sourceEffects;
	
	public Ability(CharacterModel source, AbilitySettings settings) {
		super();
		this.settings = settings;
		this.source = source;
		this.sourceEffects = new Array <Effect>();
	}
	
	@Override
	public void sendActionToListener(ActionListener actionListener) {
		// Do nothing.
		
	}
	
	public void sourceProcessWithoutSuper(CharacterModel source) {
		for (EffectSettings effectSettings : settings.sourceEffectSettings) {
			Effect effect = EffectInitializer.initializeEffect(effectSettings);
			source.addEffect(effect);
			sourceEffects.add(effect);
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
		if (this.forceInterrupt) {
			return this.settings.windupTime + this.stateTime + this.settings.cooldownTime;
		}
		return this.settings.windupTime + this.settings.duration + this.settings.cooldownTime;
	}

	@Override
	public void interruptionBlock() {
		for(Effect effect : this.sourceEffects) {
			effect.setForceInterrupt(true);
		}
	}



}
