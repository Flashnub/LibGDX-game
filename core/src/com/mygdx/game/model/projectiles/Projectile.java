package com.mygdx.game.model.projectiles;

import java.util.UUID;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.actions.HitTracker;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.CollisionCheck;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.characters.EntityUIDataType;
import com.mygdx.game.model.effects.EntityEffect;
import com.mygdx.game.model.effects.EffectController;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.MovementEffectSettings;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.CollisionChecker;
import com.mygdx.game.model.globalEffects.WorldEffect;
import com.mygdx.game.model.globalEffects.WorldEffectSettings;
import com.mygdx.game.utils.MathUtils;

public class Projectile extends EntityModel implements EffectController{
	
	private float currentTime;
	private float activeTime;
	private EntityUIModel projectileUIModel;
	private String uuid;
	private ActionListener actionListener;
	private CharacterModel source;
	private CharacterModel target;
	private ProjectileSettings settings;
	private String state;
	private boolean didChangeState;
	private boolean forceCooldown;
	private boolean forceEnd;
	private Array <HitTracker> charactersHit;

	
	public Projectile(String name, Vector2 originOffset, CharacterModel source, CharacterModel target, ActionListener actionListener, CollisionChecker collisionChecker, Vector2 originOverride)
	{
		super();
		this.state = EntityModel.windupState;
		this.forceCooldown = false;
		this.forceEnd = false;
		this.didChangeState = true;
		this.currentTime = 0f;
		this.activeTime = 0f;
		this.settings = JSONController.projectiles.get(name).deepCopy();
		this.widthCoefficient = this.settings.getWidthCoefficient();
		this.heightCoefficient = this.settings.getHeightCoefficient();
		this.acceleration.y = -this.settings.getGravity();
		if (originOverride != null) {
			this.imageHitBox.x = originOverride.x + originOffset.x;
			this.imageHitBox.y = originOverride.y + originOffset.y;
		}
		else {
			this.imageHitBox.x = source.getImageHitBox().x + (source.getImageHitBox().width / 2f) + originOffset.x;
			this.imageHitBox.y = source.getImageHitBox().y + (source.getImageHitBox().height / 2f) + originOffset.y;
		}

		this.gameplayHitBox = new Rectangle(this.imageHitBox);
		this.projectileUIModel = new EntityUIModel(name, EntityUIDataType.PROJECTILE);
		this.actionListener = actionListener;
		this.setCollisionChecker(collisionChecker);
		this.source = source;
		this.target = target;
		this.charactersHit = new Array <HitTracker>();
		UUID id = UUID.randomUUID();
		this.uuid = id.toString();
		this.allegiance = source.getAllegiance();
	}

	public void update(float delta, TiledMapTileLayer collisionLayer) {
		currentTime += delta;
		this.changeStateCheck();
		if (this.state.equals(EntityModel.activeState)) {
			activeTime += delta;
		}
		if (this.didChangeState || this.settings.isTracks()) {
			this.didChangeState = false;
			this.determineAndSetVelocity(target, collisionLayer);
			System.out.println(this.velocity);
		}
//		this.velocity.y -= this.settings.getGravity() * delta;
		this.setGameplaySize(delta);
		if (this.settings.isHasCollisionDetection()) {
			this.movementWithCollisionDetection(delta, collisionLayer);
		}
		else {
			moveWithoutCollisionDetection(delta);
		}
		projectileUIModel.setCurrentFrame(this, delta, this.getVelocityAngle());
		actionListener.processProjectile(this);
		this.deletionCheck();
	}

