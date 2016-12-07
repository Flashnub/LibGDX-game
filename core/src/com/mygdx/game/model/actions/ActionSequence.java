package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.actions.nonhostile.DialogueAction;
import com.mygdx.game.model.actions.nonhostile.DialogueSettings;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.CollisionChecker;
import com.mygdx.game.model.events.DialogueListener;
import com.mygdx.game.model.world.DialogueController;
import com.mygdx.game.wrappers.StringWrapper;

public class ActionSequence implements Serializable {
	
	enum ActionStrategy {
		RangedOffensive, RangedHeal, MeleeOffensive, Story
	}
	
	enum ActionType {
		Attack, ProjectileAttack, Ability, Dialogue, Gesture, ItemGift
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
	private String leftWindupState;
	private String leftActingState;
	private String leftCooldownState;
	private boolean useLeft;
	private boolean isActive;
	private CharacterModel source;
	private CharacterModel target;
	private ActionListener actionListener;
	private CollisionChecker collisionChecker;
	private DialogueController dialogueController;
	
	
	public ActionSequence() {

	}
	
//	public ActionSequence(ActionType type) {
//		actionKey = new ActionSegmentKey(new StringWrapper(type.toString()), type);
//		if (type.equals(ActionType.Dialogue) || type.equals(ActionType.StatelessDialogue)) {
//			//Create default dialogue action and store it in char properties?
//			//Or make static void to create dialogue Action with DialogueSettings to create them on demand?
//		}
//	}
//	
	public static ActionSequence createSequenceWithDialog(DialogueSettings settings, CharacterModel source, CharacterModel target, DialogueController dialogueController) {
		ActionSequence sequence = new ActionSequence();
		boolean hasState = settings.getActingState().equals(DialogueSettings.defaultState);
		sequence.actionKey = new ActionSegmentKey(
				new StringWrapper(hasState ? DialogueSettings.defaultState : DialogueSettings.stateless),
				ActionType.Dialogue);
		sequence.isActive = hasState;
		sequence.strategy = ActionStrategy.Story;

		sequence.windupState = settings.getWindupState();
		sequence.actingState = settings.getActingState();
		sequence.cooldownState = settings.getCooldownState();
		sequence.leftWindupState = sequence.windupState;
		sequence.leftActingState = sequence.actingState;
		sequence.leftCooldownState = sequence.cooldownState;
		sequence.useLeft = source.isFacingLeft();
		
		sequence.source = source;
		sequence.target = target;
		sequence.dialogueController = dialogueController;
		
		sequence.createActionFromSettings(settings);
		
		return sequence;
	}

	@Override
	public void write(Json json) {
		json.writeValue("actionKey", actionKey);
		json.writeValue("strategy", strategy);
		json.writeValue("windupState", windupState);
		json.writeValue("actingState", windupState);
		json.writeValue("cooldownState", windupState);

	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		actionKey = json.readValue("actionKey", ActionSegmentKey.class, jsonData);
		strategy = json.readValue("strategy", ActionStrategy.class, jsonData);
//		nextActionKey = json.readValue("nextActionKey", String.class, jsonData);
		String windupState = json.readValue("windupState", String.class, jsonData);
		String cooldownState = json.readValue("cooldownState", String.class, jsonData);
		String leftWindupState = json.readValue("leftWindupState", String.class, jsonData);
		String leftActingState = json.readValue("leftActingState", String.class, jsonData);
		String leftCooldownState = json.readValue("leftCooldownState", String.class, jsonData);
		
		actingState = json.readValue("actingState", String.class, jsonData);
		
		if (windupState != null) {
			this.windupState = windupState;
		}
		else {
			this.windupState = actingState;
		}
		
		if (leftWindupState != null) {
			this.leftWindupState = leftWindupState;
		}
		else {
			this.leftWindupState = this.windupState;
		}
		
		if (cooldownState != null) {
			this.cooldownState = cooldownState;
		}
		else {
			this.cooldownState = actingState;
		}
		
		if (leftActingState != null) {
			this.leftActingState = leftActingState;
		}
		else {
			this.leftActingState = this.actingState;
		}
		
		if (leftCooldownState != null) {
			this.leftCooldownState = leftCooldownState;
		}
		else {
			this.leftCooldownState = this.cooldownState;
		}
		
//		actionKey = json.readValue("actionKey", String.class, jsonData);
		Boolean isActive = json.readValue("isActive", Boolean.class, jsonData);
		if (isActive != null) {
			this.isActive = isActive;
		}
		else {
			this.isActive = true;
		}
	}
	
	public ActionSequence cloneSequenceWithSourceAndTarget(CharacterModel source, CharacterModel target, ActionListener actionListener, CollisionChecker collisionChecker) {
		ActionSequence sequence = cloneBareSequence();
		sequence.source = source;
		sequence.target = target;
		sequence.actionListener = actionListener;
		sequence.collisionChecker = collisionChecker;
		sequence.useLeft = source.isFacingLeft();
		sequence.createActionFromSettings(null);
		return sequence;
	}
	
	public ActionSequence cloneBareSequence() {
		ActionSequence sequence = new ActionSequence();
		sequence.actionKey = this.actionKey;
		sequence.strategy = this.strategy;
		sequence.windupState = this.windupState;
		sequence.actingState = this.actingState;
		sequence.cooldownState = this.cooldownState;
		sequence.leftWindupState = this.leftWindupState;
		sequence.leftActingState = this.leftActingState;
		sequence.leftCooldownState = this.leftCooldownState;
		sequence.isActive = this.isActive;
		return sequence;
	}
	
	public void forceFinish() {
		this.action.forceRemove = true;
	}
	
	public void forceInterrupt() {
		this.action.forceInterrupt = true;
	}
	
	public boolean isFinished() {
		return this.action.isFinished();
	}
	
	public boolean canUseTarget() {
		switch(actionKey.typeOfAction) {
			case Ability:
			case Dialogue:
			case Gesture:
			case ItemGift:
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
				return this.useLeft ? this.leftWindupState : this.windupState;
			case ACTION:
				return this.useLeft ? this.leftActingState : this.actingState;
			case COOLDOWN:
				return this.useLeft ? this.leftCooldownState : this.cooldownState;
			default:
				return this.useLeft ? this.leftActingState : this.actingState;
		}
	}
	
	public void process(float delta, ActionListener actionListener) {
		this.action.update(delta, actionListener);
		if (action.didChangeState && this.isActive) {
			source.setState(this.getCurrentActionState());
		}
	}
	
	private void createActionFromSettings(DialogueSettings potentialDialogue) {
		ActionSegment action = 	this.getActionSegmentForKey(this.actionKey, potentialDialogue);
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
	
	private ActionSegment getActionSegmentForKey(ActionSegmentKey segmentKey, DialogueSettings potentialDialogue) {
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
			case Dialogue:
				action = new DialogueAction(potentialDialogue, this.dialogueController, source, target);
				break;
			case Gesture:
				break;
			case ItemGift:
				break;
		}
		return action;
	}
	
	public ActionSegmentKey getActionKey() {
		return actionKey;
	}

	public ActionSegment getAction() {
		return this.action;
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

	public boolean isActive() {
		return isActive;
	}

}
