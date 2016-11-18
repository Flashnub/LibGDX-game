package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.CollisionChecker;

public class ActionSequence implements Serializable {
	
	enum ActionStrategy {
		RangedOffensive, RangedHeal, MeleeOffensive
	}
	
	enum ActionType {
		Attack, ProjectileAttack, Ability
	}
	
//	private ArrayList <ActionSegmentKey> actionKeys;
//	private ArrayDeque <ActionSegment> actions; //Find a way to make characters use these.
//	private ArrayList<ActionSegment> currentActions;
//	private ArrayList <ActionSegment> allActionSegments; //Used for AI calculations
//	private ArrayList <Integer> indicesToRemove;
	private ActionSegmentKey actionKey;
	private ActionSegment action;
//	private ActionSegment sampleAction;
	private ActionStrategy strategy;
	private String windupState;
	private String actingState;
	private String cooldownState;
	private CharacterModel source;
	private CharacterModel target;
	private ActionListener actionListener;
	private CollisionChecker collisionChecker;
	
	public ActionSequence() {

	}

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("actionKey", actionKey);
		json.writeValue("strategy", strategy);
		json.writeValue("windupState", windupState);
		json.writeValue("actingState", windupState);
		json.writeValue("cooldownState", windupState);

	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		actionKey = json.readValue("actionKey", ActionSegmentKey.class, jsonData);
		strategy = json.readValue("strategy", ActionStrategy.class, jsonData);
//		nextActionKey = json.readValue("nextActionKey", String.class, jsonData);
		String windupState = json.readValue("windupState", String.class, jsonData);
		String cooldownState = json.readValue("cooldownState", String.class, jsonData);
		actingState = json.readValue("actingState", String.class, jsonData);
		
		if (windupState != null) {
			this.windupState = windupState;
		}
		else {
			this.windupState = actingState;
		}
		
		if (cooldownState != null) {
			this.cooldownState = cooldownState;
		}
		else {
			this.cooldownState = actingState;
		}
//		actionKey = json.readValue("actionKey", String.class, jsonData);
	}
	
	public ActionSequence cloneSequenceWithSourceAndTarget(CharacterModel source, CharacterModel target, ActionListener actionListener, CollisionChecker collisionChecker) {
		ActionSequence sequence = cloneBareSequence();
		sequence.source = source;
		sequence.target = target;
		sequence.actionListener = actionListener;
		sequence.collisionChecker = collisionChecker;
		sequence.createActionFromSettings();
		return sequence;
	}
	
	public ActionSequence cloneBareSequence() {
		ActionSequence sequence = new ActionSequence();
		sequence.actionKey = this.actionKey;
		sequence.strategy = this.strategy;
		sequence.windupState = this.windupState;
		sequence.actingState = this.actingState;
		sequence.cooldownState = this.cooldownState;
		return sequence;
	}
	
	public boolean isFinished() {
		return this.action.isFinished();
	}
	
	public boolean canUseTarget() {
		switch(actionKey.typeOfAction) {
			case Ability:
				return false;
			case Attack:
			case ProjectileAttack:
				return true;
		}
		return false;
	}
	
	public String getCurrentActionState() {
		this.action.didChangeState = false;
		switch (action.getActionState()) {
			case WINDUP:
				return this.windupState;
			case ACTION:
				return this.actingState;
			case COOLDOWN:
				return this.cooldownState;
			default:
				return this.actingState;
		}
	}
	
	public void process(float delta, ActionListener actionListener) {
		this.action.update(delta, actionListener);
		if (action.didChangeState) {
			source.setState(this.getCurrentActionState());
		}
	}
	
	private void createActionFromSettings() {
		ActionSegment action = 	this.getActionSegmentForKey(this.actionKey);
		if (action != null) {
			this.action = action;
		}
	}
	
	public float getEffectiveRange() {
//		for (ActionSegment actionSegment : allActionSegments) {
//			range += actionSegment.getEffectiveRange();
//		}
		return action.getEffectiveRange();
	}
	
	private ActionSegment getActionSegmentForKey(ActionSegmentKey segmentKey) {
		ActionSegment action = null;
		switch (segmentKey.getTypeOfAction()) {
			case Attack:
				action = new Attack(source, target, JSONController.attacks.get(segmentKey.getKey().value));
				break;
			case ProjectileAttack:
				action = new ProjectileAttack(source, target, actionListener, collisionChecker, JSONController.projectileAttacks.get(segmentKey.getKey().value));
				break;
			case Ability:
				action = new Ability(source, JSONController.abilities.get(segmentKey.getKey().value));
				break;
		}
		return action;
	}

	public CharacterModel getSource() {
		return source;
	}
	
	public void setSource(CharacterModel source) {
		this.source = source;
	}

	public String getWindUpState() {
		return windupState;
	}

	public String getActingState() {
		return actingState;
	}

	public String getCooldownState() {
		return cooldownState;
	}

}
