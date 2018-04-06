package com.mygdx.game.model.effects;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.conditions.PassiveCondition;
import com.mygdx.game.model.conditions.PassiveConditionSettings;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.conditions.ConditionInitializer;

public abstract class EntityEffect extends Effect {

	Array <PassiveCondition> passiveConditions;
	float staggerTime;
	boolean staggered;
	EntityEffectSettings eSettings;
	boolean checkedConditions;
	
	public static final int LowPriority = 3;
	public static final int MediumPriority = 5;
	public static final int HighPriority = 7;
	public static final int UltraPriority = 9;
	
	public EntityEffect(EffectSettings settings, EffectController controller) {
		super(settings);
		this.setController(controller);
		this.passiveConditions = new Array <PassiveCondition>();
		for (PassiveConditionSettings conditionSettings : this.settings.getPassiveConditions()) {
			PassiveCondition condition = ConditionInitializer.initializePassiveCondition(conditionSettings);
			this.passiveConditions.add(condition);
		}
		staggerTime = 0f;
		staggered = false;
		if (settings instanceof EntityEffectSettings) {
			this.eSettings = (EntityEffectSettings) settings;
		}
		this.checkedConditions = false;
	}
	
	public abstract boolean shouldReciprocateToSource(EntityModel target, ActionListener listener);
	public abstract void flipValues();
	public abstract void flipValuesIfNecessary(EntityModel target, EntityModel source);
	public abstract boolean shouldAddIfIntercepted();
	public abstract boolean isUniqueEffect();
	
	public boolean shouldUseFor(EntityModel target) {
		return true;
	}
	
	public int getPriority() {
		return EntityEffect.LowPriority;
	}
	
	public static Array <Integer> getPriorities() {
		Array <Integer> priorities = new Array <Integer> ();
		priorities.add(EntityEffect.UltraPriority);
		priorities.add(EntityEffect.HighPriority);
		priorities.add(EntityEffect.MediumPriority);
		priorities.add(EntityEffect.LowPriority);
		return priorities;
	}
	
	protected void initialProcess(EntityModel target) {
		super.initialProcess();
	}
	
	protected void processDuringActive(EntityModel target, float delta) {
		super.processDuringActive(delta);
	}
	
	protected void completion(EntityModel target) {
		super.completion();
	}
	
	public boolean process(EntityModel target, float delta) {
		if (this.staggered) {
			this.staggerTime += delta;
			if (staggerTime > EntityUIModel.standardStaggerDuration) {
				this.staggered = false;
				this.staggerTime = 0f;
			}
			return false;
		}
		boolean isFinished = false;
		if (!this.shouldUseFor(target)) {
			isFinished = true;
			return isFinished;
		}
		if (!this.checkedConditions) {
			boolean conditionsMet = true;
			for (PassiveCondition condition : this.passiveConditions) {
				conditionsMet = conditionsMet && condition.isConditionMet(target);
				if (!conditionsMet) {
					this.setForceEnd(true);
					isFinished = true;
					return isFinished;
				}
			}
			this.checkedConditions = true;
		}

		if ((getCurrentTime() >= settings.getDelayToActivate() || settings.isInstantaneous()) && !this.hasProcessedInitial()) {
			this.initialProcess(target);
		}
		if ((getCurrentTime() > settings.getDelayToActivate() && getCurrentTime() < settings.getDuration() + settings.getDelayToActivate()) || settings.isInstantaneous()) {
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
