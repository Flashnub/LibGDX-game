package com.mygdx.game.model.actions.nonhostile;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

public class InjuryDialogueSettings extends ConditionalDialogueSettings{
	
	double minimumRatio;
	double maximumRatio;
	double actualRatio;

	@Override
	public boolean conditionsMet() {
		boolean conditionsMet = super.conditionsMet();
		if (source != null) {
			return conditionsMet && source.getHealthRatio() < actualRatio;
		}
		return false;
	}

	
	@Override
	public void read(Json json, JsonValue jsonData) {
		super.read(json, jsonData);
		minimumRatio = json.readValue("minimumRatio", Double.class, jsonData);
		maximumRatio = json.readValue("maximumRatio", Double.class, jsonData);
		actualRatio = ThreadLocalRandom.current().nextDouble(minimumRatio, maximumRatio);
	}
	

}
