package com.mygdx.game.model.actions.nonhostile;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.model.characters.player.Player;
import com.mygdx.game.model.effects.EffectSettings;

public abstract class DialogueSettings implements Serializable{
	String writtenDialogue;
	String cooldownState;
	String windupState;
	String actingState;
	String fontName;
	String fontColor;
	Array <EffectSettings> targetEffects;
	Float speed;
	Float windupTime;
	Float cooldownTime;
	
	//Use this class for when player talks to npc actively.
	String UUIDForNextCharacterToTalk;
	String UUIDForNextDialogue; //Should only be needed for if the npc points back to the player for dialogue.
	
	public static String stateless = "StatelessDialogue";
	public static String defaultState = "Dialogue";
	public static String defaultFontName = "white_font";
	public static String defaultFontColor = "white";
	public static Float defaultSpeed = 2f; //2 characters per second.
	public static Float defaultCooldownTime = 3f;
	public static Float defaultWindupTime = 0f;
	
	@Override
	public void write(Json json) {
		json.writeValue("writtenDialogue", writtenDialogue);
//		json.writeValue("sourceName", sourceName);
		json.writeValue("targetEffects", targetEffects);
		json.writeValue("speed", speed);
		json.writeValue("windupTime", windupTime);
		json.writeValue("cooldownTime", cooldownTime);
		json.writeValue("windupTime", windupTime);
		json.writeValue("windupState", windupState);
		json.writeValue("actingState", actingState);
		json.writeValue("cooldownState", cooldownState);
		json.writeValue("fontName", fontName);
		json.writeValue("fontColor", fontColor);
		json.writeValue("UUIDForNextDialogue", UUIDForNextDialogue);
		json.writeValue("UUIDForNextCharacterToTalk", UUIDForNextCharacterToTalk);
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void read(Json json, JsonValue jsonData) {
		targetEffects = json.readValue("targetEffects", Array.class, jsonData);
//		sourceName = json.readValue("sourceName", String.class, jsonData);
		writtenDialogue = json.readValue("writtenDialogue", String.class, jsonData);
		Boolean hasState = json.readValue("hasState", Boolean.class, jsonData);
		if (hasState == null) {
			hasState = false;
		}

		Float speed = json.readValue("speed", Float.class, jsonData);
		if (speed != null) {
			this.speed = speed;
		}
		else {
			this.speed = DialogueSettings.defaultSpeed;
		}
		
		Float cooldownTime = json.readValue("cooldownTime", Float.class, jsonData);
		if (cooldownTime != null) {
			this.cooldownTime = cooldownTime;
		}
		else {
			this.cooldownTime = DialogueSettings.defaultCooldownTime;
		}		
		
		Float windupTime = json.readValue("windupTime", Float.class, jsonData);
		if (windupTime != null) {
			this.windupTime = windupTime;
		}
		else {
			this.windupTime = DialogueSettings.defaultWindupTime;
		}
		
		String windupState = json.readValue("windupState", String.class, jsonData);
		if (windupTime != null) {
			this.windupState = windupState;
		}
		else {
			this.windupState = hasState ? DialogueSettings.defaultState : DialogueSettings.stateless;
		}
		
		String actingState = json.readValue("actingState", String.class, jsonData);
		if (actingState != null) {
			this.actingState = actingState;
		}
		else {
			this.actingState = hasState ? DialogueSettings.defaultState : DialogueSettings.stateless;
		}
		
		String cooldownState = json.readValue("cooldownState", String.class, jsonData);
		if (cooldownState != null) {
			this.cooldownState = cooldownState;
		}
		else {
			this.cooldownState = hasState ? DialogueSettings.defaultState : DialogueSettings.stateless;
		}
		
		String fontName = json.readValue("fontName", String.class, jsonData);
		if (fontName != null) {
			this.fontName = fontName;
		}
		else {
			this.fontName = DialogueSettings.defaultFontName;
		}
		
		String fontColor = json.readValue("fontColor", String.class, jsonData);
		if (fontColor != null) {
			this.fontColor = fontColor;
		}
		else {
			this.fontColor = DialogueSettings.defaultFontColor;
		}
		
		this.UUIDForNextDialogue = json.readValue("UUIDForNextDialogue", String.class, jsonData);
		this.UUIDForNextCharacterToTalk = json.readValue("UUIDForNextCharacterToTalk", String.class, jsonData);
	}
	
	public float getTimeToDisplayDialogue() {
		return this.writtenDialogue.length() / this.speed;
	}

	public String getWrittenDialogue() {
		return writtenDialogue;
	}

	public String getCooldownState() {
		return cooldownState;
	}

	public String getWindupState() {
		return windupState;
	}

	public String getActingState() {
		return actingState;
	}

	public Float getSpeed() {
		return speed;
	}

	public Float getWindupTime() {
		return windupTime;
	}

	public Float getCooldownTime() {
		return cooldownTime;
	}

	public String getFontName() {
		return fontName;
	}

	public String getFontColor() {
		return fontColor;
	}

	public boolean doesHaveMoreDialogue() {
		return this.UUIDForNextCharacterToTalk != null;
	}
	
	public String getUUIDForNextDialogue() {
		return UUIDForNextDialogue;
	}

	public String getUUIDForNextCharacterToTalk() {
		return UUIDForNextCharacterToTalk;
	}
	
	
}
