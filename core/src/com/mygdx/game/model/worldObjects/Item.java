package com.mygdx.game.model.worldObjects;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.actions.ActionSegmentListener;
import com.mygdx.game.model.actions.ActionSequence;
import com.mygdx.game.model.characters.Character;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.player.Player.PlayerModel;

public class Item implements Serializable, ActionSegmentListener {
	ActionSequence itemSequence;
	String name;
	String desc;
	ItemUIModel uiModel;
	boolean expiresOnUse;
	
	public Item () {

	}
	
	
	public void use(CharacterModel owner) {
		if (itemSequence != null) {
			ActionSequence itemSeq = itemSequence.cloneSequenceWithSourceAndTarget(owner, null, owner.getActionListener(), owner.getCollisionChecker());
			itemSeq.addSegmentListener(this);
			owner.addActionSequence(itemSeq);
		}
	}


	@Override
	public void write(Json json) {
		json.writeValue("name", name);
		json.writeValue("desc", desc);
		json.writeValue("itemSequence", itemSequence);
		json.writeValue("uiModel", uiModel);
		json.writeValue("expiresOnUse", expiresOnUse);
	}


	@Override
	public void read(Json json, JsonValue jsonData) {
		itemSequence = json.readValue("itemSequence", ActionSequence.class, jsonData);
		name = json.readValue("name", String.class, jsonData);
		desc = json.readValue("desc", String.class, jsonData);
		uiModel = json.readValue("uiModel", ItemUIModel.class, jsonData);
		Boolean expiresOnUse = json.readValue("expiresOnUse", Boolean.class, jsonData);
		if (expiresOnUse != null) {
			this.expiresOnUse = expiresOnUse;
		}
		else {
			this.expiresOnUse = true;
		}
	}


	public ItemUIModel getUiModel() {
		return uiModel;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof Item) {
			Item otherItem = (Item) other;
			return otherItem.name.equals(this.name);
		}
		return false;
	}


	@Override
	public void onWindup(CharacterModel source) {
		
	}

	@Override
	public void onActive(CharacterModel source) {
		if (source instanceof PlayerModel) {
			PlayerModel pSource = (PlayerModel) source;
			pSource.removeFromInventory(this);
		}
	}


	@Override
	public void onCooldown(CharacterModel source) {
		// TODO Auto-generated method stub
		
	}

	public boolean isUseable() {
		return this.itemSequence != null;
	}

	public boolean expiresOnUse() {
		return expiresOnUse;
	}
	
}
