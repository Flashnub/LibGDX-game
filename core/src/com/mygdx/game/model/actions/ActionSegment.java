package com.mygdx.game.model.actions;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EffectController;
import com.mygdx.game.model.events.ActionListener;

public abstract class ActionSegment implements EffectController {
	
	public enum ActionState {
		WINDUP, ACTION, COOLDOWN
	}
	
	public static int StoryPriority = 1;
	public static int CombatPriority = 2;
	
	Float currentTime;
	boolean hasProcessedActiveSource;
	boolean hasProcessedWindupSource;
	boolean didChangeState;
	boolean forceEnd; //Used for ending actions involuntarily (Removes from processing)
	boolean forceCooldownState; //Used for actions that end on demand rather than fixed time 
	boolean hasProcessedInterruption;
	boolean hasProcessedCompletion;
	boolean shouldChain;
	CharacterModel source;
	ActionState actionState;
	float activeTime;

	ActionListener actionListener;
	
	public ActionSegment() {
		forceEnd = false;
		forceCooldownState = false;
		hasProcessedActiveSource = false;
		hasProcessedWindupSource = false;
		hasProcessedInterruption = false;
		hasProcessedCompletion = false;
		shouldChain = false;
		activeTime = 0f;
	}
	
	public ActionSegment(ActionListener listener) {
		this();
		this.actionListener = listener;
		
	}

	public void sourceActiveProcess(CharacterModel source) {
		hasProcessedActiveSource = true;
		this.setActionState(ActionState.ACTION);
		sourceActiveProcessWithoutSuper(source);
	}
	
	public void sourceWindupProcess(CharacterModel source) {
		hasProcessedWindupSource = true;
		this.setActionState(ActionState.WINDUP);
		sourceWindupProcessWithoutSuper(source);
		if (!source.isActionLock()) {
			source.lockControls();
		}
	}
	
	public void completionBlock(CharacterModel source) {
		this.setActionState(ActionState.COOLDOWN);
		this.hasProcessedCompletion = true;
		this.sourceCompletionWithoutSuper(source);
//		System.out.println("Completion");
	}
	
	
	public void update(float delta) {
		if (this.currentTime == null) {
			this.currentTime = 0f;
		}
		this.currentTime += delta;
		if (currentTime >= this.getWindUpPlusActionTime() || this.forceCooldownState || this.forceEnd) {
			if (!hasProcessedInterruption && (forceCooldownState || this.forceEnd)) {
				this.interruptionBlock();
			}
			if (!this.hasProcessedCompletion) {
				this.completionBlock(source);
			}
		}
		else if (currentTime >= this.getWindUpTime() && hasProcessedWindupSource) {
			if (!this.hasProcessedActiveSource) {
				sourceActiveProcess(getSource());
			}
			sendActionToListener(actionListener, delta);
			this.activeTime += delta;
		}
		else if (!this.hasProcessedWindupSource){
			sourceWindupProcess(source);
		}
	}	
	
	public void setSource(CharacterModel source) {
		this.source = source;
	} 
	
	public CharacterModel getSource() {
		return this.source; 
	}
	
	public ActionState getActionState() {
		return this.actionState;
	}
	
	public void setActionState(ActionState state) {
		if (this.actionState != state) {
//			System.out.println("Action State" + state);
//			System.out.println("Action time:" + this.currentTime);
			this.didChangeState = true;
			this.actionState = state;
		}
	}
	
	public boolean isFinished() {
		return currentTime >= this.getTotalTime() || forceEnd;
	}
	
	public int getPriority() {
		return ActionSegment.CombatPriority;
	}
	
	public boolean shouldChain() {
		return shouldChain;
	}
	
	public void setShouldChain(boolean shouldChain) {
		this.shouldChain = shouldChain;
	}

	@Override
	public ActionListener getActionListener() {
		return this.actionListener;
	}
	
	@Override
	public Vector2 spawnOriginForChar() {
		if (source.isFacingLeft()) {
			return new Vector2(this.source.getImageHitBox().x + this.source.getImageHitBox().width + 100, this.source.getImageHitBox().y);
		}
		return new Vector2(this.source.getImageHitBox().x - 200, this.source.getImageHitBox().y);
	}
	
	public abstract void sendActionToListener(ActionListener actionListener, float delta);
	public abstract void sourceActiveProcessWithoutSuper(CharacterModel source);
	public abstract void sourceWindupProcessWithoutSuper(CharacterModel source);
	public abstract void sourceCompletionWithoutSuper(CharacterModel source);
	public abstract float getWindUpTime();
	public abstract float getWindUpPlusActionTime();
	public abstract float getTotalTime();
	public abstract ActionSegment cloneActionSegmentWithSourceAndTarget(CharacterModel source, CharacterModel target);
	public abstract float getEffectiveRange();
	public abstract void interruptionBlock();
	public abstract boolean shouldRespectEntityCollisions();
//	public abstract boolean canBeInterrupted();
}
