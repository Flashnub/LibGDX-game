package com.mygdx.game.model.actions;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.events.ActionListener;

public abstract class ActionSegment {
	
	float currentTime;
	boolean isConcurrent;
	boolean hasProcessedSource;
	
	public ActionSegment() {
		hasProcessedSource = false;
		currentTime = 0f;
	}
	
	public abstract void sendActionToListener(ActionListener actionListener);
	public void sourceProcess(CharacterModel source) {
		hasProcessedSource = true;
	}
	
	public void update(float delta, ActionListener actionListener) {
		this.currentTime += delta;
		if (currentTime >= this.getDelayToActivate()) {
			if (!this.hasProcessedSource) {
				sourceProcess(getSource());
			}
			sendActionToListener(actionListener);
		}
	}	
	
	public abstract CharacterModel getSource();
	public abstract float getDelayToActivate();
	public abstract ActionSegment cloneActionSegment();
	public abstract boolean isFinished();
}
