package com.mygdx.game.model.characters;

public class InteractableCharacter extends Character{

	public InteractableCharacter(String characterName) {
		super(characterName);
	}
	
	public abstract class InteractableCharacterModel extends CharacterModel {

		public InteractableCharacterModel(String characterName, EntityUIModel uiModel) {
			super(characterName, uiModel);
		}
		
	}
}
