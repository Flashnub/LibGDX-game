package com.mygdx.game.model.characters;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.mygdx.game.assets.AnimationData;
import com.mygdx.game.assets.EntityUIData;
import com.mygdx.game.assets.SpriteUtils;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.hitSpark.HitSpark;

//=============================================================//
//-----------------------------UI------------------------------//
//=============================================================//
	
public class EntityUIModel {
		
	private ObjectMap <String, Animation<TextureRegion>> animations;
	private EntityUIData entityUIData;
	private TextureRegion currentFrame;
	private float animationTime;
	private float angleOfRotation;
	private boolean shouldStagger;
	public static final float standardStaggerDuration = 0.25f;
	private float staggerTime;
		
	public EntityUIModel(String name, EntityUIDataType type) {
		animationTime = 0f;
		angleOfRotation = 0f;
		staggerTime = 0f;
		loadSprites(name, type);
	}
		
	private void loadSprites(String name, EntityUIDataType type) {
		this.entityUIData = JSONController.loadUIDataFromJSONForEntity(name, type);
			
		loadAnimations();
	}
		
	private void loadAnimations() {
		if (entityUIData != null) {
			animations = new ObjectMap <String, Animation<TextureRegion>>();
			TextureAtlas playerAtlas = entityUIData.getMasterAtlas();
			
			for (AnimationData animationData : entityUIData.getAnimations()) {
				Array <TextureRegion> leftAnimationFrames = new Array<TextureRegion>();
				Array <TextureRegion> rightAnimationFrames = new Array<TextureRegion>();
				
//				if (animationData.getName().equals("Active")) {
//					System.out.println(animationData.getName());
//				}
				//Windup
				for (int i = 0; i < animationData.getNumberOfWindupFrames(); i++) {
					TextureRegion rightFrame = playerAtlas.findRegion(SpriteUtils.animationStringWithData(animationData, SpriteUtils.windupState, i+1));
					rightAnimationFrames.add(rightFrame);
					
					if (rightFrame == null) {
						System.out.println(SpriteUtils.animationStringWithData(animationData, SpriteUtils.activeState, i+1));
						System.out.println(entityUIData.getMasterAtlasPath());
					}
					else {
						TextureRegion leftFrame = new TextureRegion(rightFrame);
						leftFrame.flip(true, false);
						leftAnimationFrames.add(leftFrame);
					}
				}
				//Active
				for (int i = 0; i < animationData.getNumberOfActiveFrames(); i++) {
					TextureRegion rightFrame = playerAtlas.findRegion(SpriteUtils.animationStringWithData(animationData, SpriteUtils.activeState, i+1));
					rightAnimationFrames.add(rightFrame);
					
					if (rightFrame == null) {
						System.out.println(SpriteUtils.animationStringWithData(animationData, SpriteUtils.activeState, i+1));
						System.out.println(entityUIData.getMasterAtlasPath());
					}
					else {
						TextureRegion leftFrame = new TextureRegion(rightFrame);
						leftFrame.flip(true, false);
						leftAnimationFrames.add(leftFrame);
					}

				}
				//Cooldown
				for (int i = 0; i < animationData.getNumberOfCooldownFrames(); i++) {
					TextureRegion rightFrame = playerAtlas.findRegion(SpriteUtils.animationStringWithData(animationData, SpriteUtils.cooldownState, i+1));
					rightAnimationFrames.add(rightFrame);
					
					if (rightFrame == null) {
						System.out.println(SpriteUtils.animationStringWithData(animationData, SpriteUtils.activeState, i+1));
						System.out.println(entityUIData.getMasterAtlasPath());
					}
					else {
						TextureRegion leftFrame = new TextureRegion(rightFrame);
						leftFrame.flip(true, false);
						leftAnimationFrames.add(leftFrame);
					}
				}
				
				
				Animation <TextureRegion>  leftAnimation = new Animation<TextureRegion> (animationData.getFrameRate(), leftAnimationFrames, animationData.getPlayMode());
				Animation <TextureRegion>  rightAnimation = new Animation<TextureRegion> (animationData.getFrameRate(), rightAnimationFrames, animationData.getPlayMode());

				animations.put(animationData.getName() + SpriteUtils.left, leftAnimation);
				animations.put(animationData.getName() + SpriteUtils.right, rightAnimation);
			}
		}
	}
	
