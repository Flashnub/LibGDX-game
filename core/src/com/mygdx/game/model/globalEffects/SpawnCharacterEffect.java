package com.mygdx.game.model.globalEffects;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EffectController;
import com.mygdx.game.model.events.CollisionChecker;

public class SpawnCharacterEffect extends WorldEffect {
	
	SpawnCharacterEffectSettings spawnSettings;
	Vector2 origin;
	public static String type = "Spawn";
	
	public SpawnCharacterEffect(WorldEffectSettings settings, EffectController retriever, CollisionChecker collisionChecker, CharacterModel source) {
		super(settings, retriever, collisionChecker, source);
	
		if (settings instanceof SpawnCharacterEffectSettings) {
			this.spawnSettings = (SpawnCharacterEffectSettings) settings;
		}
		origin = retriever.spawnOriginForChar();
	}

	@Override
	protected void initialProcess() {
		super.initialProcess();	
		this.actionListener.spawnCharacter(this.spawnSettings.characterName, this.spawnSettings.characterType, origin.x, origin.y);
	}

	@Override
	public float getEffectiveRange() {
		return Float.MAX_VALUE;
	}

	@Override
	public String getType() {
		return SpawnCharacterEffect.type;
	}
	
}
