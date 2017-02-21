package com.mygdx.game.model.globalEffects;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EffectController;
import com.mygdx.game.model.events.CollisionChecker;

public class DeleteCharacterEffect extends WorldEffect {
	
	public static final String type = "DeleteCharacterEffect";
	DeleteCharacterEffectSettings dSettings;

	public DeleteCharacterEffect(WorldEffectSettings settings, EffectController retriever,
			CollisionChecker collisionChecker, CharacterModel source) {
		super(settings, retriever, collisionChecker, source);
		if (settings instanceof DeleteCharacterEffectSettings) {
			dSettings = (DeleteCharacterEffectSettings) settings;
		}
		
	}
	
	@Override
	protected void completion() {
		super.completion();	
		this.actionListener.deleteCharacter(this.getController().getSource());
	}

	@Override
	public float getEffectiveRange() {
		return Float.MAX_VALUE;
	}

	@Override
	public String getType() {
		return type;
	}



}
