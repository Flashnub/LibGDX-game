package com.mygdx.game.model.actions;

import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.Effect;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.MovementEffectSettings;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.events.ActionListener;

public class Attack extends ActionSegment {
	 
	Rectangle hitBox;
	CharacterModel source;
	int allegiance;
	AttackSettings settings;
	
	public Attack(CharacterModel source, AttackSettings settings) {
		super();
		this.source = source;
		this.allegiance = source.getAllegiance();
		this.settings = settings;
		this.isConcurrent = settings.isConcurrent;
		this.hitBox = new Rectangle(settings.originX, settings.originY, settings.width, settings.height);
	}
	
	public void processAttackOnCharacter(CharacterModel target) {
		for (EffectSettings effectSettings : this.settings.targetEffectSettings) {
			Effect effect = EffectInitializer.initializeEffect(effectSettings);
			target.addEffect(effect);
		}
	}
	
	@Override
	public ActionSegment cloneActionSegment() {
		Attack attack = new Attack(source, settings);
		return attack;
	}
	
	@Override
	public void sendActionToListener(ActionListener actionListener) {
		actionListener.processAttack(this);
	}
	
	@Override
	public void sourceProcess(CharacterModel source) {
		super.sourceProcess(source);
		for (EffectSettings effectSettings : settings.sourceEffectSettings) {
			Effect effect = EffectInitializer.initializeEffect(effectSettings);
			source.addEffect(effect);
		}
	}
	
	@Override
	public float getEffectiveRange() {
		float range = 0f;
		for (EffectSettings effectSettings : this.settings.sourceEffectSettings) {
			if (effectSettings instanceof MovementEffectSettings) {
				MovementEffectSettings mEffectSettings = (MovementEffectSettings) effectSettings;
			}
		}
		return range;
	}

	@Override
	public float getDelayToActivate() {
		return this.settings.delayToActivate;
	}
	
	
	public boolean isFinished() {
		return currentTime > this.settings.delayToActivate + this.settings.duration;
	}
	
	public Rectangle getAttackHitBox() {
		return new Rectangle(settings.originX, settings.originY, settings.width, settings.height);
	}
	
	public Attack() {
		
	}

	public Rectangle getHitBox() {
		return hitBox;
	}

	public CharacterModel getSource() {
		return source;
	}

	public int getAllegiance() {
		return allegiance;
	}

	public void setHitBox(Rectangle hitBox) {
		this.hitBox = hitBox;
	}

	public void setSource(CharacterModel source) {
		this.source = source;
	}

	public void setAllegiance(int allegiance) {
		this.allegiance = allegiance;
	}







}
