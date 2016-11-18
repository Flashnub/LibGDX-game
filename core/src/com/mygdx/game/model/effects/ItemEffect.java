package com.mygdx.game.model.effects;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.player.Player.PlayerModel;

public class ItemEffect extends Effect {
	
	ItemEffectSettings itemEffectSettings;
	boolean hasAlreadyAdded;

	public ItemEffect(EffectSettings settings) {
		super(settings);
		if (settings instanceof ItemEffectSettings) {
			this.itemEffectSettings = (ItemEffectSettings) settings;
		}
		hasAlreadyAdded = false;
	}
	
	@Override
	public boolean process(CharacterModel target, float delta) {
		boolean isFinished = super.process(target, delta);
		if (target instanceof PlayerModel) {
			PlayerModel pTarget = (PlayerModel) target;
			if (!hasAlreadyAdded && (itemEffectSettings.isInstantaneous || (isActive && !isFinished))) {
				pTarget.addItemToInventory(itemEffectSettings.item);
				hasAlreadyAdded = true;
			}
		}
		return isFinished;
	}

}
