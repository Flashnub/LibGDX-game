package com.mygdx.game.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.model.characters.Character;
import com.mygdx.game.renderer.CoordinatesHelper;
import com.mygdx.game.assets.HUDUtils;

public class ResourceBar extends Actor{
	
	public enum ResourceBarType {
		PlayerHealth {
			
			@Override 
			public TextureRegion getForegroundSprite() {
				TextureAtlas atlas = HUDUtils.masterAtlas;
				if (atlas != null) {
					return atlas.findRegion("health-bar");
				}
				return null;
			}
			
			@Override
			public TextureRegion getIntermediateForegroundSprite() {
				TextureAtlas atlas = HUDUtils.masterAtlas;
				if (atlas != null) {
					return atlas.findRegion("health-bar-intermediate");
				}
				return null;
			}
			
			@Override
			public TextureRegion getBackgroundSprite() {
				TextureAtlas atlas = HUDUtils.masterAtlas;
				if (atlas != null) {
					return atlas.findRegion("health-bar-background");
				}
				return null;
			}
		}, PlayerTension {
			
		}, EnemyHealth {
			@Override
			public TextureRegion getForegroundSprite() {
				TextureAtlas atlas = HUDUtils.masterAtlas;
				if (atlas != null) {
					return atlas.findRegion("enemy-health-bar");
				}
				return null;
			}
			
			@Override
			public TextureRegion getIntermediateForegroundSprite() {
				TextureAtlas atlas = HUDUtils.masterAtlas;
				if (atlas != null) {
					return atlas.findRegion("enemy-health-bar-intermediate");
				}
				return null;
			}
			
			@Override
			public TextureRegion getBackgroundSprite() {
				TextureAtlas atlas = HUDUtils.masterAtlas;
				if (atlas != null) {
					return atlas.findRegion("enemy-health-bar-background");
				}
				return null;
			}
			
			
		};
		
		public String getAffiliation() {
			return this.toString();
		}
		
		public TextureRegion getForegroundSprite() {
			TextureAtlas atlas = HUDUtils.masterAtlas;
			if (atlas != null) {
				return atlas.findRegion("health-bar-background");
			}
			return null;
		}
		
		public TextureRegion getIntermediateForegroundSprite() {
			TextureAtlas atlas = HUDUtils.masterAtlas;
			if (atlas != null) {
				return atlas.findRegion("health-bar-intermediate");
			}
			return null;
		}
		
		public TextureRegion getBackgroundSprite() {
			TextureAtlas atlas = HUDUtils.masterAtlas;
			if (atlas != null) {
				return atlas.findRegion("health-bar-background");
			}
			return null;
		}
	}
	
	TextureRegion background;
	TextureRegion intermediateForeground;
	TextureRegion foreground;
	Character owner;
	ResourceBarType type;
	CoordinatesHelper helper;
	
	final float delayToAnimate = 1.5f;
	final float timeToAnimate = 3f;
	float animationTime = 0f;
	float expectedRatio;
	float differenceInRatio;
	boolean isAnimating = false;
	boolean isShowingIntermediate = false;
	
	public static final float enemyHealthBarWidth = 30f;
	
	public ResourceBar(ResourceBarType type, Character character, CoordinatesHelper helper) {
		this.owner = character;
		this.type = type;
		this.foreground = type.getForegroundSprite();
		this.intermediateForeground = type.getIntermediateForegroundSprite();
		this.background = type.getBackgroundSprite();
		this.expectedRatio = this.owner.getCharacterData().getHealthRatio();
		this.differenceInRatio = 0f;
		this.helper = helper;
	}
	
	public void layoutWithViewPort(Viewport viewPort, Batch batch) {
		if (type.getAffiliation().equals(ResourceBarType.PlayerHealth.getAffiliation())) {
			this.setX(viewPort.getScreenWidth() * 0.1f);
			this.setY(viewPort.getScreenHeight() * 0.8f);
			this.setWidth(viewPort.getScreenWidth() * 0.3f);
			this.setHeight(30);
		}
		else if (type.getAffiliation().equals(ResourceBarType.PlayerTension.getAffiliation())) {
			
		}
		else if (type.getAffiliation().equals(ResourceBarType.EnemyHealth.getAffiliation())) {
			Vector2 enemyCoordinates = this.helper.getScreenCoordinatesForCharacter(owner);
			this.setX(enemyCoordinates.x);
			this.setY(enemyCoordinates.y);
			this.setWidth(ResourceBar.enemyHealthBarWidth);
			this.setHeight(5);
		}
	
		batch.draw(background, this.getX(), this.getY(), this.getWidth(), this.getHeight());
		if (this.isShowingIntermediate) {
			batch.draw(intermediateForeground, this.getX(), this.getY(), this.getWidth() * (this.expectedRatio + differenceInRatio), this.getHeight());
		}
		batch.draw(foreground, this.getX(), this.getY(), this.getWidth() * this.expectedRatio, this.getHeight());
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		layoutWithViewPort(this.getStage().getViewport(), batch);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		if (this.owner.getCharacterData().getHealthRatio() != expectedRatio) {
			this.differenceInRatio += expectedRatio - this.owner.getCharacterData().getHealthRatio();
			this.expectedRatio = this.owner.getCharacterData().getHealthRatio();
			this.animationTime = 0f;
			this.isAnimating = false;
			this.isShowingIntermediate = true;
		}
		else if (this.differenceInRatio > 0f) {
			animationTime += delta;
			if (animationTime > delayToAnimate) {
				this.isAnimating = true;
				differenceInRatio = differenceInRatio * (1f - ((animationTime - delayToAnimate) / timeToAnimate));
			}
		}
		else if (animationTime >= this.delayToAnimate + this.timeToAnimate) {
			this.differenceInRatio = 0f;
			this.isAnimating = false;
			this.isShowingIntermediate = false;
		}
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof ResourceBar) {
			return this.owner.equals(((ResourceBar) object).owner);
		}
		return super.equals(object);
	}
	
	
}
