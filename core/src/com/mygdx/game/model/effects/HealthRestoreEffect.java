package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class HealthRestoreEffect extends Effect {

	public HealthRestoreEffect(EffectSettings settings) {
		super(settings);
	}

	@Override
	public boolean process(CharacterModel target, float delta) {
		boolean isFinished = super.process(target, delta);
		if (settings.isInstantaneous) {
			target.addToCurrentHealth(settings.value);
		}
		else if (isActive){
			target.addToCurrentHealth((int) (settings.value * (delta / settings.duration)));
		}
		return isFinished;
	}


}
