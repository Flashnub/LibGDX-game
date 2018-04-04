package com.mygdx.game.model.hitSpark;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.model.characters.EntityUIDataType;
import com.mygdx.game.model.characters.EntityUIModel;

public class HitSpark {
	Rectangle imageBounds;
	EntityUIModel uiModel;
	float currentTime;
	HitSparkData data;

	public HitSpark (HitSparkData data, float x, float y) {
		this.data = data;
		this.imageBounds = new Rectangle(x, y, 0, 0);
		this.uiModel = new EntityUIModel(data.getType(), EntityUIDataType.HITSPARK);
		currentTime = 0f;
	}
	
	
	public void update(float delta) {
		this.uiModel.setCurrentFrame(this, delta);
		
//		if (this.uiModel.getCurrentFrame() != null) {
//			this.imageBounds.width = this.uiModel.getCurrentFrame().getRegionWidth();
//			this.imageBounds.height = this.uiModel.getCurrentFrame().getRegionHeight();
//		}
	}

	public String getSize() {
		return data.getSize();
	}
	
	public Rectangle getImageBounds() {
		return imageBounds;
	}
}
