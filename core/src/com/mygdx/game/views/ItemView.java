package com.mygdx.game.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.mygdx.game.assets.HUDUtils;
import com.mygdx.game.model.actions.nonhostile.DialogueSettings;
import com.mygdx.game.model.worldObjects.Item;

public class ItemView {
	TextureRegion background;
	TextureRegion itemIcon;
	Label itemName;
	BitmapFont itemFont;
	final float textSpacing = 3f;
	
	public ItemView() {
		background = HUDUtils.masterAtlas.findRegion("item-background");
		itemFont = HUDUtils.HUDSkin.getFont(DialogueSettings.defaultFontName);
		Color itemFontColor = HUDUtils.HUDSkin.getColor(DialogueSettings.defaultFontColor);
		LabelStyle labelStyle = new LabelStyle();
		labelStyle.font = itemFont;
		labelStyle.fontColor = itemFontColor;
		itemName = new Label("", labelStyle);
	}
	
	public void setItemInfo(Item item) {
		itemIcon = item.getUiModel().getItemIcon();
		itemName.setText(item.getName());
	}
	
	public void draw(Batch batch, float xOrigin, float yOrigin) {
		batch.draw(background, xOrigin, yOrigin, background.getRegionWidth(), background.getRegionHeight());
		if (itemIcon != null) {
			batch.draw(itemIcon, xOrigin, yOrigin, itemIcon.getRegionWidth(), itemIcon.getRegionHeight());
			itemFont.draw(batch, this.itemName.getText(), xOrigin + itemIcon.getRegionWidth() + textSpacing, yOrigin);
		}
		
	}
	
	public TextureRegion getBackground() {
		return background;
	}
}
