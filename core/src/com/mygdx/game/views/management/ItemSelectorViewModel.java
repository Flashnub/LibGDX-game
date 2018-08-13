package com.mygdx.game.views.management;

import java.util.ArrayList;

import com.mygdx.game.model.worldObjects.Item;

/**
 * Used to look at and use items or assign them to hotkeys. Will have item object + icon.
 * @author Xx420TryhardxX
 *
 */
public class ItemSelectorViewModel extends SelectorViewModel{
    private ArrayList<Item> inventory;
    private ItemSelectorViewModelCell selectedItemCell;
}
