package com.mygdx.game.model.hitSpark;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.model.characters.EntityUIDataType;
import com.mygdx.game.model.characters.EntityUIModel;

public class HitSpark {
	Rectangle imageBounds;
	EntityUIModel uiModel;
	float currentTime;
	HitSparkData data;
	boolean isFinished;
	HitSparkListener listener;

	public HitSpark (HitSparkData data, float x, float y, HitSparkListener listener) {
		this.data = data;
		currentTime = 0f;
		isFinished = false;
		this.listener = listener;
		this.imageBounds = new Rectangle();
		this.uiModel = new EntityUIModel(data.getType(), EntityUIDataType.HITSPARK);
		this.uiModel.setCurrentFrame(this, currentTime);
		this.imageBounds.x = x;
		this.imageBounds.y = y - (this.uiModel.getCurrentFrame().getRegionHeight() / 2);
	}
	
	
	public void update(float delta) {
		this.isFinished = this.uiModel.setCurrentFrame(this, delta);
		currentTime += delta;
		if (this.isFinished){
			listener.deleteHitSpark(this);
		}

//		if (this.uiModel.getCurrentFrame() != null) {
//			this.imageBounds.width = this.uiModel.getCurrentFrame().getRegionWidth();
//			this.imageBounds.height = this.uiModel.getCurrentFrame().getRegionHeight();
//		}
	}

	public String getSize() {
		return data.getSize();
	}
	
	public Rectangle getImageHitBox() {
		return imageBounds;
	}

	public EntityUIModel getUiModel() {
		return uiModel;
	}
	
	public String getType() {
		return data.getType();
	}
}
