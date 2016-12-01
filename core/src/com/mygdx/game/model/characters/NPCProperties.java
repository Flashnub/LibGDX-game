package com.mygdx.game.model.characters;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.actions.nonhostile.DialogueSettings;
import com.mygdx.game.model.actions.nonhostile.NormalDialogueSettings;
import com.mygdx.game.model.actions.nonhostile.ConditionalDialogueSettings;
import com.mygdx.game.model.actions.nonhostile.DialogueAction;
import com.mygdx.game.model.actions.nonhostile.DialogueIndex;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.events.DialogueListener;

public class NPCProperties implements Serializable {
	HashMap <DialogueIndex, Array<NormalDialogueSettings>> storyDialogues;
	Array <ConditionalDialogueSettings> conditionalDialogues;
	HashMap <String, NormalDialogueSettings> externalConditionalDialogues;
	DialogueIndex currentIndex;
	String UUID;

	@Override
	public void write(Json json) {
		json.writeValue("storyDialogues", storyDialogues);
		json.writeValue("conditionalDialogues", conditionalDialogues);
		json.writeValue("externalConditionalDialogues", externalConditionalDialogues);

		json.writeValue("UUID", UUID);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		storyDialogues = json.readValue("storyDialogues", HashMap.class, jsonData);
		conditionalDialogues = json.readValue("additionalDialogues", Array.class, jsonData);
		externalConditionalDialogues = json.readValue("externalConditionalDialogues", HashMap.class, jsonData);
		UUID = json.readValue("UUID", String.class, jsonData);
		currentIndex = new DialogueIndex(0, 0);
	}
		
	public DialogueSettings getNextStoryDialogue() {
		Array<NormalDialogueSettings> chapterDialogues = storyDialogues.get(currentIndex.getChapterIndex());
		DialogueSettings dialogue = chapterDialogues.get(currentIndex.getDialogueIndex());
		if (currentIndex.getDialogueIndex() < chapterDialogues.size - 1) {
			currentIndex.setDialogueIndex(currentIndex.getDialogueIndex() + 1);
		}
		return dialogue;
	}
	
	public DialogueSettings getSpecificExternalConditionalDialogue(String UUIDForDialogue) {
		return externalConditionalDialogues.get(UUIDForDialogue);
		 
	}
	
	public void setChapterIndex(Integer chapterIndex) {
		this.currentIndex.setChapterIndex(chapterIndex);
	}
	
	public void setSource(CharacterModel source) 
	{
		for(ConditionalDialogueSettings settings : conditionalDialogues) {
			settings.setSource(source);
		}
	}

	public HashMap<DialogueIndex, Array<NormalDialogueSettings>> getNormalDialogues() {
		return storyDialogues;
	}

	public Array<ConditionalDialogueSettings> getConditionalDialogues() {
		return conditionalDialogues;
	}

	
}
