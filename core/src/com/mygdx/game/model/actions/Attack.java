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
	int allegiance;
	AttackSettings attackSettings;
	CharacterModel target;
	
	public Attack(CharacterModel source, CharacterModel target, AttackSettings settings) {
		super();
		this.source = source;
		this.allegiance = source.getAllegiance();
		this.attackSettings = settings;
		if (source.isFacingLeft()) {
			this.hitBox = new Rectangle(source.getGameplayHitBox().x + settings.originX - 10, source.getGameplayHitBox().y + settings.originY, -settings.width, settings.height);
		}
		else {
			this.hitBox = new Rectangle(source.getGameplayHitBox().x + settings.originX + 10, source.getGameplayHitBox().y + settings.originY, settings.width, settings.height);
		}
	}
	
	public void processAttackOnCharacter(CharacterModel target) {
		for (EffectSettings effectSettings : this.attackSettings.targetEffectSettings) {
			Effect effect = EffectInitializer.initializeEffect(effectSettings);
			target.addEffect(effect);
		}
	}
	
	@Override
	public ActionSegment cloneActionSegmentWithSourceAndTarget(CharacterModel source, CharacterModel target) {
		Attack attack = new Attack(source, target, attackSettings);
		return attack;
	}
	
	@Override
	public void sendActionToListener(ActionListener actionListener) {
		actionListener.processAttack(this);
	}
	
	@Override
	public void sourceProcess(CharacterModel source) {
		super.sourceProcess(source);
		for (EffectSettings effectSettings : attackSettings.sourceEffectSettings) {
			Effect effect = EffectInitializer.initializeEffect(effectSettings);
			source.addEffect(effect);
		}
	}
	
	@Override
	public float getEffectiveRange() {
		float range = 0f;
		for (EffectSettings effectSettings : this.attackSettings.sourceEffectSettings) {
			if (effectSettings instanceof MovementEffectSettings) {
				MovementEffectSettings mEffectSettings = (MovementEffectSettings) effectSettings;
				range += mEffectSettings.getEstimatedDistance();
			}
		}
		return range + hitBox.width;
	}

	@Override
	public float getWindUpTime() {
		return this.attackSettings.windUpTime;
	}
	
	@Override 
	public float getWindUpPlusActionTime() {
		return this.attackSettings.windUpTime + this.attackSettings.duration;
	}

	
	public boolean isFinished() {
		return currentTime > this.attackSettings.windUpTime + this.attackSettings.duration;
	}
	
	public Rectangle getAttackHitBox() {
		return new Rectangle(attackSettings.originX, attackSettings.originY, attackSettings.width, attackSettings.height);
	}
	
	public Rectangle getHitBox() {
		return hitBox;
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
