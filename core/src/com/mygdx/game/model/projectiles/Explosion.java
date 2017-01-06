package com.mygdx.game.model.projectiles;

import java.util.UUID;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EntityEffect;
import com.mygdx.game.model.effects.EffectController;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.MovementEffectSettings;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.characters.EntityUIDataType;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.events.ActionListener;

public class Explosion extends EntityModel implements EffectController {
	
	ExplosionSettings settings;
	float currentTime;
	int allegiance;
	ActionListener actionListener;
	EntityUIModel explosionUIModel;
	String uuid;
	Array <CharacterModel> alreadyHitCharacters;
	String state;
	CharacterModel source;
	 
	
	public Explosion(String name, ActionListener actionListener, Vector2 originOffset, CharacterModel source, Vector2 originOverride) {
		this.settings = JSONController.explosions.get(name).deepCopy();
		this.actionListener = actionListener;
		this.currentTime = 0f;
		this.explosionUIModel = new EntityUIModel(name, EntityUIDataType.EXPLOSION);
		this.widthCoefficient = this.settings.getWidthCoefficient();
		this.heightCoefficient = this.settings.getHeightCoefficient();
		if (originOverride != null) {
			this.imageHitBox.x = originOverride.x + originOffset.x;
			this.imageHitBox.y = originOverride.y + originOffset.y;
		}
		UUID id = UUID.randomUUID();
		this.uuid = id.toString();
		this.allegiance = source.getAllegiance();
		this.source = source;
		this.state = EntityModel.windupState;
	}
	
	public void update(float delta) {
		currentTime += delta;
		this.changeStateCheck();
		float oldWidth = 0f;
		oldWidth = this.imageHitBox.width;
		explosionUIModel.setCurrentFrame(this, delta);
		this.handleOverlapCooldown(delta);
		this.moveWithoutCollisionDetection(delta);
		this.setGameplaySize(delta);
		this.expansionCheck(oldWidth);
		this.actionListener.processExplosion(this);
		this.deletionCheck();
	}

	@Override
	public boolean handleAdditionalXCollisionLogic(Rectangle tempGameplayBounds, Rectangle tempImageBounds, boolean alreadyCollided) {
		return false;
	}

	@Override
	public boolean handleAdditionalYCollisionLogic(Rectangle tempGameplayBounds, Rectangle tempImageBounds, boolean alreadyCollided) {
		return false;
	}


	@Override
	public int getAllegiance() {
		return this.allegiance;
	}
	
	public void processExplosionHit(CharacterModel target) {
		if (target != null) {
			for (EffectSettings effectSettings : settings.getTargetEffects()) {
				EntityEffect effect = EffectInitializer.initializeEntityEffect(effectSettings, this);
				target.addEffect(effect);
			}
			target.setImmuneToInjury(true);
		}
	}
	
	
	private void changeStateCheck() {
		if (((this.currentTime <= this.settings.getTotalTime() && this.currentTime > this.settings.getWindUpPlusDuration()))) {
			this.setState(EntityModel.cooldownState);
		}
		else if (this.currentTime > this.settings.getWindUpTime() && this.currentTime <= this.settings.getWindUpPlusDuration()) {
			this.setState(EntityModel.activeState);
		}
		else if (this.currentTime <= this.settings.getWindUpTime()) {
			this.setState(EntityModel.windupState);
		}		
	}
	
	public void expansionCheck(float oldWidth) {
		//Origin needs to move backwards to account for leftward expansion.
		float widthDifference = this.imageHitBox.width - oldWidth;
		if (widthDifference > 0) {
			this.gameplayHitBox.x = this.gameplayHitBox.x - ((widthDifference * this.widthCoefficient) / 2);
			this.imageHitBox.x = this.imageHitBox.x - widthDifference / 2;
		}

	}
	
	private void deletionCheck() {
		if (this.currentTime > this.settings.getTotalTime()) {
			this.actionListener.deleteExplosion(this);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Explosion) {
			return ((Explosion) obj).uuid.equals(this.uuid); 
		}
		return super.equals(obj);
	}

	public ExplosionSettings getExplosionSettings() {
		return settings;
	}

	public Array<CharacterModel> getAlreadyHitCharacters() {
		return alreadyHitCharacters;
	}

	@Override
	public MovementEffectSettings getReplacementMovementForStagger() {
		MovementEffectSettings mSettings = null;
		for (EffectSettings settings : this.settings.targetEffects) {
			if (settings instanceof MovementEffectSettings) {
				mSettings = (MovementEffectSettings) settings;
			}
		}
		return mSettings;
	}
	
	@Override
	public boolean isFacingLeft() {
		return true;
	}

	@Override
	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}

	public EntityUIModel getExplosionUIModel() {
		return explosionUIModel;
	}

	@Override
	public ActionListener getActionListener() {
		return this.actionListener;
	}
	
	@Override
	public Vector2 spawnOriginForChar() {
		if (this.isFacingLeft()) {
			return new Vector2(this.getImageHitBox().x + this.getImageHitBox().width + 100, this.getImageHitBox().y);
		}
		return new Vector2(this.getImageHitBox().x - 200, this.getImageHitBox().y);
	}

	@Override
	public CharacterModel getSource() {
		return this.source;
	}
}
