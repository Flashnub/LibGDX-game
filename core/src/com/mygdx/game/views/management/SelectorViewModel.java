package com.mygdx.game.views.management;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.game.model.characters.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public abstract class SelectorViewModel {
	Container<Table> tableContainer;
	Skin contentSkin;
	Skin parentSkin;

	Player player;
	
	public SelectorViewModel(float width, float height) {
		tableContainer = new Container<Table>();
		parentSkin = new Skin(Gdx.files.internal("selectorViewParentSkin"));
		this.create(width, height);
	}
	
	protected void create(float width, float height) {
		tableContainer.setSize(width * 0.2f, height * 0.6f);

		Table parent = new Table(parentSkin);
		
		//generate header for different tabs.
		Table content = this.createContentTable(width, height);
		parent.row();
		parent.add(content).fillX();
		
		tableContainer.setActor(parent);
	}
	
	public abstract Table createContentTable(float width, float height);

	public void prepareRender(float delta) {
		
	}
	
	
}