	//Taken from http://stackoverflow.com/questions/2248876/2d-game-fire-at-a-moving-target-by-predicting-intersection-of-projectile-and-u
	//Use this for linear traveling projectiles with a reasonably fast projectile speed. 
	//This shouldn't be used for slow moving or pattern-based projectiles 
	public Vector2 findInterceptPointWith(CharacterModel target, TiledMapTileLayer collisionLayer) {
		Vector2 targetPosition = new Vector2(target.getGameplayHitBox().x + target.getGameplayHitBox().width / 2, target.getGameplayHitBox().y + target.getGameplayHitBox().height / 2); 
		Vector2 targetVelocity = new Vector2(target.getVelocity().x, target.getVelocity().y);
		Vector2 targetAcceleration = target.getAcceleration();
		
		Vector2 projectilePosition = new Vector2(imageHitBox.x + (imageHitBox.width / 2), imageHitBox.y + (imageHitBox.height / 2));
		
		float xPositionDifference = targetPosition.x - projectilePosition.x;
		float yPositionDifference = targetPosition.y - projectilePosition.y;
		float targetVelocityX = targetVelocity.x;
		float targetVelocityY = targetVelocity.y;
		float quadraticA = (targetVelocityX * targetVelocityX) + (targetVelocityY * targetVelocityY) - (this.projectileSpeed() * this.projectileSpeed());
		float quadraticB = 2 * ((targetVelocityX * xPositionDifference) + (targetVelocityY * yPositionDifference));
		float quadraticC = (xPositionDifference * xPositionDifference) + (yPositionDifference * yPositionDifference);
		
		Vector2 solvedQuadratic = MathUtils.solveQuadratic(quadraticA, quadraticB, quadraticC);
		
		Vector2 solution = new Vector2();
		if (solvedQuadratic != null) {
			float t0 = solvedQuadratic.x;
			float t1 = solvedQuadratic.y;
			
			float t = Math.min(t0, t1);
			
			if (t < 0) {
				t = Math.max(t0, t1);
			}
			if (t > 0) {
				//YPosition should never be negative.
				float xTimeTillCollision = target.howLongTillXCollision(t, collisionLayer);
				float xTimeTillAccelStops = Float.MAX_VALUE;
				if (target.getCurrentMovement() != null) {
					xTimeTillAccelStops = target.getCurrentMovement().remainingDuration();
				}
				float xTime = Math.min(xTimeTillCollision, xTimeTillAccelStops);
				float yTimeTillCollision = target.howLongTillYCollision(t, collisionLayer);
//				System.out.println(xTime);
//				System.out.println(yTimeTillCollision);
				solution = new Vector2((targetPosition.x) +  ((targetVelocityX * xTime) + (.5f * targetAcceleration.x * xTime * xTime)),
										(targetPosition.y) + ((targetVelocityY * yTimeTillCollision) + (.5f * targetAcceleration.y * yTimeTillCollision * yTimeTillCollision)));
			}
			else {
				solution = new Vector2(targetPosition.x, targetPosition.y);
			}
		}
		
		return solution;
	}
	
	public void determineAndSetVelocity(CharacterModel target, TiledMapTileLayer collisionLayer) {
		if (this.settings.getAngleOfVelocity() != null) {
			boolean isTargetToLeft = source.isTargetToLeft(target);
			float angleOfVelocity = isTargetToLeft ? 180f - this.settings.getAngleOfVelocity() : this.settings.getAngleOfVelocity();
			float angleInRadians = (float) Math.toRadians(angleOfVelocity);
			float xVelocity = (float) (this.projectileSpeed() * Math.cos(angleInRadians));
			float yVelocity = (float) (this.projectileSpeed() * Math.sin(angleInRadians));
			
			this.velocity.x = xVelocity;
			this.velocity.y = yVelocity;
		}
		else {
			Vector2 expectedPositionOfTarget;

			if (this.settings.isUseSmartAim() && !this.settings.isTracks()) {
				expectedPositionOfTarget = this.findInterceptPointWith(target, collisionLayer);
			}
			else {
				expectedPositionOfTarget = new Vector2(target.getGameplayHitBox().x + target.getGameplayHitBox().width / 2, target.getGameplayHitBox().y + target.getGameplayHitBox().height / 2);
			}
			Vector2 projectilePosition = new Vector2(imageHitBox.x + (imageHitBox.width / 2), imageHitBox.y + (imageHitBox.height / 2));
			
			float xDelta = (expectedPositionOfTarget.x - projectilePosition.x);
			if (xDelta >= 0) {
				xDelta = Math.max(0.01f, xDelta);
			}
			else {
				xDelta = Math.min(-0.01f, xDelta);
			}
			
			float yDelta = expectedPositionOfTarget.y - projectilePosition.y;
			if (yDelta >= 0) {
				yDelta = Math.max(0.01f, yDelta);
			}
			else {
				yDelta = Math.min(-0.01f, yDelta);
			}
			
			float fullDelta = (float) Math.sqrt((xDelta * xDelta) + (yDelta * yDelta));
			
			float time = yDelta / ((yDelta / fullDelta) * this.projectileSpeed());
			this.velocity.x = (xDelta / fullDelta) * this.projectileSpeed();
			if (this.projectileSpeed() == 0) {
				this.velocity.y = 0;
			}
			else {
				this.velocity.y = ((yDelta / fullDelta) * this.projectileSpeed()) + (.5f * this.settings.getGravity() * time);

			}
		}
	}
	
