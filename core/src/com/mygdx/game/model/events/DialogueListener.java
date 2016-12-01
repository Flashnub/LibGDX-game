package com.mygdx.game.model.events;

import com.mygdx.game.model.actions.nonhostile.DialogueSettings;

public interface DialogueListener {
	public void updateDialogueText (String dialogueText, String fontName, String fontColor, boolean newDialogue);
	public void endDialogue();
}
