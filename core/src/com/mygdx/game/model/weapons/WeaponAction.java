package com.mygdx.game.model.weapons;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.actions.ActionSegmentKey;
import com.mygdx.game.model.actions.ActionSequence;

public class WeaponAction implements Serializable{
	private ActionSequence sequence;

	
	public WeaponAction () {
		
	}
	
	@Override
	public void write(Json json) {
		json.writeValue("sequence", sequence);
		json.writeValue("inputs", inputs);
		json.writeValue("isAerial", isAerial);
		json.writeValue("chainableAttackKeys", chainableAttackKeys);

	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		json.readValue("sequence", ActionSequence.class, jsonData);
		json.readValue("inputs", Array.class, jsonData);
		json.readValue("isAerial", Boolean.class, jsonData);
		json.readValue("chainableAttackKeys", Array.class, jsonData);
	}
	
	public boolean canChainWithAction(WeaponAction action) {
		return action.isAerial == this.isAerial && this.chainableAttackKeys != null && this.chainableAttackKeys.contains(action.sequence.getActionKey(), true);
	}

	public ActionSequence getSequenceKey() {
		return sequence;
	}

	public Array<Integer> getInputs() {
		return inputs;
	}

	public boolean isAerial() {
		return isAerial;
	}

}
