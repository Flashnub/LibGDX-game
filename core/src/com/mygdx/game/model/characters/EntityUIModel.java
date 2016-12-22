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

//=============================================================//
//-----------------------------UI------------------------------//
//=============================================================//
	
public class EntityUIModel {
		
	private ObjectMap <String, Animation> animations;
	private EntityUIData entityUIData;
	private TextureRegion currentFrame;
	private float animationTime = 0f;
		
	public EntityUIModel(String name, EntityUIDataType type) {
		loadSprites(name, type);
	}
		
	private void loadSprites(String name, EntityUIDataType type) {
		this.entityUIData = JSONController.loadUIDataFromJSONForEntity(name, type);
			
		loadAnimations();
	}
		
	private void loadAnimations() {
		if (entityUIData != null) {
			animations = new ObjectMap <String, Animation>();
			TextureAtlas playerAtlas = entityUIData.getMasterAtlas();
			
			for (AnimationData animationData : entityUIData.getAnimations()) {
				Array <TextureRegion> leftAnimationFrames = new Array<TextureRegion>();
				Array <TextureRegion> rightAnimationFrames = new Array<TextureRegion>();
				
//				if (animationData.getName().equals("Active")) {
//					System.out.println(animationData.getName());
//				}
					
				for (int i = 0; i < animationData.getNumberOfFrames(); i++) {
					TextureRegion leftFrame = playerAtlas.findRegion(SpriteUtils.animationStringWithData(animationData, i+1));
					leftAnimationFrames.add(leftFrame);
					
					TextureRegion rightFrame = new TextureRegion(leftFrame);

					rightFrame.flip(true, false);
					rightAnimationFrames.add(rightFrame);
				}
				Animation leftAnimation = new Animation(animationData.getDuration(), leftAnimationFrames, animationData.getPlayMode());
				Animation rightAnimation = new Animation(animationData.getDuration(), rightAnimationFrames, animationData.getPlayMode());

				animations.put(animationData.getName() + SpriteUtils.left, leftAnimation);
				animations.put(animationData.getName() + SpriteUtils.right, rightAnimation);
			}
		}
	}
	
	public boolean setCurrentFrame(EntityModel entity, float delta) {
		String animationString = SpriteUtils.animationStringWithState(entity.getState(), entity.isFacingLeft());
		Animation currentAnimation = animations.get(animationString);
			
		this.animationTime += delta;
		currentFrame = currentAnimation.getKeyFrame(this.animationTime);
		
		entity.getImageHitBox().width = currentFrame.getRegionWidth();
		entity.getImageHitBox().height = currentFrame.getRegionHeight();
		
		return this.isFinishedCurrentAnimation(currentAnimation);
	}
		

	private boolean isFinishedCurrentAnimation(Animation currentAnimation) {
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
}
