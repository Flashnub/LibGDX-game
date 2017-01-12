package com.mygdx.game.constants;

import com.badlogic.gdx.Input.Keys;
import com.mygdx.game.model.characters.player.GameSave;

public class InputConverter {
	
	GameSave playerSave;
	boolean isUsingController;
	
	
	
	public enum DirectionalInput {
		LEFT, RIGHT, UP, DOWN, NONE
	}
	
	public InputConverter (GameSave save) {
		this.playerSave = save;
		this.isUsingController = false;
	}
	
	public String convertIntToInputType(int inputCode, DirectionalInput directionHeld) {
		String result = "";
		if (isUsingController) {
			
		}
		else {
			String inputName = Keys.toString(inputCode);
			if (directionHeld != null && !directionHeld.equals(DirectionalInput.NONE) && !this.isDirectionalInput(inputName) && this.doesHaveDirectionalInputs(inputName)) {
				inputName = inputName.concat(directionHeld.toString());
			}
			if (inputName != null) {
				result = playerSave.getKBMouseScheme().get(inputName);
			}
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
	
	private boolean doesHaveDirectionalInputs(String inputName) {
		String test = playerSave.getKBMouseScheme().get(inputName);
		switch(test) {
		case InputType.LOCKON:
		case InputType.JUMP:
		case InputType.ACTION:
			return false;
		default:
			return true;
		}	}
	
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

	public boolean isUsingController() {
		return isUsingController;
	}

	public void setUsingController(boolean isUsingController) {
		this.isUsingController = isUsingController;
	}
	
}
