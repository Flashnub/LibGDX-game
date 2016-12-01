package com.mygdx.game.model.actions.nonhostile;

public class DialogueIndex {
	int dialogueIndex;
	int chapterIndex;
	
	public DialogueIndex(int dialogueIndex, int chapterIndex) {
		this.dialogueIndex = dialogueIndex;
		this.chapterIndex = chapterIndex;
	}

	public int getDialogueIndex() {
		return dialogueIndex;
	}

	public int getChapterIndex() {
		return chapterIndex;
	}

	public void setDialogueIndex(int dialogueIndex) {
		this.dialogueIndex = dialogueIndex;
	}

	public void setChapterIndex(int chapterIndex) {
		this.chapterIndex = chapterIndex;
	}
	
	
}
