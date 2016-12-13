package com.mygdx.game.model.projectiles;

import java.util.UUID;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.constants.JSONController;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.CollisionCheck;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.characters.EntityUIDataType;
import com.mygdx.game.model.effects.Effect;
import com.mygdx.game.model.effects.EffectDataRetriever;
import com.mygdx.game.model.effects.EffectInitializer;
import com.mygdx.game.model.effects.EffectSettings;
import com.mygdx.game.model.effects.MovementEffectSettings;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.CollisionChecker;
import com.mygdx.game.utils.MathUtils;

public class Projectile extends EntityModel implements EffectDataRetriever{
//	
//	public enum ProjectileState implements State {
//		Active, Preactive;
//		
//		@Override
//		public String getState() {
//			return toString();
//		}
//	}

	private final String activeState = "Active";
	private final String preActiveState = "Preactive";
	
	private float stateTime = 0f;
	private boolean firstTimeSetup = true;
	private EntityUIModel projectileUIModel;
	private String projectileState;
	private String uuid;
	private ActionListener actionListener;
	private CharacterModel source;
	private CharacterModel target;
	private ProjectileSettings settings;
	
	public Projectile(String name, CharacterModel source, CharacterModel target, ActionListener actionListener, CollisionChecker collisionChecker)
	{
		super();
		this.settings = JSONController.projectiles.get(name);
		this.acceleration.y = -this.settings.getGravity();
		this.imageHitBox.x = source.getImageHitBox().x + (source.getImageHitBox().width / 2f) + settings.getPossibleOrigins().get(0).x;
		this.imageHitBox.y = source.getImageHitBox().y + (source.getImageHitBox().height / 2f) + settings.getPossibleOrigins().get(0).x;
		this.gameplayHitBox = this.imageHitBox;
		this.projectileUIModel = new EntityUIModel(name, EntityUIDataType.PROJECTILE);
		this.projectileState = this.activeState;
		this.actionListener = actionListener;
		this.setCollisionChecker(collisionChecker);
		this.source = source;
		this.target = target;
		UUID id = UUID.randomUUID();
		this.uuid = id.toString();
		this.allegiance = source.getAllegiance();
	}
	
	public Projectile() {
		this.projectileState = this.activeState;
	}

