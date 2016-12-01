package com.mygdx.game.model.actions;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.events.ActionListener;

public abstract class ActionSegment {
	
	enum ActionState {
		WINDUP, ACTION, COOLDOWN
	}
	
	public static int StoryPriority = 1;
	public static int CombatPriority = 2;
	
	Float currentTime;
	boolean hasProcessedSource;
	boolean didChangeState;
	CharacterModel source;
	ActionState actionState;
	
	public ActionSegment() {
		
	}

	public void sourceProcess(CharacterModel source) {
		hasProcessedSource = true;
		this.setActionState(ActionState.ACTION);
		sourceProcessWithoutSuper(source);
	}
	
	
	public void update(float delta, ActionListener actionListener) {
		if (this.currentTime == null) {
			this.currentTime = 0f;
		}
		this.currentTime += delta;
		if (currentTime >= this.getWindUpPlusActionTime()) {
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
			System.out.println("Action State" + state);
			System.out.println("Action time:" + this.currentTime);
			this.didChangeState = true;
			this.actionState = state;
		}
	}
	
	public boolean isFinished() {
		return currentTime >= this.getTotalTime();
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
}
