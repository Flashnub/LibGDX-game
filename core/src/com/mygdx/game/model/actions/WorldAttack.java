package com.mygdx.game.model.actions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.actions.ActionSegment.ActionState;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.EntityEffect;
import com.mygdx.game.model.effects.XMovementEffectSettings;
import com.mygdx.game.model.effects.YMovementEffectSettings;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.CollisionChecker;
import com.mygdx.game.model.globalEffects.DeleteCharacterEffect;
import com.mygdx.game.model.globalEffects.WorldEffect;
import com.mygdx.game.model.globalEffects.WorldEffectSettings;


public class WorldAttack extends ActionSegment{
	
	Array<WorldEffect> worldEffects;
	ActionSegment potentialAbility;
	CharacterModel target;
	CollisionChecker collisionChecker;
	WorldAttackSettings worldAttackSettings;
	Array <EntityEffect> sourceEffects;
	
	public WorldAttack() {
		super();
	}
	
	public WorldAttack(CharacterModel source, CharacterModel target, ActionListener actionListener, CollisionChecker collisionChecker, WorldAttackSettings settings, String overridingAbilitySettingsKey) {
		super(actionListener);
		this.source = source;
		this.target = target;
		worldEffects = new Array<WorldEffect>();
		this.worldAttackSettings = settings;
		for (WorldEffectSettings worldEffectSettings : this.worldAttackSettings.worldEffectSettings) {
			WorldEffect effect = EffectInitializer.initializeWorldEffect(worldEffectSettings, this, collisionChecker, source, target, null);
			worldEffects.add(effect);
		}
		if (settings.getAbilitySettings() != null || overridingAbilitySettingsKey != null) {
			if (overridingAbilitySettingsKey != null) {
				settings.abilitySettingKey = overridingAbilitySettingsKey;
				settings.abilitySettings = JSONController.abilities.get(settings.abilitySettingKey).deepCopy();
			}
			if (settings.getAbilitySettings() instanceof AbilitySettings) {
				potentialAbility = new Ability(source, settings.getAbilitySettings(), actionListener);
			}
			else if (settings.getAbilitySettings() instanceof AttackSettings) {
				AttackSettings atkSettings = (AttackSettings) settings.getAbilitySettings();
				Attack attack = new Attack(source, atkSettings, actionListener, this.collisionChecker);
				potentialAbility = attack;
			}
		}
		this.sourceEffects = new Array <EntityEffect>();
		this.setDurations(source);
	}
	
//	@Override
//	public float getEffectiveRange() {
//		float range = 0f;
//		for (WorldEffect worldEffect : worldEffects) {
//			range += worldEffect.getEffectiveRange();
//		}
//		return range;
//	}
//	
//	@Override 
//	public float getWindUpPlusActionTime() {
//		return this.worldAttackSettings.getAbilitySettings().windupTime + this.worldAttackSettings.getAbilitySettings().duration;
//	}
//	
//	@Override
//	public float getWindUpTime() {
//		return this.worldAttackSettings.getAbilitySettings().windupTime;
//	}
//	
//	@Override
//	public float getTotalTime() {
//		if (this.forceCooldownState) {
//			return this.worldAttackSettings.getAbilitySettings().windupTime + this.activeTime + this.worldAttackSettings.getAbilitySettings().cooldownTime;
//		}
//		return this.worldAttackSettings.getAbilitySettings().windupTime + this.worldAttackSettings.getAbilitySettings().cooldownTime + this.worldAttackSettings.getAbilitySettings().duration;
//	}
//	
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

	@Override
	public void sendActionToListener(ActionListener actionListener, float delta) {
		if (this.potentialAbility != null) {
			this.potentialAbility.sendActionToListener(actionListener, delta);
		}
	}
	
	public void sourceActiveProcessWithoutSuper(CharacterModel source) {
		if (this.potentialAbility != null) {
			this.potentialAbility.sourceActiveProcessWithoutSuper(source);
		}
		for (WorldEffect worldEffect : worldEffects) {
			this.actionListener.addWorldEffect(worldEffect);
		}
	}
	
	@Override
	public void sourceWindupProcessWithoutSuper(CharacterModel source) {
		if (this.potentialAbility != null) {
			this.potentialAbility.sourceWindupProcessWithoutSuper(source);
		}		
	}
	
	@Override
	public void sourceCompletionWithoutSuper(CharacterModel source) {
		if (this.potentialAbility != null) {
			this.potentialAbility.sourceCompletionWithoutSuper(source);
		}				
	}

