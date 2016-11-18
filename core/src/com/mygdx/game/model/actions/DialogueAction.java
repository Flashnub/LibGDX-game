package com.mygdx.game.model.actions;

import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.events.ActionListener;

public class DialogueAction extends ActionSegment{

	@Override
	public void sendActionToListener(ActionListener actionListener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sourceProcessWithoutSuper(CharacterModel source) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getWindUpTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getWindUpPlusActionTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getTotalTime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ActionSegment cloneActionSegmentWithSourceAndTarget(CharacterModel source, CharacterModel target) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getEffectiveRange() {
		// TODO Auto-generated method stub
		return 0;
	}

}
