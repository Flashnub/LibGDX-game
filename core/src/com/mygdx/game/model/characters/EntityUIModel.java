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
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.projectiles.Projectile;
import com.mygdx.game.model.worldObjects.WorldObject;

//=============================================================//
//-----------------------------UI------------------------------//
//=============================================================//
	
public class EntityUIModel {
		
	private ObjectMap <String, Animation> animations;
	private EntityUIData entityUIData;
	private TextureRegion currentFrame;
	private float animationTime = 0f;
	private String entityName;
		
	public EntityUIModel(String name, EntityUIDataType type) {
		loadSprites(name, type);
		this.entityName = name;
	}
		
	private void loadSprites(String name, EntityUIDataType type) {
		this.entityUIData = JSONController.loadUIDataFromJSONForEntity(name, type);
			
		loadAnimations(name);
	}
		
	private void loadAnimations(String name) {
		if (entityUIData != null) {
			animations = new ObjectMap <String, Animation>();
			TextureAtlas playerAtlas = entityUIData.getMasterAtlas();
			
			for (AnimationData animationData : entityUIData.getAnimations()) {
				Array <TextureRegion> leftAnimationFrames = new Array<TextureRegion>();
				Array <TextureRegion> rightAnimationFrames = new Array<TextureRegion>();
					
				System.out.println(animationData.getName());
					
				for (int i = 0; i < animationData.getNumberOfFrames(); i++) {
					leftAnimationFrames.add(playerAtlas.findRegion(SpriteUtils.animationStringWithData(name, animationData, i+1)));
					
					TextureRegion rightFrame = playerAtlas.findRegion(SpriteUtils.animationStringWithData(name, animationData, i+1));

					rightFrame.flip(true, false);
					rightAnimationFrames.add(rightFrame);
				}

				Animation leftAnimation = new Animation(animationData.getDuration(), leftAnimationFrames, animationData.getPlayMode());
				Animation rightAnimation = new Animation(animationData.getDuration(), rightAnimationFrames, animationData.getPlayMode());

				animations.put(name + "-" + animationData.getName() + SpriteUtils.left, leftAnimation);
				animations.put(name + "-" + animationData.getName() + SpriteUtils.right, rightAnimation);
			}
		}
	}
		
	public void setCurrentFrame(CharacterModel model, float delta) {
		String animationString = SpriteUtils.animationStringWithState(this.entityName, model.state, model.isFacingLeft());
		Animation currentAnimation = animations.get(animationString);
		
		this.animationTime += delta;
		currentFrame = currentAnimation.getKeyFrame(this.animationTime);
			
		model.imageHitBox.width = currentFrame.getRegionWidth();
		model.imageHitBox.height = currentFrame.getRegionHeight();
	}
	
	public void setCurrentFrame(Projectile projectile, float delta) {
		String animationString = SpriteUtils.animationStringWithState(this.entityName, projectile.getProjectileState(), projectile.isFacingLeft());
		Animation currentAnimation = animations.get(animationString);
			
		this.animationTime += delta;
		currentFrame = currentAnimation.getKeyFrame(this.animationTime);
		
		projectile.getImageHitBox().width = currentFrame.getRegionWidth();
		projectile.getImageHitBox().height = currentFrame.getRegionHeight();
	}
	
	public void setCurrentFrame(WorldObject object, float delta) {
		String animationString = SpriteUtils.animationStringWithState(this.entityName, object.getState());
		Animation currentAnimation = animations.get(animationString);
			
		this.animationTime += delta;
		currentFrame = currentAnimation.getKeyFrame(this.animationTime);
		
		object.getBounds().width = currentFrame.getRegionWidth();
		object.getBounds().height = currentFrame.getRegionHeight();
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
