package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;

public abstract class EntityEffect extends Effect {

	
	public EntityEffect(EffectSettings settings, EffectController retriever) {
		super(settings);
		this.setRetriever(retriever);
	}
	
	public enum EffectType {
		MOVEMENT, DAMAGE, HEALING, ITEMGIVE, STABILITYDMG, BLOCK
	}
	
	
	
	protected void initialProcess(CharacterModel target) {
		super.initialProcess();
	}
	
	protected void processDuringActive(CharacterModel target, float delta) {
		super.processDuringActive(delta);
	}
	
	protected void completion(CharacterModel target) {
		super.completion();
	}
	
	public boolean process(CharacterModel target, float delta) {
		boolean isFinished = false;
		if ((getCurrentTime() >= settings.getDelayToActivate() || settings.isInstantaneous()) && !this.hasProcessedInitial()) {
			this.initialProcess(target);
		}
		if ((getCurrentTime() > settings.getDelayToActivate() && getCurrentTime() < settings.getDuration() + settings.getDelayToActivate()) || settings.isInstantaneous()) {
			this.processDuringActive(target, delta);
		}
		if ((getCurrentTime() >= (settings.getDuration() + settings.getDelayToActivate()) || settings.isInstantaneous() || this.isForceEnd()) && !this.hasProcessedCompletion()) {
			isFinished = true;
			this.completion(target);
		}
		setCurrentTime(getCurrentTime() + delta);
		return isFinished;
	}
	
	public float remainingDuration() {
		if (getCurrentTime() <= this.settings.getDelayToActivate()) {
			return this.settings.getDuration();
		}
		else {
			return this.settings.getDuration() - (this.getCurrentTime() - this.settings.getDelayToActivate());
		}
	}
	
	public float getActiveTime() {
		return this.getCurrentTime() - this.settings.getDelayToActivate();
	}

	public void setForceEnd(boolean forceEnd) {
		this.forceEnd = forceEnd;
	}

	
}