	protected void movementWithCollisionDetection(float delta, TiledMapTileLayer collisionLayer) {
		//logic for collision detection
		CollisionCheck collisionX = this.checkForXCollision(delta, collisionLayer, this.velocity.x, true);
		if (collisionX.isDoesCollide()) {
			if (this.settings.isBounces()) {
				this.getVelocity().x = -this.getVelocity().x;
			}
//			else if (this.settings.isDisappearOnImpact()){
//				forceCooldown = true;
//				this.actionListener.deleteProjectile(this);
//			}
			this.collisionCheck();
		}
		CollisionCheck collisionY = this.checkForYCollision(delta, collisionLayer, this.velocity.y, true, !this.state.equals(EntityModel.cooldownState));
		if (collisionY.isDoesCollide()) {
			if (this.settings.isBounces()) {
				this.getVelocity().y = -this.getVelocity().y;
			}
//			else if (this.settings.isDisappearOnImpact()){
//				forceCooldown = true;
//				this.actionListener.deleteProjectile(this);
//			}
			this.collisionCheck();
		}
	}
	
	public float getEffectiveRange() {
		if (this.settings.getDuration() == null) {
			return 10f;
		}
		return this.settings.getDuration() * this.projectileSpeed();
	}
	
	protected float projectileSpeed() {
		return this.settings.getSpeed(this.state);
	}
	
	//If characterModel is null, projectile expires.
//	public void processExpirationOrHit(CharacterModel characterModel){

//			if (this.settings.isDisappearOnImpact()) {
//				this.actionListener.deleteProjectile(this);
//				forceCooldown = true;
//			}
		
//		else {
////			this.actionListener.deleteProjectile(this);
////			forceCooldown = true;
//			this.collisionCheck(true);
//		}

//	}
	
	public void processHit(CharacterModel target) {
		if (target != null) {
			for (HitTracker tracker : this.charactersHit) {
				if (tracker.equals(target)){
					return;
				}
			}
			for (EffectSettings effectSettings : settings.getTargetEffects()) {
				EntityEffect effect = EffectInitializer.initializeEntityEffect(effectSettings, this);
				target.addEffect(effect);
			}
			target.setImmuneToInjury(true);
			this.charactersHit.add(new HitTracker(target));
			if (this.getSettings().isDisappearOnImpact()) {
				this.collisionCheck();
			}
		}
	}
	
	public boolean isActive() {
		return this.state.equals(EntityModel.windupState) || this.state.equals(EntityModel.activeState);
	}
	
