package com.mygdx.game.views;

import com.badlogic.gdx.graphics.Color;
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
					return atlas.findRegion("Health_bar_Full");
				}
				return null;
			}
			
			@Override
			public TextureRegion getIntermediateForegroundSprite() {
				TextureAtlas atlas = HUDUtils.masterAtlas;
				if (atlas != null) {
					return atlas.findRegion("Health_bar_Hurt");
				}
				return null;
			}
			
			@Override
			public TextureRegion getBackgroundSprite() {
				TextureAtlas atlas = HUDUtils.masterAtlas;
				if (atlas != null) {
					return atlas.findRegion("Health_bar_Empty");
				}
				return null;
			}
			
			@Override 
			public Color getTintColor(float ratio) {
				return ratio > 0.6f ? Color.WHITE : new Color(1, Math.max((1 - ratio), 0.1f), Math.max((1 - ratio), 0.1f), 1);
			}
			
			@Override
			public Vector2 originCoefficients() {
				return new Vector2(0.1f, 0.8f);
			}			
			
			public Float fixedHeight() {
				return 30f;
			}
			
//			public Float proportionalWidth() {
//				return 0.25f;
//			}
			
			public Float fixedWidth() {
				return 483f;
			}
			
		}, PlayerTension {
			@Override
			public TextureRegion getForegroundSprite() {
				TextureAtlas atlas = HUDUtils.masterAtlas;
				if (atlas != null) {
					return atlas.findRegion("Meter_bar_Full");
				}
				return null;
			}
			
			@Override
			public TextureRegion getIntermediateForegroundSprite() {
				TextureAtlas atlas = HUDUtils.masterAtlas;
				if (atlas != null) {
					return atlas.findRegion("Meter_bar_Empty");
				}
				return null;
			}
			
			@Override
			public TextureRegion getBackgroundSprite() {
				TextureAtlas atlas = HUDUtils.masterAtlas;
				if (atlas != null) {
					return atlas.findRegion("Meter_bar_Empty");
				}
				return null;
			}
						
			@Override 
			public Color getTintColor(float ratio) {
				return new Color(1, Math.max((1 - ratio), 0.1f), Math.max((1 - ratio), 0.1f), 1);
			}
			
			@Override
			public Vector2 originCoefficients() {
				return new Vector2(0.1f, 0.8f);
			}
			
			public Float fixedHeight() {
				return 20f;
			}
			
			public Float proportionalWidth() {
				return 0.3f;
			}
			
			public Vector2 originOffset() {
				return new Vector2(0, -35f);
			}
			
		}, EnemyHealth {
			@Override
			public TextureRegion getForegroundSprite() {
				TextureAtlas atlas = HUDUtils.masterAtlas;
				if (atlas != null) {
					return atlas.findRegion("Enemy_health_bar");
				}
				return null;
			}
			
			@Override
			public TextureRegion getIntermediateForegroundSprite() {
				TextureAtlas atlas = HUDUtils.masterAtlas;
				if (atlas != null) {
					return atlas.findRegion("Enemy_health_bar_hurt");
				}
				return null;
			}
			
			@Override
			public TextureRegion getBackgroundSprite() {
				TextureAtlas atlas = HUDUtils.masterAtlas;
				if (atlas != null) {
					return atlas.findRegion("Enemy_health_bar_background");
				}
				return null;
			}
			
			@Override 
			public Color getTintColor(float ratio) {
				return ratio > 0.6f ? Color.WHITE : new Color(1, Math.max((1 - ratio), 0.1f), Math.max((1 - ratio), 0.1f), 1);
			}
			
			public Float fixedHeight() {
				return 5f;
			}
			
			public Float fixedWidth() {
				return 30f;
			}
			
			public boolean shouldUseHelper() {
				return true;
			}

		};
		
		public boolean shouldUseHelper() {
			return false;
		}
		
		public String getAffiliation() {
			return this.toString();
		}
		
		public TextureRegion getForegroundSprite() {
			TextureAtlas atlas = HUDUtils.masterAtlas;
			if (atlas != null) {
				return atlas.findRegion("Health_bar_Full");
			}
			return null;
		}
		
		public TextureRegion getIntermediateForegroundSprite() {
			TextureAtlas atlas = HUDUtils.masterAtlas;
			if (atlas != null) {
				return atlas.findRegion("Health_bar_Hurt");
			}
			return null;
		}
		
		public TextureRegion getBackgroundSprite() {
			TextureAtlas atlas = HUDUtils.masterAtlas;
			if (atlas != null) {
				return atlas.findRegion("Health_bar_Empty");
			}
			return null;
		}
		
		public Color getTintColor(float ratio) {
			return Color.WHITE;
		}
		
		public Vector2 originCoefficients() {
			return new Vector2();
		}
		
		public Vector2 originOffset() {
			return new Vector2();
		}
		
		public Float fixedHeight() {
			return 0f;
		}
		
		public Float fixedWidth() {
			return 0f;
		}
		
		public Float proportionalWidth() {
			return 0.0f;
		}
		
		public Float proportionalHeight() {
			return 0.0f;
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
	

	public ResourceBar(ResourceBarType type, Character character, CoordinatesHelper helper) {
		this.owner = character;
		this.type = type;
		this.foreground = type.getForegroundSprite();
		this.intermediateForeground = type.getIntermediateForegroundSprite();
		this.background = type.getBackgroundSprite();
		this.expectedRatio = this.getRatio();
		this.differenceInRatio = 0f;
		this.helper = helper;
	}
	
	public void layoutWithViewPort(Viewport viewPort, Batch batch) {
		if (this.type.shouldUseHelper()) {
			Vector2 screenOffset = helper.getScreenCoordinatesForCharacter(owner);
			this.setX(screenOffset.x);
			this.setY(screenOffset.y);
		}
		else 
		{
			this.setX(viewPort.getScreenWidth() * type.originCoefficients().x + type.originOffset().x);
			this.setY(viewPort.getScreenHeight() * type.originCoefficients().y + type.originOffset().y);
		}

		
		//Width.
		if (type.fixedWidth() != null && type.proportionalWidth() != null) {
			this.setWidth(viewPort.getScreenWidth() * type.proportionalWidth() + type.fixedWidth());
		}
		else if (type.fixedWidth() != null){
			this.setWidth(type.fixedWidth());
		}
		else {
			this.setWidth(viewPort.getScreenWidth() * type.proportionalWidth());
		}
		
		//Height.
		if (type.fixedHeight() != null && type.proportionalHeight() != null) {
			this.setHeight(viewPort.getScreenHeight() * type.proportionalHeight() + type.fixedHeight());
		}
		else if (type.fixedHeight() != null) {
			this.setHeight(type.fixedHeight());
		}
		else {
			this.setHeight(viewPort.getScreenHeight() * type.proportionalHeight() + type.fixedHeight());
		}
		
//		if (type.getAffiliation().equals(ResourceBarType.PlayerHealth.getAffiliation())) {
//
//		}
//		else if (type.getAffiliation().equals(ResourceBarType.PlayerTension.getAffiliation())) {
//			this.setX(viewPort.getScreenWidth() * type.originCoefficients().x);
//			this.setY(viewPort.getScreenHeight() * type.originCoefficients().y - 35);
//			this.setWidth(viewPort.getScreenWidth() * 0.25f);
//			this.setHeight(20);
//		}
//		else if (type.getAffiliation().equals(ResourceBarType.EnemyHealth.getAffiliation())) {
//			Vector2 enemyCoordinates = this.helper.getScreenCoordinatesForCharacter(owner);
//			this.setX(enemyCoordinates.x);
//			this.setY(enemyCoordinates.y);
//			this.setWidth(ResourceBar.enemyHealthBarWidth);
//			this.setHeight(5);
//		}

		float intermediateRatio = 0f;
		float solidRatio = 0f;
		if (differenceInRatio < 0) {
			intermediateRatio = (this.expectedRatio);
			solidRatio = (this.expectedRatio + this.differenceInRatio);
		}
		else {
			intermediateRatio = (this.expectedRatio + this.differenceInRatio);
			solidRatio = (this.expectedRatio);
		}
		batch.draw(background, this.getX(), this.getY(), this.getWidth(), this.getHeight());
		if (this.isShowingIntermediate) {
			batch.draw(intermediateForeground, this.getX(), this.getY(), this.getWidth() * intermediateRatio, this.getHeight());
		}
		batch.setColor(type.getTintColor(this.getRatio()));
		batch.draw(foreground, this.getX(), this.getY(), this.getWidth() * solidRatio, this.getHeight());
		batch.setColor(Color.WHITE);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		layoutWithViewPort(this.getStage().getViewport(), batch);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		if (this.getRatio() != expectedRatio) {
			this.differenceInRatio += expectedRatio - this.getRatio();
			this.expectedRatio = this.getRatio();
			this.animationTime = 0f;
			this.isAnimating = false;
			this.isShowingIntermediate = true;
		}
		else if (this.differenceInRatio != 0f) {
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
	
	private float getRatio() {
		if (this.type.equals(ResourceBarType.PlayerTension)) {
			return this.owner.getCharacterData().getTensionRatio();
		}
		else {
			return this.owner.getCharacterData().getHealthRatio();
		}
	}
	
}
