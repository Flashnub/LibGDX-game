package com.mygdx.game.model.actions.nonhostile;

import com.mygdx.game.model.actions.ActionSegment;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EntityEffect;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.MovementEffectSettings;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.world.DialogueController;

public class DialogueAction extends ActionSegment {
	DialogueSettings dialogue;
	DialogueController dialogueController;
	CharacterModel target;
	
	public DialogueAction(DialogueSettings dialogue, DialogueController dialogueController, ActionListener actionListener, CharacterModel source, CharacterModel target) {
		super(actionListener);
		this.setSource(source);
		this.dialogue = dialogue;
		this.dialogueController = dialogueController;
		this.target = target;
	}
	
	@Override
	public void sendActionToListener(ActionListener actionListener, float delta) {
		// Do nothing.
	}

	@Override
	public void sourceActiveProcessWithoutSuper(CharacterModel source) {
		if (dialogue.targetEffects != null) {
			for (EffectSettings effectSettings : dialogue.targetEffects) {
				EntityEffect effect = EffectInitializer.initializeEntityEffect(effectSettings, this);
				target.addEffect(effect);
			}
		}
		dialogueController.handleDialogue(this.dialogue);
	}

	@Override
	public float getWindUpTime() {
		return dialogue.windupTime;
	}

	@Override
	public float getWindUpPlusActionTime() {
		return dialogue.windupTime + dialogue.getTimeToDisplayDialogue();
	}

	@Override
	public float getTotalTime() {
		return dialogue.windupTime + dialogue.getTimeToDisplayDialogue() + dialogue.cooldownTime;
	}

	@Override
	public ActionSegment cloneActionSegmentWithSourceAndTarget(CharacterModel source, CharacterModel target) {
		return new DialogueAction(this.dialogue, this.dialogueController, this.getActionListener(), source, target);
	}

	@Override
	public float getEffectiveRange() {
		return Float.MAX_VALUE;
	}
	
	@Override 
	public int getPriority() {
		return ActionSegment.StoryPriority;
	}

	@Override
	public void interruptionBlock() {
		//Do nothing.
	}

	@Override
	public MovementEffectSettings getReplacementMovementForStagger() {
		return null;
	}

	@Override
	public void sourceWindupProcessWithoutSuper(CharacterModel source) {
		
	}

	@Override
	public ActionListener getActionListener() {
		return null;
	}

	@Override
	public boolean shouldRespectEntityCollisions() {
		return false;
	}

	@Override
	public void sourceCompletionWithoutSuper(CharacterModel source) {
		
	}

}
