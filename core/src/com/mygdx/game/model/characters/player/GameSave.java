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
	Array <String> quickItemKeys;
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
		json.writeValue("quickItemKeys", quickItemKeys);
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
		quickItemKeys = json.readValue("quickItemKeys", Array.class, jsonData);
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
	
	
	
	public Array<String> getQuickItemKeys() {
		return quickItemKeys;
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
		gameSave.quickItemKeys = new Array <String>();
		
		gameSave.inventoryItemKeys.add("Health Potion");
		gameSave.quickItemKeys.add("Health Potion");
		
		//GamePad Scheme
		gameSave.controllerScheme = new HashMap <String, String>();
		gameSave.controllerScheme.put(XBox360Pad.axisCodeToString(XBox360Pad.AXIS_LEFT_Y, -.5f), InputType.UP);
		gameSave.controllerScheme.put(XBox360Pad.axisCodeToString(XBox360Pad.AXIS_LEFT_X, -.5f), InputType.LEFT);
		gameSave.controllerScheme.put(XBox360Pad.axisCodeToString(XBox360Pad.AXIS_LEFT_Y, .5f), InputType.DOWN);
		gameSave.controllerScheme.put(XBox360Pad.axisCodeToString(XBox360Pad.AXIS_LEFT_X, .5f), InputType.RIGHT);
		gameSave.controllerScheme.put(XBox360Pad.axisCodeToString(XBox360Pad.AXIS_LEFT_TRIGGER, .5f), InputType.USEITEM);
//		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_LB), InputType.ACTION);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_LB), InputType.ACTIONCANCEL);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_A), InputType.JUMP);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_A).concat(DirectionalInput.UP.toString()), InputType.JUMPUP);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_A).concat(DirectionalInput.LEFT.toString()), InputType.JUMPLEFT);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_A).concat(DirectionalInput.DOWN.toString()), InputType.JUMPDOWN);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_A).concat(DirectionalInput.RIGHT.toString()), InputType.JUMPRIGHT);

		
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_X), InputType.P);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_X).concat(DirectionalInput.UP.toString()), InputType.PUP);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_X).concat(DirectionalInput.LEFT.toString()), InputType.PLEFT);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_X).concat(DirectionalInput.DOWN.toString()), InputType.PDOWN);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_X).concat(DirectionalInput.RIGHT.toString()), InputType.PRIGHT);

		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_Y), InputType.S);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_Y).concat(DirectionalInput.UP.toString()), InputType.SUP);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_Y).concat(DirectionalInput.LEFT.toString()), InputType.SLEFT);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_Y).concat(DirectionalInput.DOWN.toString()), InputType.SDOWN);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_Y).concat(DirectionalInput.RIGHT.toString()), InputType.SRIGHT);

		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_B), InputType.H);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_B).concat(DirectionalInput.UP.toString()), InputType.HUP);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_B).concat(DirectionalInput.LEFT.toString()), InputType.HLEFT);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_B).concat(DirectionalInput.DOWN.toString()), InputType.HDOWN);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_B).concat(DirectionalInput.RIGHT.toString()), InputType.HRIGHT);

		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_RB), InputType.SP);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_RB).concat(DirectionalInput.UP.toString()), InputType.SPUP);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_RB).concat(DirectionalInput.LEFT.toString()), InputType.SPLEFT);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_RB).concat(DirectionalInput.DOWN.toString()), InputType.SPDOWN);
		gameSave.controllerScheme.put(XBox360Pad.buttonCodeToString(XBox360Pad.BUTTON_RB).concat(DirectionalInput.RIGHT.toString()), InputType.SPRIGHT);

		gameSave.controllerScheme.put(XBox360Pad.axisCodeToString(XBox360Pad.AXIS_RIGHT_TRIGGER, -.5f), InputType.DASH);
		gameSave.controllerScheme.put(XBox360Pad.axisCodeToString(XBox360Pad.AXIS_RIGHT_TRIGGER, -.5f).concat(DirectionalInput.UP.toString()), InputType.DASHUP);
		gameSave.controllerScheme.put(XBox360Pad.axisCodeToString(XBox360Pad.AXIS_RIGHT_TRIGGER, -.5f).concat(DirectionalInput.LEFT.toString()), InputType.DASHLEFT);
		gameSave.controllerScheme.put(XBox360Pad.axisCodeToString(XBox360Pad.AXIS_RIGHT_TRIGGER, -.5f).concat(DirectionalInput.DOWN.toString()), InputType.DASHDOWN);
		gameSave.controllerScheme.put(XBox360Pad.axisCodeToString(XBox360Pad.AXIS_RIGHT_TRIGGER, -.5f).concat(DirectionalInput.RIGHT.toString()), InputType.DASHRIGHT);
		
		gameSave.controllerScheme.put(XBox360Pad.axisCodeToString(XBox360Pad.AXIS_RIGHT_TRIGGER, -.01f), InputType.DASHRELEASE);

		
		//KBMouse Scheme
		gameSave.KBMouseScheme = new HashMap <String, String> ();
		gameSave.KBMouseScheme.put(Keys.toString(Keys.W), InputType.UP);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.A), InputType.LEFT);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.S), InputType.DOWN);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.D), InputType.RIGHT);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.TAB), InputType.USEITEM);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.E), InputType.ACTIONCANCEL);

		gameSave.KBMouseScheme.put(Keys.toString(Keys.SPACE), InputType.JUMP);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.SPACE).concat(DirectionalInput.UP.toString()), InputType.JUMPUP);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.SPACE).concat(DirectionalInput.DOWN.toString()), InputType.JUMPDOWN);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.SPACE).concat(DirectionalInput.LEFT.toString()), InputType.JUMPLEFT);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.SPACE).concat(DirectionalInput.RIGHT.toString()), InputType.JUMPRIGHT);

		
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Z), InputType.P);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Z).concat(DirectionalInput.UP.toString()), InputType.PUP);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Z).concat(DirectionalInput.LEFT.toString()), InputType.PLEFT);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Z).concat(DirectionalInput.DOWN.toString()), InputType.PDOWN);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Z).concat(DirectionalInput.RIGHT.toString()), InputType.PRIGHT);

		gameSave.KBMouseScheme.put(Keys.toString(Keys.X), InputType.S);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.X).concat(DirectionalInput.UP.toString()), InputType.SUP);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.X).concat(DirectionalInput.LEFT.toString()), InputType.SLEFT);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.X).concat(DirectionalInput.DOWN.toString()), InputType.SDOWN);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.X).concat(DirectionalInput.RIGHT.toString()), InputType.SRIGHT);
		
		gameSave.KBMouseScheme.put(Keys.toString(Keys.C), InputType.H);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.C).concat(DirectionalInput.UP.toString()), InputType.HUP);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.C).concat(DirectionalInput.LEFT.toString()), InputType.HLEFT);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.C).concat(DirectionalInput.DOWN.toString()), InputType.HDOWN);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.C).concat(DirectionalInput.RIGHT.toString()), InputType.HRIGHT);
		
		gameSave.KBMouseScheme.put(Keys.toString(Keys.R), InputType.SP);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.R).concat(DirectionalInput.UP.toString()), InputType.SPUP);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.R).concat(DirectionalInput.LEFT.toString()), InputType.SPLEFT);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.R).concat(DirectionalInput.DOWN.toString()), InputType.SPDOWN);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.R).concat(DirectionalInput.RIGHT.toString()), InputType.SPRIGHT);

		gameSave.KBMouseScheme.put(Keys.toString(Keys.Q), InputType.DASH);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Q).concat(DirectionalInput.UP.toString()), InputType.DASHUP);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Q).concat(DirectionalInput.LEFT.toString()), InputType.DASHLEFT);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Q).concat(DirectionalInput.DOWN.toString()), InputType.DASHDOWN);
		gameSave.KBMouseScheme.put(Keys.toString(Keys.Q).concat(DirectionalInput.RIGHT.toString()), InputType.DASHRIGHT);
		
		return gameSave;
	}
}
