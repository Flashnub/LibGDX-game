package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.worldObjects.Item;

public class ItemEffectSettings extends EntityEffectSettings {
	
	Item item;
	
	@Override
	public void write(Json json) {
		super.write(json);
		json.writeValue("item", item);
	}
	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		this.setType(ItemEffect.type);
		this.item = json.readValue("item", Item.class, jsonData);
	}
	
	@Override
	public ItemEffectSettings deepCopy() {
		ItemEffectSettings copy = new ItemEffectSettings();
		this.setBaseFieldsForSettings(copy);
		copy.item = this.item;
		return copy;
	}

}
