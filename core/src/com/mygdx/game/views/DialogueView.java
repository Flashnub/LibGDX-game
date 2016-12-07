package com.mygdx.game.views;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.assets.HUDUtils;
import com.mygdx.game.model.actions.nonhostile.DialogueSettings;
import com.mygdx.game.model.events.DialogueListener;

public class DialogueView extends Actor implements DialogueListener{
	
	Label dialogueLabel;
	LabelStyle dialogueLabelStyle;
	String currentFontString;
	BitmapFont currentFont;
	String currentFontColorString;
	String currentText;
	Color currentFontColor;
	float textSpeed;
	TextureRegion background;
	float xOrigin;
	float yOrigin;
	
	public DialogueView() {
		dialogueLabelStyle = new LabelStyle();
		this.setFontWithString(DialogueSettings.defaultFontName, DialogueSettings.defaultFontColor);
		dialogueLabel = new Label("", dialogueLabelStyle);
		dialogueLabel.setWrap(false);
		dialogueLabel.setAlignment(Align.center);
		this.setVisible(false);
		background = this.getBackgroundSprite();
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		layoutWithViewPort(this.getStage().getViewport(), batch);
	}
	
	public void layoutWithViewPort(Viewport viewPort, Batch batch) {
		if (this.isVisible()) {
			this.setX(0f);
			this.setY(0f);
			this.setWidth(viewPort.getScreenWidth());
			this.setHeight(viewPort.getScreenHeight() * 0.3f);
			batch.draw(background, this.getX(), this.getY(), this.getWidth(), this.getHeight());
			
			currentFont.draw(batch, this.currentText, this.xOrigin, this.yOrigin);
		}
	}
	
	@Override
	public void endDialogue() {
		this.currentText = "";
		fadeOut();
	}

	private TextureRegion getBackgroundSprite() {
		TextureAtlas atlas = HUDUtils.masterAtlas;
		if (atlas != null) {
			return atlas.findRegion("text-background");
		}
		return null;
	}
	
	@Override
	public void updateDialogueText(String dialogueText, String fontName, String fontColor, boolean newDialogue, String fullText) {
		// TODO Auto-generated method stub
		if (!this.isVisible()) {
			fadeInDialogueView();
		}
		this.setFontWithString(fontName,fontColor);
		this.currentText = dialogueText;
		dialogueLabel.setText(fullText);
		if (newDialogue) {
			this.xOrigin = (this.getStage().getViewport().getScreenWidth() - dialogueLabel.getPrefWidth()) / 2;
			this.yOrigin = ((this.getStage().getViewport().getScreenHeight() * 0.3f) - dialogueLabel.getPrefHeight()) / 2;
		}
		this.dialogueLabel.setText(currentText);
	}
	
	private void fadeInDialogueView() {
		this.setVisible(true);
	}
	
	private void fadeOut() {
		this.setVisible(false);
	}
	
	private void setFontWithString(String fontName, String fontColorName) {
		if (this.currentFontString == null || !this.currentFontString.equals(fontName)) {
			this.currentFontString = fontName;
			this.currentFont = HUDUtils.HUDSkin.getFont(fontName);
			this.dialogueLabelStyle.font = this.currentFont;
		}
		if (this.currentFontColorString == null || !this.currentFontColorString.equals(fontColorName)) {
			this.currentFontColorString = fontColorName;
			this.currentFontColor = HUDUtils.HUDSkin.getColor(fontColorName);
			this.dialogueLabelStyle.fontColor = this.currentFontColor;
		}
	}






}
