package com.mygdx.game.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.model.worldObjects.Item;
import com.mygdx.game.views.ResourceBar.ResourceBarType;

public class AdditionalInfoView extends Actor {
	ItemView itemView;
	
	public AdditionalInfoView() {
		itemView = new ItemView();
	}
	
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		layoutWithViewPort(this.getStage().getViewport(), batch);
	}
	
	public void layoutWithViewPort(Viewport viewPort, Batch batch) {
		this.setX(viewPort.getScreenWidth() * ResourceBarType.PlayerTension.originCoefficients().x + ResourceBarType.PlayerTension.originOffset().x);
		this.setY(viewPort.getScreenHeight() * ResourceBarType.PlayerTension.originCoefficients().y + ResourceBarType.PlayerTension.originOffset().y - 35);
		this.setWidth(itemView.getBackground().getRegionWidth());
		this.setHeight(itemView.getBackground().getRegionHeight());
		
		//layout each mini item view.
		itemView.draw(batch, this.getX(), this.getY());
		
//		batch.draw(background, this.getX(), this.getY(), this.getWidth(), this.getHeight());
	}

	public void onSwitchItem(Item item, int numberOfItems) {
		itemView.setItemInfo(item, numberOfItems);
	}
}