	@Override
	public ActionSegment cloneActionSegmentWithSourceAndTarget(CharacterModel source, CharacterModel target) {
		WorldAttack worldAttack = new WorldAttack();
		worldAttack.source = source;
		worldAttack.target = target;
		worldAttack.actionListener = actionListener;
		worldAttack.collisionChecker = collisionChecker;
		worldAttack.worldAttackSettings = worldAttackSettings;
		Array <WorldEffect> copy = new Array <WorldEffect>();
		for (WorldEffectSettings worldEffectSettings : this.worldAttackSettings.worldEffectSettings) {
//			Vector2 sourcePosition = new Vector2(this.source.gameplayHitBox.x + this.source.gameplayHitBox.width / 2, this.source.gameplayHitBox.y + this.source.gameplayHitBox.height / 2); 
			WorldEffect effect = EffectInitializer.initializeWorldEffect(worldEffectSettings, this, collisionChecker, source, target, null);
			worldEffects.add(effect);
		}
		worldAttack.worldEffects = copy;
		if (worldAttackSettings.getAbilitySettings() != null) {
			if (worldAttackSettings.getAbilitySettings() instanceof AbilitySettings) {
				worldAttack.potentialAbility = new Ability(source, worldAttackSettings.getAbilitySettings(), this.actionListener);
			}
			else if (worldAttackSettings.getAbilitySettings() instanceof AttackSettings) {
				AttackSettings atkSettings = (AttackSettings) worldAttackSettings.getAbilitySettings();
				Attack attack = new Attack(source, atkSettings, this.actionListener, this.collisionChecker);
				worldAttack.potentialAbility = attack;
			}
		}
		return worldAttack;
	}

	@Override
	public void interruptionBlock() {
		for(EntityEffect effect : this.sourceEffects) {
			effect.setForceEnd(true);
		}
	}
	
	@Override
	public XMovementEffectSettings getXReplacementMovementForStagger() {
		return potentialAbility.getXReplacementMovementForStagger();
	}
	
	@Override
	public YMovementEffectSettings getYReplacementMovementForStagger() {
		return potentialAbility.getYReplacementMovementForStagger();
	}


	@Override
	public boolean shouldRespectEntityCollisions() {
		return potentialAbility.shouldRespectEntityCollisions();
	}

	@Override
	public boolean doesNeedDisruptionDuringWindup() {
		return this.worldAttackSettings.getAbilitySettings().windupTillDisruption;
	}

	@Override
	public boolean doesNeedDisruptionDuringActive() {
		return this.worldAttackSettings.getAbilitySettings().activeTillDisruption;
	}

	@Override
	public void setDurations(CharacterModel source) {
		this.windupTime = this.worldAttackSettings.getAbilitySettings().windupTillDisruption ? Float.MAX_VALUE : source.getUiModel().getTimeForAnimation(this.worldAttackSettings.getAbilitySettings().name, ActionSegment.Windup);
		this.activeTime = this.worldAttackSettings.getAbilitySettings().activeTillDisruption ? Float.MAX_VALUE : source.getUiModel().getTimeForAnimation(this.worldAttackSettings.getAbilitySettings().name, ActionSegment.Active);
		this.cooldownTime = source.getUiModel().getTimeForAnimation(this.worldAttackSettings.getAbilitySettings().name, ActionSegment.Cooldown);
	}
	
	@Override
	public XMovementEffectSettings getSourceXMove() {
		for(EffectSettings effect : this.worldAttackSettings.getAbilitySettings().sourceEffectSettings) {
			if (effect instanceof XMovementEffectSettings) {
				return (XMovementEffectSettings) effect.deepCopy();
			}
		}
		return null;
	}

	@Override
	public YMovementEffectSettings getSourceYMove() {
		for(EffectSettings effect : this.worldAttackSettings.getAbilitySettings().sourceEffectSettings) {
			if (effect instanceof YMovementEffectSettings) {
				return (YMovementEffectSettings) effect.deepCopy();
			}
		}
		return null;
	}
	
	@Override
	public boolean chainsWithJump() {
		return worldAttackSettings.getAbilitySettings().chainsWithJump;
	}
	
	@Override
	public boolean isSuper() {
		return this.worldAttackSettings.getAbilitySettings().isSuper;
	}

	@Override
	public boolean metChainConditions() {
		return this.potentialAbility.metChainConditions();
	}

	@Override
	public boolean willActionHitTarget(CharacterModel target) {
		return true;
	}

	@Override
	public void updateHurtBoxes() {
		if (this.actionState.equals(ActionState.WINDUP)) {
			source.updateHurtBoxProperties(this.worldAttackSettings.abilitySettings.windupHurtBoxProperties);
		}
		else if (this.actionState.equals(ActionState.ACTIVE)) {
			source.updateHurtBoxProperties(this.worldAttackSettings.abilitySettings.activeHurtBoxProperties);
		}
		else if (this.actionState.equals(ActionState.COOLDOWN)) {
			source.updateHurtBoxProperties(this.worldAttackSettings.abilitySettings.cooldownHurtBoxProperties);
		}
	}
}
