//package com.mygdx.game.model.characters;
//
//import com.badlogic.gdx.math.Vector2;
//import com.mygdx.game.model.characters.Character.CharacterModel;
//
//public class Movement implements ActionSegment {
//	MovementSettings settings;
//	float currentTime;
//	boolean hasExecutedInitial;
//	Vector2 oldAccel;
//
//	
//	public Movement(MovementSettings settings) {
//		this.settings = settings;
//		this.currentTime = 0f;
//		this.hasExecutedInitial = false;
//	}
//	
//	public float remainingDuration() {
//		if (currentTime <= this.settings.delayToActivate) {
//			return this.settings.duration;
//		}
//		else {
//			return this.settings.duration - (this.currentTime - this.settings.delayToActivate);
//		}
//	}
//	
//
//	public void hitProcess(CharacterModel mover) {
//		this.setHasExecutedInitial(true);
//		
//		mover.getVelocity().x = settings.velocity.x;
//		mover.getVelocity().y = settings.velocity.y;	
//		
//		oldAccel.x = mover.acceleration.x;
//		oldAccel.y = mover.acceleration.y;
//		
//		mover.acceleration.x = settings.acceleration.x;
//		mover.acceleration.y = settings.acceleration.y;
//	}
//	
//	public void sourceProcess(CharacterModel mover, float delta) {
//		//apply gravity
//		if (!hasExecutedInitial) {
//			this.hitProcess(mover);
//		}
//		mover.getVelocity().y += mover.getAcceleration().y * delta;
//		
//		mover.gameplayHitBox.width = mover.getImageHitBox().width * mover.gameplayHitBoxWidthModifier;
//		mover.gameplayHitBox.height = mover.getImageHitBox().height * mover.gameplayHitBoxHeightModifier;
//		mover.getVelocity().x = mover.getVelocity().x + mover.getAcceleration().x * delta;
//	}
//	
//	public void completion(CharacterModel mover) {
//		mover.acceleration.x = oldAccel.x;
//		mover.acceleration.y = oldAccel.y;
////		mover.velocity.x = 0;
////		mover.velocity.y = 0; // Necessary?
////		mover.acceleration.x = 0;
//	}
//	
//	public float getSmartDuration() {
//		return this.settings.duration + this.settings.delayToActivate;
//	}
//
//	public boolean isHasExecutedInitial() {
//		return hasExecutedInitial;
//	}
//
//	public void setHasExecutedInitial(boolean hasExecutedInitial) {
//		this.hasExecutedInitial = hasExecutedInitial;
//	}
//
//	public float getCurrentTime() {
//		return currentTime;
//	}
//
//	public void setCurrentTime(float currentTime) {
//		this.currentTime = currentTime;
//	}
//
//	public MovementSettings getSettings() {
//		return settings;
//	}
//
//	public void setSettings(MovementSettings settings) {
//		this.settings = settings;
//	}
//	
//	
//}
