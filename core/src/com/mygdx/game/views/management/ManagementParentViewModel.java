
package com.mygdx.game.views.management;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.mygdx.game.model.characters.player.Player;

public class ManagementParentViewModel {
	private SelectorViewModel contentView;
	private Table headerView;
	private Skin headerSkin;
	private ArrayList <SelectorViewModel> selectorViewModels;

	
	private Player player;
	
	public ManagementParentViewModel(float width, float height, Player player) {
		this.player = player;
		this.create(width, height);
	
	}
	
	public void create(float width, float height) {
		this.contentView = new ItemSelectorViewModel(width, height, player.getCharacterData().getPlayerProperties().getInventory());
		this.headerSkin = new Skin(Gdx.files.internal("selectorViewHeaderSkin"));

		headerView = new Table(headerSkin);

		contentView.create(width, height);
	}
	
}