	public void update(float delta, TiledMapTileLayer collisionLayer) {
		if (this.firstTimeSetup || this.settings.isTracks()) {
			if (this.firstTimeSetup) {
				this.firstTimeSetup = false;
			}
			this.determineAndSetVelocity(target, collisionLayer);
			System.out.println(this.velocity);
		}
//		this.velocity.y -= this.settings.getGravity() * delta;
		this.setGameplaySize(delta, collisionLayer);
		if (this.settings.isHasCollisionDetection()) {
			this.movementWithCollisionDetection(delta, collisionLayer);
		}
		else {
			moveWithoutCollisionDetection(delta, collisionLayer);
		}
		stateTime += delta;
		projectileUIModel.setCurrentFrame(this, delta);
		actionListener.processProjectile(this);
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
			
			float yDelta = Math.max(0.01f, (expectedPositionOfTarget.y - projectilePosition.y));
			if (yDelta >= 0) {
				yDelta = Math.max(0.01f, yDelta);
			}
			else {
				yDelta = Math.min(-0.01f, yDelta);
			}
			
			float fullDelta = (float) Math.sqrt((xDelta * xDelta) + (yDelta * yDelta));
			
			float time = yDelta / ((yDelta / fullDelta) * this.projectileSpeed());
			this.velocity.x = (xDelta / fullDelta) * this.projectileSpeed();
			this.velocity.y = ((yDelta / fullDelta) * this.projectileSpeed()) + (.5f * this.settings.getGravity() * time);
//			System.out.println(time);
//			System.out.println(expectedPositionOfTarget);
//			System.out.println(target.getGameplayHitBox());
//			System.out.println(target.getVelocity());
//			System.out.println(projectilePosition);
		}
	}
	
	protected void movementWithCollisionDetection(float delta, TiledMapTileLayer collisionLayer) {
		//logic for collision detection
		CollisionCheck collisionX = this.checkForXCollision(delta, collisionLayer, this.velocity.x, true);
		if (collisionX.isDoesCollide()) {
			if (this.settings.isBounces()) {
				this.getVelocity().x = -this.getVelocity().x;
			}
			else if (this.settings.isExplodeOnImpact()){
				this.actionListener.deleteProjectile(this);
			}
		}
		CollisionCheck collisionY = this.checkForYCollision(delta, collisionLayer, this.velocity.y, true);
		if (collisionY.isDoesCollide()) {
			if (this.settings.isBounces()) {
				this.getVelocity().y = -this.getVelocity().y;
			}
			else if (this.settings.isExplodeOnImpact()){
				this.actionListener.deleteProjectile(this);
			}
		}
		//save old position
//		float oldX = this.getImageHitBox().getX(), oldY = this.getImageHitBox().getY(), tileWidth = collisionLayer.getTileWidth(), tileHeight = collisionLayer.getTileHeight();
//		boolean collisionX = false, collisionY = false;
//		
//		//move on x axis
//		this.getImageHitBox().setX(this.getImageHitBox().getX() + this.getVelocity().x * delta);
//		
//		if (this.getVelocity().x < 0) {
//			//left blocks
//			Cell topLeftBlock = collisionLayer.getCell
//					((int) (this.imageHitBox.x / tileWidth), 
//					(int) ((this.imageHitBox.y + this.imageHitBox.height) / tileHeight));
//			Cell middleLeftBlock = collisionLayer.getCell
//					((int) (this.imageHitBox.x / tileWidth), 
//					(int) ((this.imageHitBox.y + this.imageHitBox.height / 2) / tileHeight));
//			Cell lowerLeftBlock = collisionLayer.getCell
//					((int) (this.imageHitBox.x / tileWidth), 
//					(int) ((this.imageHitBox.y) / tileHeight));
//			
//			if (topLeftBlock != null)
//				collisionX = ((String)topLeftBlock.getTile().getProperties().get("Impassable")).equals("true");
//				
//			
//			//middle left block
//			if(!collisionX && middleLeftBlock != null)
//				collisionX = ((String)middleLeftBlock.getTile().getProperties().get("Impassable")).equals("true");
//			
//			//lower left block
//			if(!collisionX && lowerLeftBlock != null )
//				collisionX = ((String)lowerLeftBlock.getTile().getProperties().get("Impassable")).equals("true");
//			
////			this.leftcollisionX = collisionX;
//			
//		}
//		else if (this.getVelocity().x > 0) {
//			//right blocks
//			Cell topRightBlock = collisionLayer.getCell
//					((int) ((this.imageHitBox.x + this.imageHitBox.width) / tileWidth),
//					(int) ((this.imageHitBox.y + this.imageHitBox.height) / tileHeight));
//			Cell middleRightBlock = collisionLayer.getCell
//					((int) ((this.imageHitBox.x + this.imageHitBox.width) / tileWidth),
//					(int) ((this.imageHitBox.y + this.imageHitBox.height / 2) / tileHeight));
//			Cell lowerRightBlock = collisionLayer.getCell
//					((int) ((this.imageHitBox.x + this.imageHitBox.width) / tileWidth), 
//					(int) ((this.imageHitBox.y) / tileHeight));
//			
//			// top right block
//			if (topRightBlock != null)
//				collisionX = ((String)topRightBlock.getTile().getProperties().get("Impassable")).equals("true");
//			
//			//middle right block
//			if(!collisionX && middleRightBlock != null)
//				collisionX = ((String)middleRightBlock.getTile().getProperties().get("Impassable")).equals("true");
//			
//			//lower right block
//			if(!collisionX && lowerRightBlock != null)
//				collisionX = ((String)lowerRightBlock.getTile().getProperties().get("Impassable")).equals("true");
//			
////			this.rightCollisionX = collisionX;
//		}
//		
//		//react to X collision
//		if (collisionX) {
//			this.getImageHitBox().setX(oldX);;
//			this.getVelocity().x = -this.getVelocity().x;
//		}
//
//		
//	
//		//move on y axis
//		this.getImageHitBox().setY(this.getImageHitBox().getY() + this.getVelocity().y * delta);
//		
//		//Collision detection: Y axis
//		
//		if (this.getVelocity().y < 0) {
//			Cell bottomLeftBlock = collisionLayer.getCell((int) (this.imageHitBox.x / tileWidth), (int) ((this.bounds.y) / tileHeight));
//			Cell bottomMiddleBlock = collisionLayer.getCell((int) ((this.imageHitBox.x + this.bounds.width / 2) / tileWidth), (int) ((this.bounds.y) / tileHeight));
//			Cell bottomRightBlock = collisionLayer.getCell((int) ((this.imageHitBox.x + this.bounds.width) / tileWidth), (int) ((this.bounds.y) / tileHeight));
//			//bottom left block
//			if (bottomLeftBlock != null)
//				collisionY = ((String)bottomLeftBlock.getTile().getProperties().get("Impassable")).equals("true");
//				
//			//bottom middle block
//			if(!collisionY && bottomMiddleBlock != null)
//				collisionY = ((String)bottomMiddleBlock.getTile().getProperties().get("Impassable")).equals("true");
//			
//			//bottom right block
//			if(!collisionY && bottomRightBlock != null)
//				collisionY = ((String)bottomRightBlock.getTile().getProperties().get("Impassable")).equals("true");
//			
//	
//		} 
//		else if (this.getVelocity().y > 0) {
//			Cell topLeftBlock = collisionLayer.getCell((int) (this.bounds.x / tileWidth), (int) ((this.bounds.y + this.bounds.height) / tileHeight));			
//			Cell topMiddleBlock = collisionLayer.getCell((int) ((this.bounds.x + this.bounds.width / 2) / tileWidth), (int) ((this.bounds.y + this.bounds.height) / tileHeight));
//			Cell topRightBlock = collisionLayer.getCell((int) ((this.bounds.x + this.bounds.width) / tileWidth), (int) ((this.bounds.y + this.bounds.height) / tileHeight));
//			
//			//top left block
//			if (topLeftBlock != null)
//				collisionY = ((String)topLeftBlock.getTile().getProperties().get("Impassable")).equals("true");
//			
//			//top middle block
//			if(!collisionY && topMiddleBlock != null)
//				collisionY = ((String)topMiddleBlock.getTile().getProperties().get("Impassable")).equals("true");
//			
//			//top right block
//			if(!collisionY && topRightBlock != null)
//				collisionY = ((String)topRightBlock.getTile().getProperties().get("Impassable")).equals("true");
//		}
//		
//		//react to Ycollision
//		if (collisionY) {
//			this.getImageHitBox().setY(oldY);
//			this.getVelocity().y = -this.getVelocity().y;
//		}
//		return;
	}
	
	public float getEffectiveRange() {
		if (this.settings.getProjectileDuration() == null) {
			return 10f;
		}
		return this.settings.getProjectileDuration() * this.projectileSpeed();
	}
	
	protected float projectileSpeed() {
		return this.settings.getSpeed();
	}
	
	//If characterModel is null, projectile expires.
	public void processExpirationOrHit(CharacterModel characterModel){
		if (characterModel != null) {
			for (EffectSettings effectSettings : settings.getTargetEffects()) {
				Effect effect = EffectInitializer.initializeEffect(effectSettings, this);
				characterModel.addEffect(effect);
			}
			characterModel.setImmuneToInjury(true);
			if (this.settings.isExplodeOnImpact()) {
				this.actionListener.deleteProjectile(this);
			}
		}
		else {
			this.actionListener.deleteProjectile(this);
		}

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
	
	public String getProjectileState() {
		return projectileState;
	}

	public void setProjectileState(String projectileState) {
		this.projectileState = projectileState;
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
		return stateTime;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public MovementEffectSettings getReplacementMovement() {
		MovementEffectSettings mSettings = null;
		for (EffectSettings settings : this.settings.getTargetEffects()) {
			if (settings instanceof MovementEffectSettings) {
				mSettings = (MovementEffectSettings) settings;
			}
		}
		return mSettings;
	}
}
