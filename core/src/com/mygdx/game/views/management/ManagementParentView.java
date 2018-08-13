package com.mygdx.game.views.management;

import java.util.ArrayList;

import com.badlogic.gdx.scenes.scene2d.Actor;
/**
 * Everything involving the Player Management. will fit in here.
 * @author Xx420TryhardxX
 *
 */
public class ManagementParentView extends Actor implements ManagementSelectorViewListener{
    private SelectorViewModel currentSelectorViewModel;
    private PlayerPoseView playerPoseView;

    private ArrayList <SelectorViewModel> selectorViewModels;
}
