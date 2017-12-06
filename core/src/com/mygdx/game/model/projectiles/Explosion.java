package com.mygdx.game.model.projectiles;

import java.util.UUID;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.effects.EntityEffect;
import com.mygdx.game.model.effects.EffectController;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.XMovementEffectSettings;
import com.mygdx.game.model.effects.YMovementEffectSettings;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.characters.CollisionCheck;
import com.mygdx.game.model.characters.EntityCollisionData;
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
	Array <EntityModel> alreadyHitCharacters;
	String state;
	CharacterModel source;
	 
	
	public Explosion(String name, ActionListener actionListener, CharacterModel source) {
		super();
		this.settings = JSONController.explosions.get(name).deepCopy();
		this.actionListener = actionListener;
		this.currentTime = 0f;
		this.explosionUIModel = new EntityUIModel(name, EntityUIDataType.EXPLOSION);
		this.widthCoefficient = this.settings.getWidthCoefficient();
		this.heightCoefficient = this.settings.getHeightCoefficient();


		UUID id = UUID.randomUUID();
		this.uuid = id.toString();
		this.allegiance = source.getAllegiance();
		this.source = source;
		this.state = EntityModel.windupState;
		
		this.setShouldRespectEntityCollision(false);
		this.setShouldRespectObjectCollision(false);
		this.setShouldRespectTileCollision(false);
		this.setRespectingEntityCollision(false);
		this.setRespectingObjectCollision(false);
		this.setRespectingTileCollision(false);
	}
	
	public void setStartingPosition(Vector2 originOverride, Vector2 originOffset) {
		if (originOverride != null && originOffset != null) {
			this.imageBounds.x = originOverride.x + originOffset.x;
			this.imageBounds.y = originOverride.y + originOffset.y;
		}
		else if (originOffset != null){
			this.imageBounds.x = source.getImageHitBox().x + (source.getImageHitBox().width / 2f) + originOffset.x;
			this.imageBounds.y = source.getImageHitBox().y + (source.getImageHitBox().height / 2f) + originOffset.y;
		}
		else {
			this.imageBounds.x = source.getImageHitBox().x + (source.getImageHitBox().width / 2f);
			this.imageBounds.y = source.getImageHitBox().y + (source.getImageHitBox().height / 2f);
		}
		
		this.gameplayCollisionBox.setX(this.getXOffsetModifier() + this.imageBounds.getX() + this.imageBounds.getWidth() * ((1f - this.widthCoefficient) / 2));
		this.gameplayCollisionBox.setY(this.getyOffsetModifier() + this.imageBounds.getY() + this.imageBounds.getHeight() * ((1f - this.heightCoefficient) / 2));
	}
	
	public void update(float delta, TiledMapTileLayer collisionLayer) {
		currentTime += delta;
		this.changeStateCheck();
		float oldWidth = 0f;
		oldWidth = this.imageBounds.width;
		explosionUIModel.setCurrentFrame(this, delta);
		this.handleCollisionRespectChecks();
		this.movementWithCollisionDetection(delta, collisionLayer); //should use movementWithCollision anyway?
		this.setGameplayCollisionSize(delta);
		this.expansionCheck(oldWidth);
		this.actionListener.processExplosion(this);
		this.deletionCheck();
	}

	@Override
	public EntityCollisionData handleEntityXCollisionLogic(Rectangle tempGameplayBounds, Rectangle tempImageBounds, boolean alreadyCollided) {
		return null;
	}

	@Override
	public EntityCollisionData handleEntityYCollisionLogic(Rectangle tempGameplayBounds, Rectangle tempImageBounds, boolean alreadyCollided) {
		return null;
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
		float widthDifference = this.imageBounds.width - oldWidth;
		if (widthDifference > 0) {
			this.gameplayCollisionBox.x = this.gameplayCollisionBox.x - ((widthDifference * this.widthCoefficient) / 2);
			this.imageBounds.x = this.imageBounds.x - widthDifference / 2;
		}

	}
	
	private void deletionCheck() {
		if (this.currentTime > this.settings.getTotalTime()) {
			this.actionListener.deleteExplosion(this);
		}
	}
	
	protected void movementWithCollisionDetection(float delta, TiledMapTileLayer collisionLayer) {
		//logic for collision detection
		CollisionCheck collisionX = this.checkForXCollision(delta, collisionLayer, this.velocity.x, this.acceleration.x, true);
		if (collisionX.doesCollide()) {

		}
		CollisionCheck collisionY = this.checkForYCollision(delta, collisionLayer, this.velocity.y, true, !this.state.equals(EntityModel.cooldownState));
		if (collisionY.doesCollide()) {

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

	public Array<EntityModel> getAlreadyHitCharacters() {
		return alreadyHitCharacters;
	}

	@Override
	public XMovementEffectSettings getXReplacementMovementForStagger() {
		XMovementEffectSettings mSettings = null;
		for (EffectSettings settings : this.settings.targetEffects) {
			if (settings instanceof XMovementEffectSettings) {
				mSettings = (XMovementEffectSettings) settings;
			}
		}
		return mSettings;
	}
	
	@Override
	public YMovementEffectSettings getYReplacementMovementForStagger() {
		YMovementEffectSettings mSettings = null;
		for (EffectSettings settings : this.settings.targetEffects) {
			if (settings instanceof YMovementEffectSettings) {
				mSettings = (YMovementEffectSettings) settings;
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
	
	public boolean checkSlopes() {
		return false;
	}
}
