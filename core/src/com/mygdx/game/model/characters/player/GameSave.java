package com.mygdx.game.model.characters.player;

import java.util.HashMap;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.game.constants.InputConverter.DirectionalInput;
import com.mygdx.game.constants.InputType;
import com.mygdx.game.constants.XBox360Pad;

public class GameSave implements Serializable{
	
	public enum UUIDType {
		OBJECT, ITEM;
	}
	

	Array <Integer> objectsInteractedHistory;
	Array <Integer> acquiredItemsHistory;
	HashMap <String, Integer> npcChapterIndices;
	Array <String> inventoryItemKeys;
	HashMap <String, String> controllerScheme;
	HashMap <String, String> KBMouseScheme;

	
	public GameSave() {
		
	}

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		json.writeValue("objectsInteractedHistory", objectsInteractedHistory);
		json.writeValue("acquiredItemsHistory", acquiredItemsHistory);
		json.writeValue("inventoryItemKeys", inventoryItemKeys);
		json.writeValue("npcChapterIndices", npcChapterIndices);
		json.writeValue("controlScheme", controllerScheme);
		json.writeValue("KBMouseScheme", KBMouseScheme);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		objectsInteractedHistory = json.readValue("objectsInteractedHistory", Array.class, jsonData);
		acquiredItemsHistory = json.readValue("acquiredItemsHistory", Array.class, jsonData);
		inventoryItemKeys = json.readValue("inventoryItemKeys", Array.class, jsonData);
		npcChapterIndices = json.readValue("npcChapterIndices", HashMap.class, jsonData);
		controllerScheme = json.readValue("controlScheme", HashMap.class, jsonData);
		KBMouseScheme = json.readValue("KBMouseScheme", HashMap.class, jsonData);

	}
	
	public Integer chapterIndexForNPCUUID(String UUID) {
		if (this.npcChapterIndices.containsKey(UUID)) {
			return npcChapterIndices.get(UUID);
		}
		return 0;
	}
	
	public boolean isUUIDInSave(Integer UUID) {
		return this.acquiredItemsHistory.contains(UUID, false) || this.objectsInteractedHistory.contains(UUID, false);
	}
	
	public void addUUIDToSave(Integer UUID, UUIDType uuidType) {
		switch (uuidType) {
		case OBJECT:
			if (!objectsInteractedHistory.contains(UUID, false))
				objectsInteractedHistory.add(UUID);
			break;
		case ITEM:			
			if (!acquiredItemsHistory.contains(UUID, false))
				acquiredItemsHistory.add(UUID);
			break;
		}
	}

	public Array<Integer> getObjectsInteractedHistory() {
		return objectsInteractedHistory;
	}

	public Array<Integer> getAcquiredItemsHistory() {
		return acquiredItemsHistory;
	}

	public Array<String> getInventoryItemKeys() {
		return inventoryItemKeys;
	}
	
	public HashMap<String, String> getControllerScheme() {
		return controllerScheme;
	}

	public HashMap<String, String> getKBMouseScheme() {
		return KBMouseScheme;
	}

	public static GameSave testSave() {
		GameSave gameSave = new GameSave();
		gameSave.acquiredItemsHistory = new Array<Integer>();
		gameSave.inventoryItemKeys = new Array<String>();
		gameSave.objectsInteractedHistory = new Array<Integer>();
		
		//GamePad Scheme
		gameSave.controllerScheme = new HashMap <String, String>();
		gameSave.controllerScheme.put(XBox360Pad.axisCodeToString(XBox360Pad.AXIS_LEFT_Y, -.5f), InputType.UP);
		gameSave.controllerScheme.put(XBox360Pad.axisCodeToString(XBox360Pad.AXIS_LEFT_X, -.5f), InputType.LEFT);
		gameSave.controllerScheme.put(XBox360Pad.axisCodeToString(XBox360Pad.AXIS_LEFT_Y, .5f), InputType.DOWN);
		gameSave.controllerScheme.put(XBox360Pad.axisCodeToString(XBox360Pad.AXIS_LEFT_X, .5f), InputType.RIGHT);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_L3), InputType.USEITEM);
