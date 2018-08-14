package com.mygdx.game.views.management;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class MiscSelectorViewModel extends SelectorViewModel{

	public MiscSelectorViewModel(float width, float height) {
		super(width, height);

	}

    @Override
    protected void create(float width, float height) {
    	super.create(width, height);
    }
    
	@Override
	public Table createContentTable(float width, float height) {
		
		contentSkin = new Skin(Gdx.files.internal("MiscSelectorViewTableSkin"));

		Table content = new Table(contentSkin);

		return content;
	}
}
