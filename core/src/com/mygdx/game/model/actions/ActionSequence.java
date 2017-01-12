package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Queue;
import com.mygdx.game.constants.InputType;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.actions.nonhostile.DialogueAction;
import com.mygdx.game.model.actions.nonhostile.DialogueSettings;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.effects.MovementEffectSettings;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.CollisionChecker;
import com.mygdx.game.model.world.DialogueController;

public class ActionSequence implements Serializable {
	
	enum ActionStrategy {
		RangedOffensive, RangedHeal, MeleeOffensive, Story
	}
	
	public enum ActionType {
		Attack, WorldAttack, Ability, Dialogue, Gesture, ItemGift, Stagger
	}
	
	public enum UseType {
		Aerial, Ground, Either
	}

	private ActionSegmentKey actionKey;
	private ActionSegment action;
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
	private boolean isStaggered;
	private boolean cannotBeOverriden;
	private float staggerTime;
	private boolean isSuper;
	private Array <Array <String>> leftInputs;
	private Array <Array <String>> rightInputs;
	private UseType useType;
	private Array <ActionSegmentKey> chainableActionKeys;
	private float probabilityToActivate;
	
	public ActionSequence() {
		this.isStaggered = false;
		this.cannotBeOverriden = false;
		this.staggerTime = 0f;
		this.probabilityToActivate = 0f;
		this.isSuper = false;
	}
	
