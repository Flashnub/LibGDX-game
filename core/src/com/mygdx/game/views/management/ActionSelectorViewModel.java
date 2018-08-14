package com.mygdx.game.views.management;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.actions.ActionSequence;

/*
 * Table of moves, each cell can be changed to a different move. Will map
 * These moves to different inputs.
 */
public class ActionSelectorViewModel extends SelectorViewModel {
	
	private Array<ActionSequence> actions;
	
	public ActionSelectorViewModel(float width, float height, Array <ActionSequence> actions) {
		super(width, height);
		
		this.actions = actions;
	}
	
    @Override
    protected void create(float width, float height) {
    	super.create(width, height);
    }

	@Override
	public Table createContentTable(float width, float height) {
		
		contentSkin = new Skin(Gdx.files.internal("ActionSelectorViewModel"));

		Table content = new Table(contentSkin);

		return content;
	}
	
}
