package com.mygdx.game.model.actions;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.events.ActionListener;

public abstract class ActionSegment {
	
	public enum ActionState {
		WINDUP, ACTION, COOLDOWN
	}
	
	public static int StoryPriority = 1;
	public static int CombatPriority = 2;
	
	Float currentTime;
	boolean hasProcessedSource;
	boolean didChangeState;
	boolean forceRemove; //Used for ending actions involuntarily (Removes from processing)
	boolean forceInterrupt; //Used for ending actions voluntarily (puts into cooldown state)
	CharacterModel source;
	ActionState actionState;
	float stateTime;
	
	public ActionSegment() {
		forceRemove = false;
		forceInterrupt = false;
		stateTime = 0f;
	}

	public void sourceProcess(CharacterModel source) {
		hasProcessedSource = true;
		this.setActionState(ActionState.ACTION);
		sourceProcessWithoutSuper(source);
		source.lockControls();
	}
	
	
	public void update(float delta, ActionListener actionListener) {
		if (this.currentTime == null) {
			this.currentTime = 0f;
		}
		this.currentTime += delta;
		this.stateTime += delta;
		if (currentTime >= this.getWindUpPlusActionTime() || this.forceInterrupt) {
			if (forceInterrupt) {
				this.interruptionBlock();
			}
			this.setActionState(ActionState.COOLDOWN);
		}
		else if (currentTime >= this.getWindUpTime()) {
			if (!this.hasProcessedSource) {
				sourceProcess(getSource());
			}
			sendActionToListener(actionListener);
		}
		else if (this.actionState != ActionState.WINDUP){
			this.setActionState(ActionState.WINDUP);
		}
		source.shouldUnlockControls(this);
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
		if (this.actionState != state && !forceRemove) {
			System.out.println("Action State" + state);
			System.out.println("Action time:" + this.currentTime);
			this.didChangeState = true;
			this.actionState = state;
		}
	}
	
	public boolean isFinished() {
		return currentTime >= this.getTotalTime() || forceRemove;
	}
	
	public int getPriority() {
		return ActionSegment.CombatPriority;
	}

	
	public abstract void sendActionToListener(ActionListener actionListener);
	public abstract void sourceProcessWithoutSuper(CharacterModel source);
	public abstract float getWindUpTime();
	public abstract float getWindUpPlusActionTime();
	public abstract float getTotalTime();
	public abstract ActionSegment cloneActionSegmentWithSourceAndTarget(CharacterModel source, CharacterModel target);
	public abstract float getEffectiveRange();
	public abstract void interruptionBlock();
//	public abstract boolean canBeInterrupted();
}
