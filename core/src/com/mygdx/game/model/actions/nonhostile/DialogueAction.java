package com.mygdx.game.model.actions.nonhostile;

import com.mygdx.game.model.actions.ActionSegment;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.Effect;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.DialogueListener;
import com.mygdx.game.model.world.DialogueController;

public class DialogueAction extends ActionSegment {
	DialogueSettings dialogue;
	DialogueController dialogueController;
	CharacterModel target;
	
	public DialogueAction(DialogueSettings dialogue, DialogueController dialogueController, CharacterModel source, CharacterModel target) {
		super();
		this.setSource(source);
		this.dialogue = dialogue;
		this.dialogueController = dialogueController;
		this.target = target;
	}
	
	@Override
	public void sendActionToListener(ActionListener actionListener) {
		// Do nothing.
	}

	@Override
	public void sourceProcessWithoutSuper(CharacterModel source) {
		if (dialogue.targetEffects != null) {
			for (EffectSettings effectSettings : dialogue.targetEffects) {
				Effect effect = EffectInitializer.initializeEffect(effectSettings);
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
		return new DialogueAction(this.dialogue, this.dialogueController, source, target);
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

}
