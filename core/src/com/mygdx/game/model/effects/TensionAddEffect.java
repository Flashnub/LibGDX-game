package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.events.ActionListener;

public class TensionAddEffect extends EntityEffect {

	public static final String type = "TensionAddEffect"; 
	TensionAddEffectSettings tSettings;
	
	public TensionAddEffect(EffectSettings settings, EffectController controller) {
		super(settings, controller);
		if (settings instanceof TensionAddEffectSettings) {
			tSettings = (TensionAddEffectSettings) settings;
		}
	}
	
	protected void processDuringActive(EntityModel target, float delta) {
		super.processDuringActive(target, delta);
		float tensionDamage = tSettings.isInstantaneous().booleanValue() ? tSettings.value : (tSettings.value * Math.min((delta / tSettings.getDuration()), 1f));
		target.addToCurrentTension(tensionDamage);
	}

	@Override
	public boolean shouldReciprocateToSource(EntityModel target, ActionListener listener) {
		return false;
	}

	@Override
	public void flipValues() {
		
	}

	@Override
	public void flipValuesIfNecessary(EntityModel target, EntityModel source) {
		
	}

	@Override
	public boolean shouldAddIfIntercepted() {
		return false;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public boolean isUniqueEffect() {
		return false;
	}

}
