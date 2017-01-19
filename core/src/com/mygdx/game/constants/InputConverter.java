package com.mygdx.game.constants;

import com.badlogic.gdx.Input.Keys;
import com.mygdx.game.model.characters.player.GameSave;

public class InputConverter {
	
	GameSave playerSave;
	
	public enum DirectionalInput {
		LEFT, RIGHT, UP, DOWN, NONE
	}
	
	public InputConverter (GameSave save) {
		this.playerSave = save;
	}
	
	public String convertKeyCodeToInputType(int keyCode, DirectionalInput directionHeld) {
		String result = "";
		String inputName = Keys.toString(keyCode);
		if (directionHeld != null && !directionHeld.equals(DirectionalInput.NONE) && !this.isDirectionalInput(inputName) && this.doesHaveDirectionalInputs(inputName, true)) {
			inputName = inputName.concat(directionHeld.toString());
		}
		if (inputName != null) {
			result = playerSave.getKBMouseScheme().get(inputName);
		}
		
		return result != null ? result : "";
	}
	
	public String convertButtonCodeToInputType(int buttonCode, DirectionalInput directionHeld) {
		String result = "";
		String inputName = XBox360Pad.buttonCodeToString(buttonCode);
		if (directionHeld != null && !directionHeld.equals(DirectionalInput.NONE) && doesHaveDirectionalInputs(inputName, false)) {
			inputName = inputName.concat(directionHeld.toString());
		}
		if (inputName != null) {
			result = playerSave.getControllerScheme().get(inputName);
		}
		return result != null ? result : "";
	}
	
	private boolean isDirectionalInput (String inputName) {
		String test = playerSave.getKBMouseScheme().get(inputName);
		switch(test) {
		case InputType.LEFT:
		case InputType.RIGHT:
		case InputType.UP:
		case InputType.DOWN:
			return true;
		default:
			return false;
		}
	}
	
	private boolean doesHaveDirectionalInputs(String inputName, boolean useKeyboard) {
		String test = useKeyboard ? playerSave.getKBMouseScheme().get(inputName) : playerSave.getControllerScheme().get(inputName);
		switch(test) {
		case InputType.USEITEM:
		case InputType.JUMP:
//		case InputType.ACTION:
		case InputType.ACTIONCANCEL:
			return false;
		default:
			return true;
		}	
	}
	
 	public DirectionalInput getDirectionFromKeyCodeForDown(int keyCode) {
		String inputName = Keys.toString(keyCode);
		String inputType = playerSave.getKBMouseScheme().get(inputName);
		switch (inputType) {
		case InputType.DOWN:
			return DirectionalInput.DOWN;
		case InputType.UP:
			return DirectionalInput.UP;
		case InputType.RIGHT:
			return DirectionalInput.RIGHT;
		case InputType.LEFT:
			return DirectionalInput.LEFT;
		default:
			return DirectionalInput.NONE;
			
		}
	}
 	
	public DirectionalInput getDirectionFromKeyCodeForUp(int keyCode, DirectionalInput currentDirectionHeld) {
		String inputName = Keys.toString(keyCode);
		String inputType = playerSave.getKBMouseScheme().get(inputName);
		switch (inputType) {
		case InputType.DOWN:
		case InputType.UP:
			return DirectionalInput.NONE;
		case InputType.RIGHT:
			if (currentDirectionHeld.equals(DirectionalInput.LEFT)) {
				return null;
			}
			else {
				return DirectionalInput.NONE;
			}
		case InputType.LEFT:
			if (currentDirectionHeld.equals(DirectionalInput.RIGHT)) {
				return null;
			}
			else {
				return DirectionalInput.NONE;
			}
		default:
			return null;
		}
	}

 	public DirectionalInput getDirectionFromAxisCode(int axisCode, float value) {
 		String inputName = XBox360Pad.axisCodeToString(axisCode, value);
 		String inputType = playerSave.getControllerScheme().get(inputName);
 		if (inputType == null) {
 			inputType = "";
 		}
 		switch (inputType) {
 			case InputType.DOWN:
 				return DirectionalInput.DOWN;
 			case InputType.UP:
 				return DirectionalInput.UP;
 			case InputType.RIGHT:
 				return DirectionalInput.RIGHT;
 			case InputType.LEFT:
 				return DirectionalInput.LEFT;
 			default:
 				return DirectionalInput.NONE;
 		}
 	}
 	
// 	public DirectionalInput getDirectionFromAxisCodeForUp(int axisCode, float value, DirectionalInput currentDirectionHeld) {
//		String inputName = XBox360Pad.axisCodeToString(axisCode, value);
//		String inputType = playerSave.getControllerScheme().get(inputName);
//		switch (inputType) {
//		case InputType.DOWN:
//		case InputType.UP:
//			return DirectionalInput.NONE;
//		case InputType.RIGHT:
//			if (currentDirectionHeld.equals(DirectionalInput.LEFT)) {
//				return null;
//			}
//			else {
//				return DirectionalInput.NONE;
//			}
//		case InputType.LEFT:
//			if (currentDirectionHeld.equals(DirectionalInput.RIGHT)) {
//				return null;
//			}
//			else {
//				return DirectionalInput.NONE;
//			}
//		default:
//			return null;
//		}
// 	}
}
