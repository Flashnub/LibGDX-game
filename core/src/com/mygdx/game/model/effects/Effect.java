package com.mygdx.game.model.effects;

import java.util.UUID;

public abstract class Effect {
	Boolean isActive;
	private boolean hasProcessedInitial;
	private boolean hasProcessedCompletion;
	protected boolean forceEnd;
	private float currentTime;
	private EffectController retriever;
	EffectSettings settings;
	String uuid;
	
	public Effect() {
		setCurrentTime(0f);
		this.isActive = false;
		this.setHasProcessedInitial(false);
		this.setHasProcessedCompletion(false);
		this.setForceEnd(false);
		UUID id = UUID.randomUUID();
		this.uuid = id.toString();
	}
	
	public Effect(EffectSettings settings) {
		this();
		this.settings = settings;

	}
	

	public EffectController getController() {
		return getRetriever();
	}

	public void setController(EffectController retriever) {
		this.setRetriever(retriever);
	}

	
	protected void initialProcess() {
		this.isActive = true;
		this.setHasProcessedInitial(true);
	}
	
	protected void processDuringActive(float delta) {
		
	}
	
	protected void completion() {
		this.setHasProcessedCompletion(true);
	}
	
	public float remainingDuration() {
		if (getCurrentTime() <= this.settings.getDelayToActivate()) {
			return this.settings.getDuration();
		}
		else {
			return this.settings.getDuration() - (this.getCurrentTime() - this.settings.getDelayToActivate());
		}
	}
	
	public abstract String getType();
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Effect) {
			return ((Effect) obj).uuid.equals(this.uuid); 
		}
		return super.equals(obj);
	}


	public float getCurrentTime() {
		return currentTime;
	}

	public void setCurrentTime(float currentTime) {
		this.currentTime = currentTime;
	}

	public boolean hasProcessedInitial() {
		return hasProcessedInitial;
	}

	public void setHasProcessedInitial(boolean hasProcessedInitial) {
		this.hasProcessedInitial = hasProcessedInitial;
	}

	public boolean isForceEnd() {
		return forceEnd;
	}

	public void setForceEnd(boolean forceEnd) {
		this.forceEnd = forceEnd;
	}

	public boolean hasProcessedCompletion() {
		return hasProcessedCompletion;
	}

	public void setHasProcessedCompletion(boolean hasProcessedCompletion) {
		this.hasProcessedCompletion = hasProcessedCompletion;
	}

	public EffectController getRetriever() {
		return retriever;
	}

	public void setRetriever(EffectController retriever) {
		this.retriever = retriever;
	}
	
	public boolean isActive() {
		return this.isActive;
	}
}