//		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_LB), InputType.ACTION);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_LB), InputType.ACTIONCANCEL);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_A), InputType.JUMP);
		
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_X), InputType.LIGHTATTACK);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_X).concat(DirectionalInput.UP.toString()), InputType.UPLIGHTATTACK);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_X).concat(DirectionalInput.LEFT.toString()), InputType.LEFTLIGHTATTACK);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_X).concat(DirectionalInput.DOWN.toString()), InputType.DOWNLIGHTATTACK);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_X).concat(DirectionalInput.RIGHT.toString()), InputType.RIGHTLIGHTATTACK);

		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_Y), InputType.MEDIUMATTACK);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_Y).concat(DirectionalInput.UP.toString()), InputType.UPMEDIUMATTACK);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_Y).concat(DirectionalInput.LEFT.toString()), InputType.LEFTMEDIUMATTACK);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_Y).concat(DirectionalInput.DOWN.toString()), InputType.DOWNMEDIUMATTACK);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_Y).concat(DirectionalInput.RIGHT.toString()), InputType.RIGHTMEDIUMATTACK);

		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_B), InputType.HEAVYATTACK);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_B).concat(DirectionalInput.UP.toString()), InputType.UPHEAVYATTACK);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_B).concat(DirectionalInput.LEFT.toString()), InputType.LEFTHEAVYATTACK);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_B).concat(DirectionalInput.DOWN.toString()), InputType.DOWNHEAVYATTACK);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_B).concat(DirectionalInput.RIGHT.toString()), InputType.RIGHTHEAVYATTACK);

		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_RB), InputType.SPECIAL);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_RB).concat(DirectionalInput.UP.toString()), InputType.UPSPECIAL);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_RB).concat(DirectionalInput.LEFT.toString()), InputType.LEFTSPECIAL);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_RB).concat(DirectionalInput.DOWN.toString()), InputType.DOWNSPECIAL);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_RB).concat(DirectionalInput.RIGHT.toString()), InputType.RIGHTSPECIAL);

		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_R3), InputType.MOVEMENT);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_R3).concat(DirectionalInput.UP.toString()), InputType.UPMOVEMENT);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_R3).concat(DirectionalInput.LEFT.toString()), InputType.LEFTMOVEMENT);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_R3).concat(DirectionalInput.DOWN.toString()), InputType.DOWNMOVEMENT);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_R3).concat(DirectionalInput.RIGHT.toString()), InputType.RIGHTMOVEMENT);
		
		//KBMouse Scheme
		gameSave.KBMouseScheme = new HashMap <String, String> ();
		gameSave.KBMouseScheme.put(Keys.toString(Keys.W), InputType.UP);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.A), InputType.LEFT);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.S), InputType.DOWN);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.D), InputType.RIGHT);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.TAB), InputType.USEITEM);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.E), InputType.ACTIONCANCEL);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.SPACE), InputType.JUMP);
		
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Z), InputType.LIGHTATTACK);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Z).concat(DirectionalInput.UP.toString()), InputType.UPLIGHTATTACK);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Z).concat(DirectionalInput.LEFT.toString()), InputType.LEFTLIGHTATTACK);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Z).concat(DirectionalInput.DOWN.toString()), InputType.DOWNLIGHTATTACK);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Z).concat(DirectionalInput.RIGHT.toString()), InputType.RIGHTLIGHTATTACK);

		gameSave.KBMouseScheme.put(Keys.toString(Keys.X), InputType.MEDIUMATTACK);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.X).concat(DirectionalInput.UP.toString()), InputType.UPMEDIUMATTACK);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.X).concat(DirectionalInput.LEFT.toString()), InputType.LEFTMEDIUMATTACK);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.X).concat(DirectionalInput.DOWN.toString()), InputType.DOWNMEDIUMATTACK);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.X).concat(DirectionalInput.RIGHT.toString()), InputType.RIGHTMEDIUMATTACK);
		
		gameSave.KBMouseScheme.put(Keys.toString(Keys.C), InputType.HEAVYATTACK);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.C).concat(DirectionalInput.UP.toString()), InputType.UPHEAVYATTACK);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.C).concat(DirectionalInput.LEFT.toString()), InputType.LEFTHEAVYATTACK);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.C).concat(DirectionalInput.DOWN.toString()), InputType.DOWNHEAVYATTACK);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.C).concat(DirectionalInput.RIGHT.toString()), InputType.RIGHTHEAVYATTACK);
		
		gameSave.KBMouseScheme.put(Keys.toString(Keys.R), InputType.SPECIAL);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.R).concat(DirectionalInput.UP.toString()), InputType.UPSPECIAL);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.R).concat(DirectionalInput.LEFT.toString()), InputType.LEFTSPECIAL);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.R).concat(DirectionalInput.DOWN.toString()), InputType.DOWNSPECIAL);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.R).concat(DirectionalInput.RIGHT.toString()), InputType.RIGHTSPECIAL);

		gameSave.KBMouseScheme.put(Keys.toString(Keys.Q), InputType.MOVEMENT);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Q).concat(DirectionalInput.UP.toString()), InputType.UPMOVEMENT);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Q).concat(DirectionalInput.LEFT.toString()), InputType.LEFTMOVEMENT);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Q).concat(DirectionalInput.DOWN.toString()), InputType.DOWNMOVEMENT);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Q).concat(DirectionalInput.RIGHT.toString()), InputType.RIGHTMOVEMENT);
		
		return gameSave;
	}
}
