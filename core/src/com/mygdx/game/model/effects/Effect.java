package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;

public abstract class Effect {
	EffectSettings settings;
	Boolean isActive;
	float currentTime;
	
	public Effect(EffectSettings settings) {
		currentTime = 0f;
		this.isActive = false;
		this.settings = settings;
	}
	
	public enum EffectType {
		MOVEMENT, DAMAGE, HEALING, ITEMGIVE
	}
	
	
	
	public void initialProcess(CharacterModel target) {
		
	}
	public void completion(CharacterModel target) {
		
	}
	
	public boolean process(CharacterModel target, float delta) {
		boolean isFinished = false;
		if (currentTime >= settings.delayToActivate || settings.isInstantaneous) {
			isActive = true;
		}
		if (currentTime >= (settings.duration + settings.delayToActivate) || settings.isInstantaneous) {
			isFinished = true;
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
