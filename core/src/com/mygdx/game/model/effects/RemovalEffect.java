package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.events.ActionListener;

public class RemovalEffect extends EntityEffect{

	public static final String type = "RemovalEffect";
	RemovalEffectSettings rSettings;
	
	public RemovalEffect(EffectSettings settings, EffectController controller) {
		super(settings, controller);
		if (settings instanceof RemovalEffectSettings) {
			this.rSettings = (RemovalEffectSettings) settings;
		}
	}
	
	@Override
	protected void initialProcess(EntityModel target) {
		super.initialProcess(target);
		if (rSettings.getIdToRemove() != null) {
			target.removeEffectByID(rSettings.getIdToRemove());
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
	public String getType() {
		return null;
	}

	@Override
	public boolean isUniqueEffect() {
		return false;
	}

}
