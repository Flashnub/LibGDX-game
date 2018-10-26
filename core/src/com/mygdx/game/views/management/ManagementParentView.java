package com.mygdx.game.views.management;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.mygdx.game.model.characters.player.Player;
/**
 * Everything involving the Player Management. will fit in here.
 * @author Xx420TryhardxX
 *
 */
public class ManagementParentView extends Actor implements ManagementSelectorViewListener{
    // private SelectorViewModel currentSelectorViewModel;
    // private ManagementParentHeaderView headerView;

    // private ArrayList <SelectorViewModel> selectorViewModels;

    private ManagementParentViewModel viewModel; 

    public ManagementParentView(float width, float height, Player player) {
        viewModel = new ManagementParentViewModel(width, height, player);
    }

    private ArrayList <SelectorViewModel> selectorViewModels;
    

}
