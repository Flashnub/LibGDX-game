package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;

public class HealthRemovalEffect extends Effect{
	
	public HealthRemovalEffect(EffectSettings settings) {
		super(settings);
	}

	@Override
	public boolean process(CharacterModel target, float delta) {
		boolean isFinished = super.process(target, delta);
		if (settings.isInstantaneous) {
			target.removeFromCurrentHealth(settings.value);
		}
		else if (isActive){
			target.removeFromCurrentHealth((int) (settings.value * (delta / settings.duration)));
		}
		return isFinished;
	}




}
