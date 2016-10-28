package com.mygdx.game.model.actions;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.Effect;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.events.ActionListener;

public class Ability extends ActionSegment{
	AbilitySettings settings;
	CharacterModel source;

	public Ability(CharacterModel source, AbilitySettings settings) {
		super();
		this.settings = settings;
		this.source = source;
		this.isConcurrent = settings.isConcurrent;
	}
	
	@Override
	public void sendActionToListener(ActionListener actionListener) {
		// Do nothing.
		
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
		Ability ability = new Ability (source, settings);
		return ability;
	}

	@Override
	public boolean isFinished() {
		return currentTime > this.settings.delayToActivate + this.settings.duration;
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
	public float getEffectiveRange() {
		return Float.MAX_VALUE;
	}




}
