package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.player.Player.PlayerModel;

public class ItemEffect extends EntityEffect {
	
	ItemEffectSettings itemEffectSettings;
	boolean hasAlreadyAdded;
	public static final String type = "Item";

	public ItemEffect(EffectSettings settings, EffectController retriever) {
		super(settings, retriever);
		if (settings instanceof ItemEffectSettings) {
			this.itemEffectSettings = (ItemEffectSettings) settings;
		}
		hasAlreadyAdded = false;
	}
	
	@Override
	protected void processDuringActive(CharacterModel target, float delta) {
		super.processDuringActive(target, delta);
		if (target instanceof PlayerModel) {
			PlayerModel pTarget = (PlayerModel) target;
			if (!hasAlreadyAdded && (itemEffectSettings.isInstantaneous() || (isActive))) {
				pTarget.addItemToInventory(itemEffectSettings.item);
				hasAlreadyAdded = true;
			}
		}
	}

	@Override
	public String getType() {
		return ItemEffect.type;
	}
	

}
