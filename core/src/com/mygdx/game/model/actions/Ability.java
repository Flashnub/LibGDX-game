package com.mygdx.game.model.actions;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EntityEffect;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.XMovementEffectSettings;
import com.mygdx.game.model.effects.YMovementEffectSettings;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.events.ActionListener;

public class Ability extends ActionSegment{
	AbilitySettings settings;
	Array <EntityEffect> activeSourceEffects;
	Array <EntityEffect> windupSourceEffects;

	
	public Ability(CharacterModel source, AbilitySettings settings, ActionListener listener) {
		super(listener);
		this.settings = settings.deepCopy();
		this.source = source;
		this.activeSourceEffects = new Array <EntityEffect>();
		this.windupSourceEffects = new Array <EntityEffect>();
		this.setDurations(source);
	}
	
	public Ability(CharacterModel source, AbilitySettings settings, ActionListener listener, XMovementEffectSettings xReplacementMovement) {
		this(source, settings, listener);
		if (xReplacementMovement != null) {
			this.settings.replaceXMovementIfNecessary(xReplacementMovement);
		}
	}
	
	@Override
	public void sendActionToListener(ActionListener actionListener, float delta) {
		// Do nothing.
		
	}
	
	@Override
	public void sourceCompletionWithoutSuper(CharacterModel source) {
		source.setWidthCoefficient(source.getCharacterProperties().getWidthCoefficient());
		source.setHeightCoefficient(source.getCharacterProperties().getHeightCoefficient());
//		source.setxOffsetModifier(0f);
//		source.setyOffsetModifier(0f);
//		if (!this.shouldRespectEntityCollisions())
//			source.setRespectEntityCollision(true);
		source.unlockEntityCollisionBehavior();
	}

	
	public void sourceActiveProcessWithoutSuper(CharacterModel source) {
		if (this.settings.tempWidthModifier != null) {
			source.setWidthCoefficient(this.settings.tempWidthModifier.floatValue());
		}
		if (this.settings.tempHeightModifier != null) {
			source.setHeightCoefficient(this.settings.tempHeightModifier.floatValue());
		}
		if (this.settings.xOffsetModifier != null) {
//			source.setxOffsetModifier(this.settings.xOffsetModifier.floatValue());
		}
		if (this.settings.yOffsetModifier != null) {
//			source.setyOffsetModifier(this.settings.yOffsetModifier.floatValue());
		}
		if (!this.shouldRespectEntityCollisions()) {
			source.setRespectingEntityCollision(false);
			source.lockEntityCollisionBehavior();
		}
		
		for (EffectSettings effectSettings : settings.sourceEffectSettings) {
			EntityEffect effect = EffectInitializer.initializeEntityEffect(effectSettings, this);
			effect.flipValuesIfNecessary(null, source);
			source.addEffect(effect);
			activeSourceEffects.add(effect);
		}

	}
	
	public void sourceWindupProcessWithoutSuper(CharacterModel source) {
		for (EffectSettings effectSettings : settings.windupEffectSettings) {
			EntityEffect effect = EffectInitializer.initializeEntityEffect(effectSettings, this);
			effect.flipValuesIfNecessary(null, source);
			source.addEffect(effect);
			windupSourceEffects.add(effect);
		}
	}

	@Override
	public float getWindUpTime() {
		return this.forceActiveState ? this.processedWindupTime : this.windupTime;
	}
	
	@Override 
	public float getWindUpPlusActionTime() {
		return getWindUpTime() + (this.forceCooldownState ? this.processedActiveTime : this.activeTime);
	}
	
	@Override
	public float getTotalTime() {
//		if (this.forceCooldownState) {
//			return getWindUpTime() + this.activeTime + this.settings.cooldownTime;
//		}
		return getWindUpPlusActionTime() + this.cooldownTime;
	}

//	@Override
//	public float getEffectiveRange() {
//		return Float.MAX_VALUE;
//	}

	@Override
	public ActionSegment cloneActionSegmentWithSourceAndTarget(CharacterModel source, CharacterModel target) {
		Ability ability = new Ability (source, settings, this.actionListener);
		return ability;
	}



	@Override
	public void interruptionBlock() {
		for(EntityEffect effect : this.activeSourceEffects) {
			effect.setForceEnd(true);
		}
		for (EntityEffect effect : this.windupSourceEffects) {
			effect.setForceEnd(true);
		}
	}

	@Override
	public XMovementEffectSettings getXReplacementMovementForStagger() {
		return null;
	}
	
	@Override
	public YMovementEffectSettings getYReplacementMovementForStagger() {
		return null;
	}


	@Override
	public ActionListener getActionListener() {
		return null;
	}

	@Override
	public boolean shouldRespectEntityCollisions() {
		return this.settings.sourceRespectEntityCollisions;
	}

	@Override
	public boolean doesNeedDisruptionDuringWindup() {
		return this.settings.windupTillDisruption;
	}

	@Override
	public boolean doesNeedDisruptionDuringActive() {
		return this.settings.activeTillDisruption;
	}

	@Override
	public void setDurations(CharacterModel source) {
		this.windupTime = this.settings.windupTillDisruption ? Float.MAX_VALUE : source.getUiModel().getTimeForAnimation(this.settings.name, ActionSegment.Windup);
		this.activeTime = this.settings.activeTillDisruption ? Float.MAX_VALUE : source.getUiModel().getTimeForAnimation(this.settings.name, ActionSegment.Active);
		this.cooldownTime = source.getUiModel().getTimeForAnimation(this.settings.name, ActionSegment.Cooldown);
	}

	@Override
	public XMovementEffectSettings getSourceXMove() {
		for(EffectSettings effect : this.settings.sourceEffectSettings) {
			if (effect instanceof XMovementEffectSettings) {
				return (XMovementEffectSettings) effect.deepCopy();
			}
		}
		return null;
	}

	@Override
	public YMovementEffectSettings getSourceYMove() {
		for(EffectSettings effect : this.settings.sourceEffectSettings) {
			if (effect instanceof YMovementEffectSettings) {
				return (YMovementEffectSettings) effect.deepCopy();
			}
		}
		return null;
	}

	@Override
	public boolean chainsWithJump() {
		return settings.chainsWithJump;
	}

	@Override
	public boolean isSuper() {
		return this.settings.isSuper;
	}

	@Override
	public boolean metChainConditions() {
		return true;
	}

	@Override
	public boolean willActionHitTarget(CharacterModel target) {
		return true;
	}

	@Override
	public void updateHurtBoxes() {
		if (this.actionState.equals(ActionState.WINDUP)) {
			source.updateHurtBoxProperties(this.settings.windupHurtBoxProperties);
		}
		else if (this.actionState.equals(ActionState.ACTIVE)) {
			source.updateHurtBoxProperties(this.settings.activeHurtBoxProperties);
		}
		else if (this.actionState.equals(ActionState.COOLDOWN)) {
			source.updateHurtBoxProperties(this.settings.cooldownHurtBoxProperties);
		}
	}

}
