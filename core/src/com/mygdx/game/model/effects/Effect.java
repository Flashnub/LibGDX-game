package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;

public abstract class Effect {
	EffectSettings settings;
	Boolean isActive;
	boolean hasProcessedInitial;
	boolean hasProcessedCompletion;
	boolean forceEnd;
	float currentTime;
	EffectDataRetriever retriever;
	
	public Effect(EffectSettings settings, EffectDataRetriever retriever) {
		currentTime = 0f;
		this.isActive = false;
		this.hasProcessedInitial = false;
		this.hasProcessedCompletion = false;
		this.forceEnd = false;
		this.settings = settings;
		this.retriever = retriever;
	}
	
	public enum EffectType {
		MOVEMENT, DAMAGE, HEALING, ITEMGIVE, STABILITYDMG, BLOCK
	}
	
	
	
	protected void initialProcess(CharacterModel target) {
		this.isActive = true;
		this.hasProcessedInitial = true;
	}
	
	protected void processDuringActive(CharacterModel target, float delta) {
		
	}
	
	protected void completion(CharacterModel target) {
		this.hasProcessedCompletion = true;
	}
	
	public boolean process(CharacterModel target, float delta) {
		boolean isFinished = false;
		if ((currentTime >= settings.delayToActivate || settings.isInstantaneous) && !this.hasProcessedInitial) {
			this.initialProcess(target);
		}
		if ((currentTime > settings.delayToActivate && currentTime < settings.duration + settings.delayToActivate) || settings.isInstantaneous) {
			this.processDuringActive(target, delta);
		}
		if ((currentTime >= (settings.duration + settings.delayToActivate) || settings.isInstantaneous || this.forceEnd) && !this.hasProcessedCompletion) {
			isFinished = true;
			this.completion(target);
		}
		currentTime += delta;
		return isFinished;
	}
	
	public float remainingDuration() {
		if (currentTime <= this.settings.delayToActivate) {
			return this.settings.duration;
		}
		else {
			return this.settings.duration - (this.currentTime - this.settings.delayToActivate);
		}
	}
	
	public float getActiveTime() {
		return this.currentTime - this.settings.delayToActivate;
	}

	
	public float getCurrentTime() {
		return currentTime;
	}

	public void setForceEnd(boolean forceEnd) {
		this.forceEnd = forceEnd;
	}

	
}
