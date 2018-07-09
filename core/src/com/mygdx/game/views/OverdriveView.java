package com.mygdx.game.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.model.characters.player.Player;

public class OverdriveView extends Actor {
	
	TextureRegion emptyOverdriveView;
	TextureRegion actualOverdriveView;
	Player owner;
	
	final float delayToAnimate = 1.5f;
	final float timeToAnimate = 3f;
	float animationTime = 0f;
	float expectedRatio;
//	float differenceInRatio;
	boolean isAnimating = false;
	boolean isShowingIntermediate = false;
	
	public OverdriveView() {

	}
	
	public void layoutWithViewPort(Viewport viewPort, Batch batch) {

	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		layoutWithViewPort(this.getStage().getViewport(), batch);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
	}
	
	
}