	public static ActionSequence createSequenceWithDialog(DialogueSettings settings, CharacterModel source, CharacterModel target, DialogueController dialogueController, ActionListener actionListener) {
		ActionSequence sequence = new ActionSequence();
		boolean hasState = settings.getActingState().equals(DialogueSettings.defaultState);
		sequence.actionKey = new ActionSegmentKey(
				hasState ? DialogueSettings.defaultState : DialogueSettings.stateless,
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
		sequence.actionListener = actionListener;
		sequence.cannotBeOverriden = true;
		sequence.useType = UseType.Either;
		sequence.leftInputs = new Array <Array <String>>();
		sequence.rightInputs = new Array <Array <String>>();
		
		sequence.createActionFromSettings(settings, null);
		
		return sequence;
	}
	
	static final String staggerKey = "Stagger";
	
	public static ActionSequence createStaggerSequence(CharacterModel source, MovementEffectSettings overridingMovement, ActionListener actionListener) {
		ActionSequence sequence = new ActionSequence();
		sequence.actionKey = new ActionSegmentKey(ActionSequence.staggerKey, ActionType.Stagger);
		sequence.isActive = true;
		sequence.strategy = ActionStrategy.Story;
		
		sequence.windupState = "StaggerWindup";
		sequence.actingState = "Stagger";
		sequence.cooldownState = "StaggerCooldown";
		
		sequence.leftWindupState = sequence.windupState;
		sequence.leftActingState = sequence.actingState;
		sequence.leftCooldownState = sequence.cooldownState;
		sequence.useLeft = source.isFacingLeft();
		
		sequence.source = source;
		sequence.actionListener = actionListener;
		sequence.useType = UseType.Either;
		sequence.leftInputs = new Array <Array <String>>();
		sequence.rightInputs = new Array <Array <String>>();

		
		sequence.createActionFromSettings(null, overridingMovement);
			
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

	@SuppressWarnings("unchecked")
	@Override
	public void read(Json json, JsonValue jsonData) {
		actionKey = json.readValue("actionKey", ActionSegmentKey.class, jsonData);
		strategy = json.readValue("strategy", ActionStrategy.class, jsonData);
		chainableActionKeys = json.readValue("chainableActionKeys", Array.class, jsonData);
		
		Boolean	isSuper = json.readValue("isSuper", Boolean.class, jsonData);
		if (isSuper != null && isSuper.booleanValue()) {
			this.isSuper = isSuper;
		}
		
		UseType useType = json.readValue("useType", UseType.class, jsonData);
		if (useType != null) {
			this.useType = useType;
		}
		else {
			this.useType = UseType.Ground;
		}
		leftInputs = json.readValue("inputs", Array.class, jsonData);
		if (leftInputs == null) {
			this.leftInputs = new Array <Array <String>>();
		}
		this.rightInputs = new Array <Array <String>>();
		for (Array <String> inputCombination : this.leftInputs) {
			Array <String> rightInputCombination = new Array <String>();
			for (String input : inputCombination) {
				String newInput = InputType.flip(input);
				rightInputCombination.add(newInput);
			}
			this.rightInputs.add(rightInputCombination);
		}

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
		
		Boolean isActive = json.readValue("isActive", Boolean.class, jsonData);
		if (isActive != null) {
			this.isActive = isActive;
		}
		else {
			this.isActive = true;
		}
		
		Float probabilityToActivate = json.readValue("probabilityToActivate", Float.class, jsonData);
		if (probabilityToActivate != null) {
			this.probabilityToActivate = probabilityToActivate;
		}
		else {
			this.probabilityToActivate = 0.25f;
		}
	}
	
	public ActionSequence cloneSequenceWithSourceAndTarget(CharacterModel source, CharacterModel target, ActionListener actionListener, CollisionChecker collisionChecker) {
		ActionSequence sequence = cloneBareSequence();
		sequence.source = source;
		sequence.target = target;
		sequence.actionListener = actionListener;
		sequence.collisionChecker = collisionChecker;
		sequence.useLeft = source.isFacingLeft();
		sequence.createActionFromSettings(null, null);
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
		sequence.chainableActionKeys = this.chainableActionKeys;
		sequence.useType = this.useType;
		sequence.leftInputs = this.leftInputs;
		sequence.rightInputs = this.rightInputs;
		sequence.probabilityToActivate = this.probabilityToActivate;
		sequence.isSuper = this.isSuper;
		return sequence;
	}
	
	public void forceEnd() {
		if (!this.cannotBeOverriden)
			this.action.forceEnd = true;
	}
	
	public void forceCooldownState() {
		this.action.forceCooldownState = true;
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
			case Stagger:
				return false;
			case Attack:
			case WorldAttack:
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
		if (this.isStaggered && !this.getAction().forceEnd) {
			this.staggerTime += delta;
			if (this.staggerTime > EntityUIModel.standardStaggerDuration) {
				this.isStaggered = false;
				this.staggerTime = 0f;
			}
		}
		else {
			this.action.update(delta);
			if (this.isActive) {
				source.shouldUnlockControls(this);
				if (action.didChangeState) {
					source.setState(this.getCurrentActionState());

				}
			}
		}

	}
	
	private void createActionFromSettings(DialogueSettings potentialDialogue, MovementEffectSettings replacementMovement) {
		ActionSegment action = 	this.getActionSegmentForKey(this.actionKey, potentialDialogue, replacementMovement);
		if (action != null) {
			this.action = action;
		}
	}

	public float getEffectiveRange() {
		return action.getEffectiveRange();
	}
	
	private ActionSegment getActionSegmentForKey(ActionSegmentKey segmentKey, DialogueSettings potentialDialogue, MovementEffectSettings replacementMovement) {
		ActionSegment action = null;
		switch (segmentKey.getTypeOfAction()) {
			case Attack:
				action = new Attack(source, JSONController.attacks.get(segmentKey.getKey()), this.actionListener, this.collisionChecker);
				break;
			case WorldAttack:
				action = new WorldAttack(source, target, actionListener, collisionChecker, JSONController.projectileAttacks.get(segmentKey.getKey()));
				break;
			case Ability:
				action = new Ability(source, JSONController.abilities.get(segmentKey.getKey()), this.actionListener);
				break;
			case Stagger:
				action = new Ability(source, JSONController.abilities.get(segmentKey.getKey()), this.actionListener, replacementMovement);
				break;
			case Dialogue:
				action = new DialogueAction(potentialDialogue, this.dialogueController, this.actionListener, source, target);
				break;
			case Gesture:
				break;
			case ItemGift:
				break;
		}
		return action;
	}
	
	public boolean isActionChainableWithThis(ActionSequence nextAction) {
		if (this.chainableActionKeys != null) {
			boolean isChainable = false;
			for (ActionSegmentKey key : this.chainableActionKeys) {
				isChainable = isChainable || key.equals(nextAction.actionKey);
			}
			return isChainable && this.useType.equals(nextAction.useType);
		}
		return false;
	}
	
	public void stagger() {
		this.isStaggered = true;
		this.staggerTime = 0f;
	}
	
	public boolean doInputsMatch(Queue <String> inputs, boolean useLeftInputs) {
		boolean isMatching = false;
		Array <Array <String>> properInputsForTesting = useLeftInputs ? this.leftInputs : this.rightInputs;
		for (Array <String> inputCombination : properInputsForTesting) {
			boolean isSequenceMatchingSoFar = true;
			if (inputs.size >= inputCombination.size) {
				for (int i = 0; i < inputCombination.size; i++) {
					isSequenceMatchingSoFar = isSequenceMatchingSoFar && inputs.get(i).equals(inputCombination.get(i));
				}
			}
			if (isSequenceMatchingSoFar) {
				isMatching = true;
				break;
			}
		}


		return isMatching;
	}
	
	// Sort them from most inputs required to least.
	public static void addSequenceToSortedArray (Array <ActionSequence> sequences, ActionSequence sequence) {
		boolean isAdded = false;
		for (int i = 0; i < sequences.size; i++) {
			if (sequence.leftInputs.size > sequences.get(i).leftInputs.size) {
				sequences.insert(i, sequence);
			}
		}
		if (!isAdded) {
			sequences.add(sequence);
		}
	}
	
	public boolean cannotBeOverriden() {
		return cannotBeOverriden;
	}
	
	//For Enemy AIs
	public boolean shouldChain() {
		return this.action.shouldChain();
	}
	
	public void increaseProbability() {
		this.probabilityToActivate *= 2;
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

	public Array<ActionSegmentKey> getChainableActionKeys() {
		return chainableActionKeys;
	}

	public float getProbabilityToActivate() {
		return probabilityToActivate;
	}
}
