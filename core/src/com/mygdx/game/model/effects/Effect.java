package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;

public abstract class Effect {
	EffectSettings settings;
	Boolean isActive;
	boolean hasProcessedInitial;
	boolean hasProcessedCompletion;
	float currentTime;
	
	public Effect(EffectSettings settings) {
		currentTime = 0f;
		this.isActive = false;
		this.hasProcessedInitial = false;
		this.hasProcessedCompletion = false;
		this.settings = settings;
	}
	
	public enum EffectType {
		MOVEMENT, DAMAGE, HEALING, ITEMGIVE
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
		if ((currentTime >= (settings.duration + settings.delayToActivate) || settings.isInstantaneous) && !this.hasProcessedCompletion) {
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

	
	public float getCurrentTime() {
		return currentTime;
	}

//	@Override
//	public void write(Json json) {
//		json.writeValue("isInstantaneous", isInstantaneous);
//		json.writeValue("duration", duration);
//		json.writeValue("delayToActivate", delayToActivate);
//		json.writeValue("value", value);
//	}
//	
//	
//	@Override
//	public void read(Json json, JsonValue jsonData) {
//		isInstantaneous = json.readValue("isInstantaneous", Boolean.class, jsonData);
//		if (isInstantaneous == null || isInstantaneous == true) {
//			isInstantaneous = true;
//			duration = 0f;
//			delayToActivate = 0f;
//		}
//		else {
//			isInstantaneous = false;
//			duration = json.readValue("duration", Float.class, jsonData);
//			Float delayToActivate = json.readValue("delayToActivate", Float.class, jsonData);
//			if (delayToActivate != null) {
//				this.delayToActivate = delayToActivate;
//			}
//			else 
//			{
//				this.delayToActivate = 0f;
//			}
//		}
//		value = json.readValue("value", Integer.class, jsonData);
//		
//		currentTime = 0f;
//		this.isActive = false;
//	}
//	
}