	public void collisionCheck() {
		if (this.settings.isDisappearOnImpact()) {
			forceCooldown = true;
		}
		if (this.settings.getWorldEffectSettings().size > 0) {
			Vector2 effectOrigin = new Vector2(getImageHitBox().x + (getImageHitBox().width / 2f), getImageHitBox().y);
			for (WorldEffectSettings wEffectSettings : this.settings.getWorldEffectSettings()) {
				WorldEffect wEffect = EffectInitializer.initializeWorldEffect(wEffectSettings, this, this.getCollisionChecker(), this.source, this.target, effectOrigin);
				this.actionListener.addWorldEffect(wEffect);
			}
		}
	}
	
		
	private void deletionCheck() {
		if (this.currentTime > this.getTotalTime() || forceEnd) {
			this.collisionCheck();
			this.actionListener.deleteProjectile(this);
		}
		
	}
	
	private void changeStateCheck() {
		if ((this.currentTime <= this.getTotalTime() && this.currentTime > this.getWindUpPlusDuration()) || this.forceCooldown) {
			this.setState(EntityModel.cooldownState);
		}
		else if (this.currentTime > this.settings.getWindUpTime() && this.currentTime <= this.getWindUpPlusDuration()) {
			this.setState(EntityModel.activeState);
		}
		else if (this.currentTime <= this.settings.getWindUpTime()) {
			this.setState(EntityModel.windupState);
		}		
	}
	
	public float getTotalTime() {
		if (this.forceCooldown) {
			return this.settings.getWindUpTime() + this.activeTime + this.settings.getCooldownTime();
		}
		return this.settings.getWindUpTime() + this.settings.getDuration() + this.settings.getCooldownTime(); 
	}
	
	private float getWindUpPlusDuration() {
		if (this.forceCooldown) {
			return this.settings.getWindUpTime() + this.activeTime;
		}
		return this.settings.getWindUpTime() + this.settings.getDuration();
	}
	
	public void setState(String state) {
		if (!this.state.equals(state))
		{
			this.didChangeState = true;
		}
		if (state.equals(EntityModel.cooldownState)) {
			this.settings.disableGravity();
		}
		this.state = state;
	}
	
	private float getVelocityAngle() {
		if (!this.settings.getShouldRotate()) {
			return 0f;
		}
		double angleInRads = Math.atan2(velocity.y, velocity.x);
		return (float) Math.toDegrees(angleInRads);
	}

	public CharacterModel getTarget() {
		return target;
	}

	public void setTarget(CharacterModel target) {
		this.target = target;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	protected void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public boolean isFacingLeft() {
		return this.velocity.x < 0;
	}

	public void setProjectileState(String state) {
		this.state = state;
		this.projectileUIModel.setAnimationTime(0f);
	}
	
	public int getAllegiance() {
		return allegiance;
	}

	public void setAllegiance(int allegiance) {
		this.allegiance = allegiance;
	}

	public void setPosition (float x, float y)
	{
		this.imageHitBox.x = x;
		this.imageHitBox.y = y;
	}

	public EntityUIModel getProjectileUIModel() {
		return projectileUIModel;
	}
	
	public void setSettings(ProjectileSettings settings) {
		this.settings = settings;
	}

	public ProjectileSettings getSettings() {
		return settings;
	}
	
	public float getStateTime() {
		return currentTime;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Projectile) {
			return ((Projectile) obj).uuid.equals(this.uuid); 
		}
		return super.equals(obj);
	}

	@Override
	public boolean handleAdditionCollisionLogic(Rectangle tempGameplayBounds) {
		return false;
	}

	@Override
	public MovementEffectSettings getReplacementMovementForStagger() {
		MovementEffectSettings mSettings = null;
		for (EffectSettings settings : this.settings.getTargetEffects()) {
			if (settings instanceof MovementEffectSettings) {
				mSettings = (MovementEffectSettings) settings;
			}
		}
		return mSettings;
	}

	@Override
	public String getState() {
		return this.state;
	}
	
	@Override
	public ActionListener getActionListener() {
		return this.actionListener;
	}
	
	@Override
	public Vector2 spawnOriginForChar() {
		return new Vector2(this.getImageHitBox().x, this.getImageHitBox().y);
	}
}
