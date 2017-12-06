package com.mygdx.game.model.actions.nonhostile;

import com.mygdx.game.model.actions.ActionSegment;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EntityEffect;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.XMovementEffectSettings;
import com.mygdx.game.model.effects.YMovementEffectSettings;
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
		return windupTime;
	}

	@Override
	public float getWindUpPlusActionTime() {
		return windupTime + activeTime;
	}

	@Override
	public float getTotalTime() {
		return windupTime + activeTime + cooldownTime;
	}

	@Override
	public ActionSegment cloneActionSegmentWithSourceAndTarget(CharacterModel source, CharacterModel target) {
		return new DialogueAction(this.dialogue, this.dialogueController, this.getActionListener(), source, target);
	}

//	@Override
//	public float getEffectiveRange() {
//		return Float.MAX_VALUE;
//	}
	
	@Override 
	public int getPriority() {
		return ActionSegment.StoryPriority;
	}

	@Override
	public void interruptionBlock() {
		//Do nothing.
	}

	@Override
	public XMovementEffectSettings getXReplacementMovementForStagger() {
		return null;
	}
	
	@Override
	public YMovementEffectSettings getYReplacementMovementForStagger() {
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

	@Override
	public boolean doesNeedDisruptionDuringWindup() {
		return false;
	}

	@Override
	public boolean doesNeedDisruptionDuringActive() {
		return false;
	}
	
	@Override
	public void setDurations(CharacterModel source) {
		this.windupTime = dialogue.windupTime;
		this.activeTime = dialogue.getTimeToDisplayDialogue();
		this.cooldownTime = dialogue.cooldownTime;
	}

	@Override
	public XMovementEffectSettings getSourceXMove() {
		return null;
	}

	@Override
	public YMovementEffectSettings getSourceYMove() {
		return null;
	}

	@Override
	public boolean chainsWithJump() {
		return false;
	}
	
	@Override
	public boolean isSuper() {
		return false;
	}

	@Override
	public boolean metChainConditions() {
		return false;
	}

	@Override
	public boolean willActionHitTarget(CharacterModel target) {
		return true;
	}

}
