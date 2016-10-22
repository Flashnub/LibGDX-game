package com.mygdx.game.model.effects;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.characters.Character.CharacterModel;

public class MovementEffect extends Effect {

	Vector2 oldAccel;
	MovementEffectSettings mSettings;	
	
	public MovementEffect(EffectSettings settings) {
		super(settings);
		oldAccel = new Vector2();
		if (settings instanceof MovementEffectSettings) {
			this.mSettings = (MovementEffectSettings) settings;
		}
	}

	
	public void initialProcess(CharacterModel target) {
		target.getVelocity().x = this.mSettings.velocity.x;
		target.getVelocity().y = this.mSettings.velocity.y;	
		
		oldAccel.x = target.acceleration.x;
		oldAccel.y = target.acceleration.y;
		
		target.acceleration.x = this.mSettings.acceleration.x;
		target.acceleration.y = this.mSettings.acceleration.y;
		System.out.println("Movement IP");
	}
	
	@Override
	public void completion(CharacterModel target) {
		target.acceleration.x = oldAccel.x;
		target.acceleration.y = oldAccel.y;
		
		System.out.println("Movement completion");
		System.out.println(oldAccel);
	}
	
	public Vector2 getOldAccel() {
		return oldAccel;
	}

	public void setOldAccel(Vector2 oldAccel) {
		this.oldAccel = oldAccel;
	}

}