	public boolean setCurrentFrame(EntityModel entity, float delta, float angleOfRotation) {
		boolean result = this.setCurrentFrame(entity, delta);
		this.angleOfRotation = angleOfRotation;
		return result;
	}
	
	public boolean setCurrentFrame(EntityModel entity, float delta) {
		String animationString = SpriteUtils.animationStringWithState(entity.getState(), entity.isFacingLeft()); //5PW doesn't work here. Needs 5P.
		Animation <TextureRegion> currentAnimation = animations.get(animationString);
		if (currentAnimation == null) {
			animationString = SpriteUtils.animationStringWithState(CharacterConstants.idleState, entity.isFacingLeft());
			currentAnimation = animations.get(animationString);
		}
		
		this.checkForStagger(delta);
		
		TextureRegion frame = currentAnimation.getKeyFrame(this.animationTime);
		if (frame != null) {
			currentFrame = frame;
		}
	
		entity.getImageHitBox().width = currentFrame.getRegionWidth();
		entity.getImageHitBox().height = currentFrame.getRegionHeight();
		
		return this.isFinishedCurrentAnimation(currentAnimation);
	}
	
	private void checkForStagger(float delta) {
		if (this.shouldStagger) {
			this.staggerTime += delta;
			if (this.staggerTime >= EntityUIModel.standardStaggerDuration) {
				this.shouldStagger = false;
			}
		}
		else {
			this.staggerTime = 0f;
			this.animationTime += delta;
		}
	}
	
	public boolean setCurrentFrame(HitSpark hitSpark, float delta) {
		String animationString = SpriteUtils.animationStringWithState(hitSpark.getSize(), false);
		Animation <TextureRegion> currentAnimation = animations.get(animationString);

		if (currentAnimation != null) {
			this.checkForStagger(delta);

			TextureRegion frame = currentAnimation.getKeyFrame(this.animationTime);
			if (frame != null) {
				currentFrame = frame;
			}
			
			if (this.currentFrame != null) {
				hitSpark.getImageBounds().width = this.currentFrame.getRegionWidth();
				hitSpark.getImageBounds().height = this.currentFrame.getRegionHeight();
			}
		}
		
		return currentAnimation != null ? this.isFinishedCurrentAnimation(currentAnimation) : false;
	}

	private boolean isFinishedCurrentAnimation(Animation<TextureRegion> currentAnimation) {
		return this.animationTime > currentAnimation.getAnimationDuration();
	}

	public TextureRegion getCurrentFrame() {
		return currentFrame;
	}

	public float getAnimationTime() {
		return animationTime;
	}

	public void setAnimationTime(float animationTime) {
		this.animationTime = animationTime;
	}

	public float getAngleOfRotation() {
		return angleOfRotation;
	}
	
	public void stagger() {
		this.shouldStagger = true;
		this.staggerTime = 0f;
	}
	
	public void endStagger() {
		this.shouldStagger = false;
		this.staggerTime = 0f;
	}
	
	public float getTimeForAnimation(String animationName, String animationType){
		for (AnimationData data : this.entityUIData.getAnimations()) {
			if (data.getName().equals(animationName)) {
				if (animationType.equals(SpriteUtils.windupState)) {
					return data.getNumberOfWindupFrames() * data.getFrameRate();
				}
				else if (animationType.equals(SpriteUtils.activeState)) {
					return data.getNumberOfActiveFrames() * data.getFrameRate();
				}
				else if (animationType.equals(SpriteUtils.cooldownState)) {
					return data.getNumberOfCooldownFrames() * data.getFrameRate();
				}
			}
		}
		return 0f;
	}
}
