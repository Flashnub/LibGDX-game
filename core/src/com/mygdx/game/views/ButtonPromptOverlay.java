package com.mygdx.game.views;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.assets.HUDUtils;
import com.mygdx.game.model.worldObjects.WorldObject;


public class ButtonPromptOverlay extends Actor {
	TextureRegion background;
	TextureRegion buttonImage;
	Array <WorldObject> nearbyObjects;
	TextField text;
	Skin skin;
	
	public ButtonPromptOverlay() {
		nearbyObjects = new Array<WorldObject>();
		skin = HUDUtils.HUDSkin;
		text = new TextField("", skin);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
	}
}
