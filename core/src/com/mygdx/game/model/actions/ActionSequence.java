package com.mygdx.game.model.actions;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.projectiles.ProjectileSettings;

public class ActionSequence implements Serializable {
	
	enum ActionStrategy {
		RangedOffensive, RangedHeal, MeleeOffensive
	}
	
	enum ActionType {
		Attack, Projectile, Ability
	}
	
	private ArrayList <ActionSegmentKey> actionKeys;
	private ArrayDeque <ActionSegment> actions; //Find a way to make characters use these.
	private ArrayList<ActionSegment> currentActions;
	private ActionStrategy strategy;
	private String windUpState;
	private String actingState;
	private String cooldownState;
	private CharacterModel source;
	private CharacterModel target;
	private ActionListener actionListener;
	private boolean isFinished;
	private boolean isQueuePopulated;
	
	public ActionSequence() {

	}

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("actionKeys", actionKeys);
		json.writeValue("strategy", strategy);
		json.writeValue("windUpState", windUpState);
		json.writeValue("actingState", windUpState);
		json.writeValue("cooldownState", windUpState);

	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		actionKeys = json.readValue("actionKeys", ArrayList.class, jsonData);
		strategy = json.readValue("strategy", ActionStrategy.class, jsonData);
//		nextActionKey = json.readValue("nextActionKey", String.class, jsonData);
		windUpState = json.readValue("windUpState", String.class, jsonData);
		actingState = json.readValue("actingState", String.class, jsonData);
		cooldownState = json.readValue("cooldownState", String.class, jsonData);
//		actionKey = json.readValue("actionKey", String.class, jsonData);
		
		actions = new ArrayDeque<ActionSegment>();
		currentActions = new ArrayList <ActionSegment>();
		this.isFinished = false;
		this.isQueuePopulated = false;
	}
	
	public ActionSequence cloneSequence() {
		ActionSequence sequence = new ActionSequence();
		sequence.actionKeys = this.actionKeys;
		sequence.strategy = this.strategy;
		sequence.windUpState = this.windUpState;
		sequence.actingState = this.actingState;
		sequence.cooldownState = this.cooldownState;
		sequence.actions = new ArrayDeque<ActionSegment>();
		sequence.currentActions = new ArrayList<ActionSegment>();
		sequence.isQueuePopulated = false;
		sequence.isFinished = false;
		return sequence;
	}
	
	public boolean isFinished() {
		return this.isFinished;
	}
	
	public void process(float delta, ActionListener actionListener) {
		if (!this.isQueuePopulated) {
			this.populateActionQueue();
		}
		if (currentActions.size() == 0) {
			ActionSegment action = actions.peek();
			System.out.println("Actions are empty.");
			if (action != null) {
				currentActions.add(action);
				actions.remove(action);
			}
			else {
				this.isFinished = true;
			}
		}
		else if (currentActions.size() > 0) {
			ActionSegment action = actions.peek();
			ActionSegment currentAction = this.currentActions.get(0);
			if (currentAction != null && action != null && currentAction.isConcurrent && action.isConcurrent) {
				currentActions.add(action);
				actions.remove(action);
			}
		}
		for (Iterator<ActionSegment> iterator = this.currentActions.iterator(); iterator.hasNext();) {
			ActionSegment action = iterator.next();
			action.update(delta, actionListener);
			if (action.isFinished()) {
				iterator.remove();
			}
		}
	}
	
	private void populateActionQueue() {
		this.isFinished = false;
		this.isQueuePopulated = true;
		for (ActionSegmentKey sequenceKey : actionKeys) {
			ActionSegment action = null;
			switch (sequenceKey.getTypeOfAction()) {
				case Attack:
					action = new Attack(source, JSONController.attacks.get(sequenceKey.getKeys().get(0).value));
					break;
				case Projectile:
					ArrayList<ProjectileSettings> settings = new ArrayList<ProjectileSettings>();
					AbilitySettings projAbilitySettings = JSONController.abilities.get(sequenceKey.getKeys().get(0).value);
					for (int i = 1; i < sequenceKey.getKeys().size(); i++) {
						settings.add(JSONController.projectiles.get(sequenceKey.getKeys().get(i).value));
					}
					action = new ProjectileAttack(source, target, actionListener, settings, projAbilitySettings);
				case Ability:
					action = new Ability(source, JSONController.abilities.get(sequenceKey.getKeys().get(0).value));
			}
			if (action != null) {
				actions.add(action);
			}
		}
	}

	public CharacterModel getSource() {
		return source;
	}

	public void setSource(CharacterModel source) {
		this.source = source;
	}

	public String getWindUpState() {
		return windUpState;
	}

	public String getActingState() {
		return actingState;
	}

	public String getCooldownState() {
		return cooldownState;
	}

}
