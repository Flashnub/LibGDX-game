package com.mygdx.game.model.world;

import com.mygdx.game.model.characters.Character.CharacterModel;

public interface DialogueActor {
	public String getUUID();
	public void dialogueAction(CharacterModel target);
	public void externalDialogueAction(CharacterModel target, String UUIDForDialogue);
}
