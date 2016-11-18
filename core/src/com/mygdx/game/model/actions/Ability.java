package com.mygdx.game.model.actions;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.Effect;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.events.ActionListener;

public class Ability extends ActionSegment{
	AbilitySettings settings;

	public Ability(CharacterModel source, AbilitySettings settings) {
		super();
		this.settings = settings;
		this.source = source;
	}
	
	@Override
	public void sendActionToListener(ActionListener actionListener) {
		// Do nothing.
		
	}
	
	public void sourceProcessWithoutSuper(CharacterModel source) {
		for (EffectSettings effectSettings : settings.sourceEffectSettings) {
			Effect effect = EffectInitializer.initializeEffect(effectSettings);
			source.addEffect(effect);
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
		return this.settings.windupTime + this.settings.duration + this.settings.cooldownTime;
	}


}
