package com.mygdx.game.model.characters;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.actions.nonhostile.DialogueSettings;
import com.mygdx.game.model.actions.nonhostile.NormalDialogueSettings;
import com.mygdx.game.model.actions.nonhostile.ConditionalDialogueSettings;
import com.mygdx.game.model.actions.nonhostile.DialogueIndex;
import com.mygdx.game.model.characters.Character.CharacterModel;

public class NPCProperties implements Serializable {
	HashMap <String, NormalDialogueSettings> storyDialogues;
	HashMap <String, NormalDialogueSettings> externalConditionalDialogues;
	Array <ConditionalDialogueSettings> conditionalDialogues;
	DialogueIndex currentIndex;
	String UUID;
	float patrolWalkSpeed;
	
    // should get this from the TMX file, not the json file.
    private Array <Float> patrolWaypoints;
    private float patrolDuration;
    private float breakDuration;
 

	@Override
	public void write(Json json) {
		json.writeValue("storyDialogues", storyDialogues);
		json.writeValue("conditionalDialogues", conditionalDialogues);
		json.writeValue("externalConditionalDialogues", externalConditionalDialogues);
		json.writeValue("UUID", UUID);
		json.writeValue("patrolWalkSpeed", patrolWalkSpeed);

	}

	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		storyDialogues = json.readValue("storyDialogues", HashMap.class, jsonData);
		if (storyDialogues == null) {
			storyDialogues = new HashMap<String, NormalDialogueSettings>();
		}
		conditionalDialogues = json.readValue("conditionalDialogues", Array.class, jsonData);
		if (conditionalDialogues == null) {
			conditionalDialogues = new Array <ConditionalDialogueSettings>();
		}
		externalConditionalDialogues = json.readValue("externalConditionalDialogues", HashMap.class, jsonData);
		if (externalConditionalDialogues == null) {
			this.externalConditionalDialogues = new HashMap<String, NormalDialogueSettings>();
		}
		UUID = json.readValue("UUID", String.class, jsonData);
		Float patrolWalkSpeed = json.readValue("patrolWalkSpeed", Float.class, jsonData);
		if (patrolWalkSpeed != null) {
			this.patrolWalkSpeed = patrolWalkSpeed.floatValue();
		}
		else {
			this.patrolWalkSpeed = 100f;
		}
		
		currentIndex = new DialogueIndex(0, 0);
		this.patrolWaypoints = new Array <Float>();
	}
		
	public DialogueSettings getNextStoryDialogue() {
		DialogueSettings dialogue = storyDialogues.get(currentIndex.toString());
		for (String key : storyDialogues.keySet()) {
			DialogueIndex index = new DialogueIndex(key);
			if (index.getChapterIndex() == this.currentIndex.getChapterIndex() 
			 && index.getDialogueIndex() > this.currentIndex.getDialogueIndex())
			{
				currentIndex.setDialogueIndex(currentIndex.getDialogueIndex() + 1);
				break;
			}
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

	public HashMap<String, NormalDialogueSettings> getNormalDialogues() {
		return storyDialogues;
	}

	public Array<ConditionalDialogueSettings> getConditionalDialogues() {
		return conditionalDialogues;
	}

	public float getPatrolDuration() {
		return patrolDuration;
	}

	public void setPatrolDuration(float patrolDuration) {
		this.patrolDuration = patrolDuration;
	}

	public float getBreakDuration() {
		return breakDuration;
	}

	public void setBreakDuration(float breakDuration) {
		this.breakDuration = breakDuration;
	}

	public Array<Float> getPatrolWaypoints() {
		return patrolWaypoints;
	}
	
	
	
	public void setPatrolWaypoints(Array<Float> patrolWaypoints) {
		this.patrolWaypoints = patrolWaypoints;
	}

	public Float getRandomPatrolWayPoint(Float startPatrol) {
		float wayPoint = startPatrol;
		if (this.patrolWaypoints.size == 0) {
			return wayPoint;
		}
		while (wayPoint == startPatrol) {
			int randomIndex = ThreadLocalRandom.current().nextInt(0, this.patrolWaypoints.size);
			wayPoint = this.patrolWaypoints.get(randomIndex);
		}
		return wayPoint;
	}
}
