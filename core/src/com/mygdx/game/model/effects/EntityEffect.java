package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.conditions.PassiveCondition;
import com.mygdx.game.model.conditions.PassiveConditionSettings;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.conditions.ConditionInitializer;

public abstract class EntityEffect extends Effect {

	Array <PassiveCondition> passiveConditions;
	float staggerTime;
	boolean staggered;
	EntityEffectSettings eSettings;
	
	public static final int LowPriority = 3;
	public static final int MediumPriority = 5;
	public static final int HighPriority = 7;
	
	public EntityEffect(EffectSettings settings, EffectController controller) {
		super(settings);
		this.setController(controller);
		this.passiveConditions = new Array <PassiveCondition>();
		for (PassiveConditionSettings conditionSettings : this.settings.getPassiveConditions()) {
			PassiveCondition condition = ConditionInitializer.initializePassiveCondition(this.getController().getSource(), conditionSettings);
			this.passiveConditions.add(condition);
		}
		staggerTime = 0f;
		staggered = false;
		if (settings instanceof EntityEffectSettings) {
			this.eSettings = (EntityEffectSettings) settings;
		}
	}
	
	public abstract boolean shouldReciprocateToSource(CharacterModel target, ActionListener listener);
	public abstract void flipValues();
	public abstract void flipValuesIfNecessary(CharacterModel target, CharacterModel source);
	public abstract boolean shouldAddIfIntercepted();
	public abstract boolean isUniqueEffect();
	
	public int getPriority() {
		return EntityEffect.LowPriority;
	}
	
	public static Array <Integer> getPriorities() {
		Array <Integer> priorities = new Array <Integer> ();
		priorities.add(EntityEffect.LowPriority);
		priorities.add(EntityEffect.MediumPriority);
		priorities.add(EntityEffect.HighPriority);
		return priorities;
	}
	
	protected void initialProcess(CharacterModel target) {
		super.initialProcess();
	}
	
	protected void processDuringActive(CharacterModel target, float delta) {
		super.processDuringActive(delta);
	}
	
	protected void completion(CharacterModel target) {
		super.completion();
	}
	
	public boolean process(CharacterModel target, float delta) {
		if (this.staggered) {
			this.staggerTime += delta;
			if (staggerTime > EntityUIModel.standardStaggerDuration) {
				this.staggered = false;
				this.staggerTime = 0f;
			}
			return false;
		}
		boolean isFinished = false;
		boolean conditionsMet = true;
		for (PassiveCondition condition : this.passiveConditions) {
			conditionsMet = conditionsMet && condition.isConditionMet();
			if (!conditionsMet && this.isActive && condition.endIfNotMet()) {
				this.setForceEnd(true);
			}
		}
		if ((getCurrentTime() >= settings.getDelayToActivate() || settings.isInstantaneous()) && !this.hasProcessedInitial()) {
			this.initialProcess(target);
		}
		if ((getCurrentTime() > settings.getDelayToActivate() && getCurrentTime() < settings.getDuration() + settings.getDelayToActivate()) || settings.isInstantaneous() && conditionsMet) {
			this.processDuringActive(target, delta);
		}
		if ((getCurrentTime() >= (settings.getDuration() + settings.getDelayToActivate()) || settings.isInstantaneous() || this.isForceEnd()) && !this.hasProcessedCompletion()) {
			isFinished = true;
			this.completion(target);
		}
		setCurrentTime(getCurrentTime() + delta);
		return isFinished;
	}
	
	public float remainingDuration() {
		if (getCurrentTime() <= this.settings.getDelayToActivate()) {
			return this.settings.getDuration();
		}
		else {
			return this.settings.getDuration() - (this.getCurrentTime() - this.settings.getDelayToActivate());
		}
	}
	
	public float getActiveTime() {
		return this.getCurrentTime() - this.settings.getDelayToActivate();
	}

	public void setForceEnd(boolean forceEnd) {
		this.forceEnd = forceEnd;
	}

	public void stagger() {
		this.staggered = true;
	}
	
	public Integer getSpecificID() {
		return this.eSettings.getSpecificID();
	}
	
}
