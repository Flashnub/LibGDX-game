package com.mygdx.game.model.actions;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.events.ActionListener;

public abstract class ActionSegment {
	
	enum ActionState {
		WINDUP, ACTION, COOLDOWN
	}
	
	float currentTime;
	boolean hasProcessedSource;
	boolean didChangeState;
	CharacterModel source;
	ActionState actionState;
	
	public ActionSegment() {
		hasProcessedSource = false;
		currentTime = 0f;
		this.setActionState(ActionState.WINDUP);
	}
	
	public abstract void sendActionToListener(ActionListener actionListener);
	public void sourceProcess(CharacterModel source) {
		hasProcessedSource = true;
		this.setActionState(ActionState.ACTION);
	}
	
	public void update(float delta, ActionListener actionListener) {
		this.currentTime += delta;
		if (currentTime >= this.getWindUpTime()) {
			if (!this.hasProcessedSource) {
				sourceProcess(getSource());
			}
			sendActionToListener(actionListener);
		}
		if (currentTime >= this.getWindUpPlusActionTime()) {
			this.setActionState(ActionState.COOLDOWN);
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
		this.actionState = state;
		this.didChangeState = true;
	}
	
	public abstract float getWindUpTime();
	public abstract float getWindUpPlusActionTime();
	public abstract ActionSegment cloneActionSegmentWithSourceAndTarget(CharacterModel source, CharacterModel target);
	public abstract boolean isFinished();
	public abstract float getEffectiveRange();
}
