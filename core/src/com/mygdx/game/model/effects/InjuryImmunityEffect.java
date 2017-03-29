package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.events.ActionListener;

public class InjuryImmunityEffect extends EntityEffect {
	
	InjuryImmunityEffectSettings iSettings;
	public static final String type = "InjuryImmunity";
	

	public InjuryImmunityEffect(EffectSettings settings, EffectController controller) {
		super(settings, controller);
		if (settings instanceof InjuryImmunityEffectSettings) {
			this.iSettings = (InjuryImmunityEffectSettings) settings;
		}
	}
	
	protected void initialProcess(EntityModel target) {
		super.initialProcess(target);
		if (target instanceof CharacterModel) {
			CharacterModel cTarget = (CharacterModel) target;
			cTarget.setImmuneToInjury(true);
		}
	}
	
	protected void completion(EntityModel target) {
		super.completion(target);
		if (target instanceof CharacterModel) {
			CharacterModel cTarget = (CharacterModel) target;
			cTarget.setImmuneToInjury(false);
		}
	}

	@Override
	public boolean shouldReciprocateToSource(EntityModel target, ActionListener listener) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void flipValues() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void flipValuesIfNecessary(EntityModel target, EntityModel source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean shouldAddIfIntercepted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isUniqueEffect() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
