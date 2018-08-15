package com.mygdx.game.views.management;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.game.model.characters.player.Player;

public class ParentSelectorViewModel {
	private SelectorViewModel contentView;
	private Table headerView;
	private Skin headerSkin;
	
	private Player player;
	
	public ParentSelectorViewModel(float width, float height, Player player) {
		this.player = player;
		
		this.contentView = new ItemSelectorViewModel(width, height, player.getCharacterData().getPlayerProperties().getInventory());
		this.headerSkin = new Skin(Gdx.files.internal("selectorViewHeaderSkin"));
	}
	
	public void create(float width, float height) {
		headerView = new Table(headerSkin);

		contentView.create(width, height);
	}
	
}
