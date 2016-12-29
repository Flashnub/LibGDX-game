package com.mygdx.game.model.globalEffects;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.Effect;
import com.mygdx.game.model.effects.EffectController;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.CollisionChecker;

public abstract class WorldEffect extends Effect{
	
	ActionListener actionListener;
	CollisionChecker collisionChecker;
	WorldEffectSettings settings;
	CharacterModel source;

	public WorldEffect(WorldEffectSettings settings, EffectController retriever, CollisionChecker collisionChecker, CharacterModel source) {
		super(settings);
		this.actionListener = retriever.getActionListener();
		this.collisionChecker = collisionChecker;
		this.settings = settings;
		this.source = source;
	}
	
	public abstract float getEffectiveRange();
	
	
	public boolean process(float delta) {
		boolean isFinished = false;
		if ((getCurrentTime() >= settings.getDelayToActivate() || settings.isInstantaneous()) && !this.hasProcessedInitial()) {
			this.initialProcess();
		}
		if ((getCurrentTime() > settings.getDelayToActivate() && getCurrentTime() < settings.getDuration() + settings.getDelayToActivate()) || settings.isInstantaneous()) {
			this.processDuringActive(delta);
		}
		if ((getCurrentTime() >= (settings.getDuration() + settings.getDelayToActivate()) || settings.isInstantaneous() || this.isForceEnd()) && !this.hasProcessedCompletion()) {
			isFinished = true;
			this.completion();
		}
		setCurrentTime(getCurrentTime() + delta);
		return isFinished;
	}
	
}
