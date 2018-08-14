package com.mygdx.game.views.management;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.game.model.worldObjects.Item;

/**
 * Used to look at and use items or assign them to hotkeys. Will have item object + icon.
 * @author Xx420TryhardxX
 *
 */
public class ItemSelectorViewModel extends SelectorViewModel{

	private ArrayList<Item> inventory;
    private ItemSelectorViewModelCell selectedItemCell;
    
    public ItemSelectorViewModel(float width, float height) {
		super(width, height);
	}
    
    @Override
    protected void create(float width, float height) {
    	super.create(width, height);
    }
    
	@Override
	public Table createContentTable(float width, float height) {		
		contentSkin = new Skin(Gdx.files.internal("ItemSelectorViewTableSkin"));
		
		Table content = new Table(contentSkin);

		return content;
	}
}
