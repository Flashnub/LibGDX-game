package com.mygdx.game.model.characters;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.actions.Attack;
import com.mygdx.game.model.characters.CollisionCheck.CollisionType;
import com.mygdx.game.model.effects.EntityEffect;
import com.mygdx.game.model.effects.EntityEffectSettings;
import com.mygdx.game.model.effects.XMovementEffect;
import com.mygdx.game.model.effects.XMovementEffectSettings;
import com.mygdx.game.model.effects.YMovementEffect;
import com.mygdx.game.model.effects.YMovementEffectSettings;
import com.mygdx.game.model.events.AssaultInterceptor;
import com.mygdx.game.model.events.CollisionChecker;
import com.mygdx.game.utils.CellWrapper;
import com.mygdx.game.utils.CellWrapper.CellType;

public abstract class EntityModel {
	
	enum SlopeSide {
		LEFT, RIGHT, NAN
	}
	
	public static final String activeState = "Active";
	public static final String windupState = "Windup";
	public static final String cooldownState = "Cooldown";
	
	public static final String Impassable = "Impassable";
	public static final String OneWayPlat = "OneWayPlat";
	public static final String OneWaySlope = "OneWaySlope";
	public static final String RightSlopeHeight = "RightSlopeHeight";
	public static final String LeftSlopeHeight = "LeftSlopeHeight";
	
	public Vector2 velocity, acceleration;
	public Rectangle gameplayCollisionBox;
	public Rectangle imageBounds;
	public Array <Rectangle> fixedHurtBoxProperties;
	public Array <Rectangle> gameplayHurtBoxes;
	public float widthCoefficient;
	public float heightCoefficient;
	float xOffsetModifier;
	float yOffsetModifier;
	public int allegiance;
	CollisionChecker collisionChecker;
	
	boolean lockTileCollisionBehavior;
	boolean lockEntityCollisionBehavior;
	boolean lockObjectCollisionBehavior;

	boolean isRespectingTileCollision;
	boolean isRespectingObjectCollision;
	boolean isRespectingEntityCollision;
	
	boolean isRespectingOneWayCollision;
	float timeToDisrespectOneWayCollision;
	
	//SHOULD NOT BE MODIFIED
	boolean shouldRespectTileCollision;
	boolean shouldRespectObjectCollision;
	boolean shouldRespectEntityCollision;
	int entityCollisionRepositionTokens;
//	SlopeSide slopeSide;
	boolean onSlope;


	
	ArrayList <EntityEffect> currentEffects;
	ArrayList <Integer> indicesToRemove;

	
	
	public EntityModel() {
		super();
		this.velocity = new Vector2();
		this.acceleration = new Vector2();
		this.imageBounds = new Rectangle();
		this.gameplayCollisionBox = new Rectangle();
		this.gameplayHurtBoxes = new Array <Rectangle>();
		this.fixedHurtBoxProperties = new Array <Rectangle>();
		this.allegiance = 0;
		
		this.lockEntityCollisionBehavior = false;
		this.lockObjectCollisionBehavior = false;
		this.lockTileCollisionBehavior = false;
		
		shouldRespectEntityCollision = true;
		shouldRespectObjectCollision = true;
		shouldRespectTileCollision = true;
		
		this.isRespectingEntityCollision = this.shouldRespectEntityCollision;
		this.isRespectingObjectCollision = this.shouldRespectObjectCollision;
		this.isRespectingTileCollision = this.shouldRespectTileCollision;
		this.isRespectingOneWayCollision = this.shouldRespectTileCollision;
		
		this.widthCoefficient = 1f;
		this.heightCoefficient = 1f;
		this.xOffsetModifier = 0f;
		this.yOffsetModifier = 0f;
		this.entityCollisionRepositionTokens = 1;
		
		this.currentEffects = new ArrayList <EntityEffect>();
		this.indicesToRemove = new ArrayList<Integer>();
//		this.slopeSide = SlopeSide.NAN;
		this.onSlope = false;
		this.timeToDisrespectOneWayCollision = 0f;
	}
	
	//Not used by projectiles.
	public void setGameplayCollisionSize(float delta) {	
		this.gameplayCollisionBox.width = this.getImageHitBox().width * widthCoefficient;
		this.gameplayCollisionBox.height = this.getImageHitBox().height * heightCoefficient;
	}	
	
	public void handleCollisionRespectChecks() {
		if (!this.lockEntityCollisionBehavior && 
			this.isRespectingEntityCollision != this.shouldRespectEntityCollision)
		{
			EntityCollisionData entityCollisionData = this.collisionChecker.checkIfEntityCollidesWithOthers(this, this.gameplayCollisionBox, false);
			if (entityCollisionData != null) {
				entityCollisionData.source.setRespectingEntityCollision(false);
			}
			else {
//				System.out.println("stop respecting " + this.toString());
				this.isRespectingEntityCollision = shouldRespectEntityCollision;
			}
		}
	}
	
	public CollisionInfo singletonYCheckWithoutVelocity(TiledMapTileLayer collisionLayer, Rectangle tempGameplayBounds) {
		return this.singletonXCheckForTileCollision(collisionLayer, tempGameplayBounds, false, 1);
	}

	public CollisionInfo singletonYCheckForTileCollision(TiledMapTileLayer collisionLayer, Rectangle tempGameplayBounds, boolean useVelocityOptimization, float yTempVelocity) {
		
		boolean collision = false;
		float pointOfReturn = tempGameplayBounds.y;
		float tileWidth = collisionLayer.getTileWidth();
		float tileHeight = collisionLayer.getTileHeight();
		Array <CellWrapper> tilesToCheck = new Array <CellWrapper>();
		
		int bottomLeftXIndex = (int) (tempGameplayBounds.x / tileWidth);
		int bottomLeftYIndex = (int) ((tempGameplayBounds.y) / tileHeight);
			
		Cell bottomLeftBlock = collisionLayer.getCell(bottomLeftXIndex, bottomLeftYIndex);
			
		int bottomMiddleXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width / 2) / tileWidth);
		int bottomMiddleYIndex = (int) (tempGameplayBounds.y / tileHeight);
			
		Cell bottomMiddleBlock = collisionLayer.getCell(bottomMiddleXIndex, bottomMiddleYIndex);
			
		int bottomRightXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth);
		int bottomRightYIndex = (int) (tempGameplayBounds.y / tileHeight);
			
		Cell bottomRightBlock = collisionLayer.getCell(bottomRightXIndex, bottomRightYIndex);
		int topLeftXIndex = (int) (tempGameplayBounds.x / tileWidth);
		int topLeftYIndex = (int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight);
			
		Cell topLeftBlock = collisionLayer.getCell(topLeftXIndex, topLeftYIndex);
			
		int topMiddleXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width / 2) / tileWidth);
		int topMiddleYIndex = (int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight);
			
		Cell topMiddleBlock = collisionLayer.getCell(topMiddleXIndex, topMiddleYIndex);
			
		int topRightXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth);
		int topRightYIndex = (int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight);
			
		Cell topRightBlock = collisionLayer.getCell(topRightXIndex, topRightYIndex);

		if (useVelocityOptimization) {
			if (this.isRespectingTileCollision && yTempVelocity < 0)
			{			
				if (bottomLeftBlock != null)
					tilesToCheck.add(new CellWrapper(bottomLeftBlock, new Vector2(bottomLeftXIndex, bottomLeftYIndex), CellType.Bottom, tileWidth, tileHeight));
				if (bottomMiddleBlock != null)
					tilesToCheck.add(new CellWrapper(bottomMiddleBlock, new Vector2(bottomMiddleXIndex, bottomMiddleYIndex), CellType.Bottom, tileWidth, tileHeight));
				if (bottomRightBlock != null)
					tilesToCheck.add(new CellWrapper(bottomRightBlock, new Vector2(bottomRightXIndex, bottomRightYIndex), CellType.Bottom, tileWidth, tileHeight));
			}
			else if (this.isRespectingTileCollision && yTempVelocity > 0) {		
				if (topLeftBlock != null)
					tilesToCheck.add(new CellWrapper(topLeftBlock, new Vector2(topLeftXIndex, topLeftYIndex), CellType.Top, tileWidth, tileHeight));
				if (topMiddleBlock != null)
					tilesToCheck.add(new CellWrapper(topMiddleBlock, new Vector2(topMiddleXIndex, topMiddleYIndex), CellType.Top, tileWidth, tileHeight));
				if (topRightBlock != null)
					tilesToCheck.add(new CellWrapper(topRightBlock, new Vector2(topRightXIndex, topRightYIndex), CellType.Top, tileWidth, tileHeight));
			}
		}
		else {
			if (topLeftBlock != null)
				tilesToCheck.add(new CellWrapper(topLeftBlock, new Vector2(topLeftXIndex, topLeftYIndex), CellType.Top, tileWidth, tileHeight));
			if (topMiddleBlock != null)
				tilesToCheck.add(new CellWrapper(topMiddleBlock, new Vector2(topMiddleXIndex, topMiddleYIndex), CellType.Top, tileWidth, tileHeight));
			if (topRightBlock != null)
				tilesToCheck.add(new CellWrapper(topRightBlock, new Vector2(topRightXIndex, topRightYIndex), CellType.Top, tileWidth, tileHeight));
			if (bottomLeftBlock != null)
				tilesToCheck.add(new CellWrapper(bottomLeftBlock, new Vector2(bottomLeftXIndex, bottomLeftYIndex), CellType.Bottom, tileWidth, tileHeight));
			if (bottomMiddleBlock != null)
				tilesToCheck.add(new CellWrapper(bottomMiddleBlock, new Vector2(bottomMiddleXIndex, bottomMiddleYIndex), CellType.Bottom, tileWidth, tileHeight));
			if (bottomRightBlock != null)
				tilesToCheck.add(new CellWrapper(bottomRightBlock, new Vector2(bottomRightXIndex, bottomRightYIndex), CellType.Bottom, tileWidth, tileHeight));
		}
		
		for (CellWrapper tile : tilesToCheck) {
			if (collision) {
				break;
			}

				Boolean impassable = ((Boolean)tile.getCell().getTile().getProperties().get(EntityModel.Impassable));
				if (impassable != null && impassable.booleanValue()) {
					collision = true;
				}
				
				if (!collision && this.gameplayCollisionBox.y > tile.getRectangleForCell().y && this.isRespectingOneWayCollision) {
					Boolean oneWayPlat = ((Boolean)tile.getCell().getTile().getProperties().get(EntityModel.OneWayPlat));
					if (oneWayPlat != null && oneWayPlat.booleanValue()) {
						collision = true;
					}
				}
				
				if (collision) {
					switch (tile.cellType()) {
					case Left:
						pointOfReturn = tile.getOrigin().x * tileWidth + tileWidth;
						break;
					case Bottom:
						pointOfReturn = tile.getOrigin().y * tileHeight + tileHeight;
						break;
					case Top:
						pointOfReturn = tile.getOrigin().y * tileHeight - tempGameplayBounds.height;
						break;
					case Right: 
						pointOfReturn = tile.getOrigin().x * tileWidth - (this.gameplayCollisionBox.width + 0.25f);
						break;
					}
				}
			
		
		}
		
		return new CollisionInfo(pointOfReturn, collision);
	}
	
	public CollisionInfo singletonXCheckWithoutVelocity(TiledMapTileLayer collisionLayer, Rectangle tempGameplayBounds) {
		return this.singletonXCheckForTileCollision(collisionLayer, tempGameplayBounds, false, 1);
	}
	
	public CollisionInfo singletonXCheckForTileCollision(TiledMapTileLayer collisionLayer, Rectangle tempGameplayBounds, boolean useVelocityOptimization, float xTempVelocity) {
		boolean collision = false;
		float pointOfReturn = tempGameplayBounds.y;
		float tileWidth = collisionLayer.getTileWidth();
		float tileHeight = collisionLayer.getTileHeight();
		Array <CellWrapper> tilesToCheck = new Array <CellWrapper>();
		
		//left blocks
		int topLeftXIndex = ((int) (tempGameplayBounds.x / tileWidth));
		int topLeftYIndex = ((int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight));

		Cell topLeftBlock = collisionLayer.getCell(topLeftXIndex, topLeftYIndex);
			
		int middleLeftXIndex = ((int) (tempGameplayBounds.x / tileWidth));
		int middleLeftYIndex = ((int) ((tempGameplayBounds.y + tempGameplayBounds.height / 2) / tileHeight));
			
		Cell middleLeftBlock = collisionLayer.getCell(middleLeftXIndex, middleLeftYIndex);
			
		int lowerLeftXIndex = ((int) (tempGameplayBounds.x / tileWidth));
		int lowerLeftYIndex = ((int) ((tempGameplayBounds.y) / tileHeight));
			
		Cell lowerLeftBlock = collisionLayer.getCell(lowerLeftXIndex, lowerLeftYIndex);
		

		//right blocks
		int topRightXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth);
		int topRightYIndex = (int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight);
			
		Cell topRightBlock = collisionLayer.getCell(topRightXIndex, topRightYIndex);
			
		int middleRightXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth);
		int middleRightYIndex = (int) ((tempGameplayBounds.y + tempGameplayBounds.height / 2) / tileHeight);
			
		Cell middleRightBlock = collisionLayer.getCell(middleRightXIndex, middleRightYIndex);
			
		int lowerRightXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth);
		int lowerRightYIndex = (int) ((tempGameplayBounds.y) / tileHeight);
			
		Cell lowerRightBlock = collisionLayer.getCell(lowerRightXIndex, lowerRightYIndex);
		
		if (useVelocityOptimization) {
			if (this.isRespectingTileCollision && xTempVelocity < 0) {
				//left blocks
				if (topLeftBlock != null)
					tilesToCheck.add(new CellWrapper(topLeftBlock, new Vector2(topLeftXIndex, topLeftYIndex), CellType.Left, tileWidth, tileHeight));
				if (middleLeftBlock != null)
					tilesToCheck.add(new CellWrapper(middleLeftBlock, new Vector2(middleLeftXIndex, middleLeftYIndex), CellType.Left, tileWidth, tileHeight));
				if (lowerLeftBlock != null)
					tilesToCheck.add(new CellWrapper(lowerLeftBlock, new Vector2(lowerLeftXIndex, lowerLeftYIndex), CellType.Left, tileWidth, tileHeight));
			}
			else if (this.isRespectingTileCollision && xTempVelocity > 0) {
				//right blocks			
				if (topRightBlock != null)
					tilesToCheck.add(new CellWrapper(topRightBlock, new Vector2(topRightXIndex, topRightYIndex), CellType.Right, tileWidth, tileHeight));
				if (middleRightBlock != null)
					tilesToCheck.add(new CellWrapper(middleRightBlock, new Vector2(middleRightXIndex, middleRightYIndex), CellType.Right, tileWidth, tileHeight));
				if (lowerRightBlock != null)
					tilesToCheck.add(new CellWrapper(lowerRightBlock, new Vector2(lowerRightXIndex, lowerRightYIndex), CellType.Right, tileWidth, tileHeight));

			}
		}
		else {
			//left blocks
			if (topLeftBlock != null)
				tilesToCheck.add(new CellWrapper(topLeftBlock, new Vector2(topLeftXIndex, topLeftYIndex), CellType.Left, tileWidth, tileHeight));
			if (middleLeftBlock != null)
				tilesToCheck.add(new CellWrapper(middleLeftBlock, new Vector2(middleLeftXIndex, middleLeftYIndex), CellType.Left, tileWidth, tileHeight));
			if (lowerLeftBlock != null)
				tilesToCheck.add(new CellWrapper(lowerLeftBlock, new Vector2(lowerLeftXIndex, lowerLeftYIndex), CellType.Left, tileWidth, tileHeight));
			if (topRightBlock != null)
				tilesToCheck.add(new CellWrapper(topRightBlock, new Vector2(topRightXIndex, topRightYIndex), CellType.Right, tileWidth, tileHeight));
			if (middleRightBlock != null)
				tilesToCheck.add(new CellWrapper(middleRightBlock, new Vector2(middleRightXIndex, middleRightYIndex), CellType.Right, tileWidth, tileHeight));
			if (lowerRightBlock != null)
				tilesToCheck.add(new CellWrapper(lowerRightBlock, new Vector2(lowerRightXIndex, lowerRightYIndex), CellType.Right, tileWidth, tileHeight));
		}
		
		for (CellWrapper tile : tilesToCheck) {
			if (collision) {
				break;
			}
			Boolean impassable = ((Boolean)tile.getCell().getTile().getProperties().get(EntityModel.Impassable));
			if (impassable != null && impassable.booleanValue()) {
				collision = true;
			}
			switch (tile.cellType()) {
			case Left:
				pointOfReturn = tile.getOrigin().x * tileWidth + tileWidth;
				break;
			case Bottom:
				pointOfReturn = tile.getOrigin().y * tileHeight + tileHeight;
				break;
			case Top:
				pointOfReturn = tile.getOrigin().y * tileHeight - tempGameplayBounds.height;
				break;
			case Right: 
				pointOfReturn = tile.getOrigin().x * tileWidth - (this.gameplayCollisionBox.width + 0.25f);
				break;
			}
		}
			
		return new CollisionInfo(pointOfReturn, collision);
	}
	
	public CollisionCheck checkForYCollision(float maxTime, TiledMapTileLayer collisionLayer, float yVelocity, boolean shouldMove, boolean applyGravity) {
		boolean collisionY = false;
		float pointOfReturn = 0f;
		float time = 0f;
		
		Rectangle tempImageBounds = new Rectangle(this.imageBounds);
		Rectangle tempGameplayBounds = new Rectangle(this.gameplayCollisionBox);
//		Array <CellWrapper> tilesToCheckForWorldObjects = new Array<CellWrapper>();
		float tempVelocity = yVelocity;
		float tempMaxTime = maxTime;
		float increment;
		EntityCollisionData entityCollisionData = null;
		CollisionType collisionType = CollisionType.None;
		
		while (tempMaxTime > 0f) {
			
			if (tempMaxTime > 1f / 400f) {
				increment = 1 / 400f;
			}
			else {
				increment = tempMaxTime;
			}
			if (applyGravity)
				tempVelocity += this.acceleration.y * increment;
			
			tempGameplayBounds.setY(tempGameplayBounds.getY() + tempVelocity * increment);
			tempImageBounds.y = -1f * (this.yOffsetModifier + (tempImageBounds.height * ((1f - this.heightCoefficient) / 2)) - tempGameplayBounds.y );
		
			//If on slope then make sure that gravity moves character down or moves up.
			//If on slope, then forgo tile collision till off of slope.
			int middleSideXIndex = ((int) ((tempGameplayBounds.x + tempGameplayBounds.width / 2) / collisionLayer.getTileWidth()));
			int yIndex = (int) ((this.gameplayCollisionBox.y) / collisionLayer.getTileHeight());
			
			SlopeInfo middleCellInfo = this.provideSlopeInfoFor(middleSideXIndex, yIndex, collisionLayer, tempGameplayBounds, tempVelocity);
			SlopeInfo aboveMiddleCellInfo = this.provideSlopeInfoFor(middleSideXIndex, yIndex + 1, collisionLayer, tempGameplayBounds, tempVelocity);
			SlopeInfo belowMiddleCellInfo = this.provideSlopeInfoFor(middleSideXIndex, yIndex - 1, collisionLayer, tempGameplayBounds, tempVelocity);

			if (this.checkSlopes()) {
				if (middleCellInfo != null) {
					pointOfReturn = middleCellInfo.pointOfReturn;
					onSlope = middleCellInfo.onSlope;
					collisionY = true;
				}
				else if (aboveMiddleCellInfo != null && this.onSlope) {
					pointOfReturn = aboveMiddleCellInfo.pointOfReturn;
					onSlope = aboveMiddleCellInfo.onSlope;
					collisionY = true;
				}
				else if (belowMiddleCellInfo != null && this.onSlope) {
					pointOfReturn = belowMiddleCellInfo.pointOfReturn;
					onSlope = true;
					collisionY = true;
				}
							
				if (collisionY) {
					collisionType = CollisionType.Slope;
				}
			}
//			else if (this.checkSlopes()) {
//				if (middleCellInfo != null) {
//					pointOfReturn = middleCellInfo.pointOfReturn;
//					onSlope = true;
//					collisionY = true;
//				}
//				else if (this.onSlope) {
//					if (aboveMiddleCellInfo != null) {
//						pointOfReturn = aboveMiddleCellInfo.pointOfReturn;
//						onSlope = aboveMiddleCellInfo.onSlope;
//						collisionY = true;
//					}
//					else if (belowMiddleCellInfo != null) {
//						pointOfReturn = belowMiddleCellInfo.pointOfReturn;
//						onSlope = belowMiddleCellInfo.onSlope;
//						collisionY = true;
//					}
//				}
//			}

			if (!collisionY) {
				//Tile
				if (this.onSlope) {
					this.onSlope = false;
				}
				else {
					CollisionInfo info = this.singletonYCheckForTileCollision(collisionLayer, tempGameplayBounds, true, tempVelocity);
					collisionY = info.didCollide;
					pointOfReturn = info.pointOfReturn;
				}
			}

			if (collisionY && collisionType.equals(CollisionType.None)) {
				collisionType = CollisionType.World;
			}
			
			//WorldObject
			if (!collisionY && this.isRespectingObjectCollision) {
				collisionY = this.collisionChecker.checkIfEntityCollidesWithObjects(this, tempGameplayBounds);
				if (collisionY && collisionType.equals(CollisionType.None)) {
					collisionType = CollisionType.Object;
				}
			}

			//EntityCollision
			if (!collisionY) {
				EntityCollisionData additionalCollisionY = this.handleEntityYCollisionLogic(tempGameplayBounds, tempImageBounds, collisionY);
				if (additionalCollisionY != null) {
					collisionY = true;
					collisionType = CollisionType.Entity;
					entityCollisionData = additionalCollisionY;
				}
				//Make sure the solution doesn't include another entity/World/object collision.
			}

			
			if (collisionY)
			{
				break;
			}
//			else {
//				tilesToCheckForWorldObjects.clear();
//			}
			
			time += increment;
			tempMaxTime -= increment;
		}
		
		//Get the entityCollisionData and put it here, and figure out if the reposition
		//would interfere with object/world/entity collisions. If it does, 
		if (shouldMove && !collisionY) {
			this.gameplayCollisionBox.y = tempGameplayBounds.y;
			this.imageBounds.y = tempImageBounds.y;
			this.velocity.y = tempVelocity;
		}
		else if (shouldMove && (collisionType.equals(CollisionType.World) || collisionType.equals(CollisionType.Slope))) {
			this.gameplayCollisionBox.y = pointOfReturn;
			this.refreshImageHitBoxY();
		}
		else if (shouldMove && collisionType.equals(CollisionType.Entity))
		{
			//Figure out reposition AND MAKE SURE IT DOESN'T HAVE TILE/OBJECT COLLISION ISSUE. If it does, just do
			//the stop movement thing.
			Rectangle sourceHitBox = new Rectangle(this.gameplayCollisionBox);
			Rectangle collidedEntityHitBox = new Rectangle(entityCollisionData.collidedEntity.gameplayCollisionBox);
			switch(entityCollisionData.collisionLocation) {
			case Left:
				sourceHitBox.x = collidedEntityHitBox.x - sourceHitBox.width - 2f; //buffer.
				CollisionInfo xCheckInfo = this.singletonXCheckWithoutVelocity(collisionLayer, sourceHitBox);
				CollisionInfo yCheckInfo = this.singletonYCheckWithoutVelocity(collisionLayer, sourceHitBox);
				boolean objectCheck = this.collisionChecker.checkIfEntityCollidesWithObjects(this, sourceHitBox);
				//May need to reenable this....
//				EntityCollisionData extraEntityCheck = this.collisionChecker.checkIfEntityCollidesWithOthers(this, sourceHitBox);
				if (!xCheckInfo.didCollide && !yCheckInfo.didCollide && !objectCheck && entityCollisionRepositionTokens > 0) {
					this.gameplayCollisionBox.x = sourceHitBox.x;
					this.refreshImageHitBoxX();
					this.refreshHurtBoxesX();
					entityCollisionRepositionTokens -= 1;
				}
				break;
			case Right:
				sourceHitBox.x = collidedEntityHitBox.x + collidedEntityHitBox.width + 2f; //buffer.
				CollisionInfo xCheckInfo2 = this.singletonXCheckWithoutVelocity(collisionLayer, sourceHitBox);
				CollisionInfo yCheckInfo2 = this.singletonYCheckWithoutVelocity(collisionLayer, sourceHitBox);
				boolean objectCheck2 = this.collisionChecker.checkIfEntityCollidesWithObjects(this, sourceHitBox);
				//May need to reenable this....
//				EntityCollisionData extraEntityCheck2 = this.collisionChecker.checkIfEntityCollidesWithOthers(this, sourceHitBox);
				if (!xCheckInfo2.didCollide && !yCheckInfo2.didCollide && !objectCheck2 && entityCollisionRepositionTokens > 0) {
					this.gameplayCollisionBox.x = sourceHitBox.x;
					this.refreshImageHitBoxX();
					this.refreshHurtBoxesY();
					entityCollisionRepositionTokens -= 1;
				}
				break;
			}
		}

		//react to X collision
		return new CollisionCheck(collisionY, tempVelocity > 0, time, collisionType, pointOfReturn);
	}
	
	public XMovementEffect getXMove() {
		for (EntityEffect effect : this.currentEffects) {
			if (effect instanceof XMovementEffect) {
				return (XMovementEffect) effect;
			}
		}
		return null;
	}
	
	public YMovementEffect getYMove() {
		for (EntityEffect effect : this.currentEffects) {
			if (effect instanceof YMovementEffect) {
				return (YMovementEffect) effect;
			}
		}
		return null;
	}
	
	protected void handleEffects(float delta) {
		this.indicesToRemove.clear();
		for (Integer priority : EntityEffect.getPriorities()) {
			processEffectsOfPriority(priority.intValue(), delta);
		}

	}
	
	private void processEffectsOfPriority(int priority, float delta) {
		for(Iterator<EntityEffect> iterator = this.currentEffects.iterator(); iterator.hasNext();) {
			EntityEffect effect = iterator.next();
			if (effect.getPriority() == priority) {
				boolean isFinished = effect.process(this, delta);
				if (isFinished) {
					iterator.remove();
				}
			}
		}
	}
	
	public void addEffect(EntityEffect effect) {
		if (effect.isUniqueEffect()) {
			for (EntityEffect currentEffect : this.currentEffects) {
				if (currentEffect.getClass().equals(effect.getClass())) {
					currentEffect.setForceEnd(true);
				}
			}
		}
		currentEffects.add(effect);
	}
	
	public void removeEffectByID(Integer effectID) {
		if (effectID.intValue() != EntityEffectSettings.defaultID) {
			for(Iterator<EntityEffect> iterator = this.currentEffects.iterator(); iterator.hasNext();) {
				EntityEffect effect = iterator.next();
				if (effect.getSpecificID().intValue() == effectID.intValue() && effect.getSpecificID().intValue() != EntityEffectSettings.defaultID) {
					effect.setForceEnd(true);
					return;
				}
			}
		}
	}
	
	public boolean checkIfIntercepted(Attack attack) {
		boolean interceptedAttack = false;
		for (EntityEffect effect : this.currentEffects) {
			if (effect instanceof AssaultInterceptor && effect.isActive()) {
//				AssaultInterceptor attackInterceptor = (AssaultInterceptor) effect;
//				interceptedAttack = attackInterceptor.didInterceptAttack(this, attack);
				interceptedAttack = true;
			}
		}
		return interceptedAttack;
	}
	
	public abstract EntityCollisionData handleEntityXCollisionLogic(Rectangle tempGameplayBounds, Rectangle tempImageBounds, boolean alreadyCollided);
	public abstract EntityCollisionData handleEntityYCollisionLogic(Rectangle tempGameplayBounds, Rectangle tempImageBounds, boolean alreadyCollided);

	public abstract int getAllegiance();
	public abstract boolean isFacingLeft();
	public abstract String getState();
	
	public float getVelocityAngle() {
		return 0f;
	}
	
//	public Polygon getGameplayHitBoxInPolygon() {
//		Polygon polygon = new Polygon(new float[] {
//			this.gameplayCollisionBox.x, this.gameplayCollisionBox.y,
//			this.gameplayCollisionBox.x, this.gameplayCollisionBox.y + this.gameplayCollisionBox.height,
//			this.gameplayCollisionBox.x + this.gameplayCollisionBox.width, this.gameplayCollisionBox.y + this.gameplayCollisionBox.height,
//			this.gameplayCollisionBox.x + this.gameplayCollisionBox.width, this.gameplayCollisionBox.y
//		});
//		polygon.setOrigin(this.gameplayCollisionBox.x + this.gameplayCollisionBox.width / 2, this.gameplayCollisionBox.y + this.gameplayCollisionBox.height / 2);
//		polygon.setRotation(this.getVelocityAngle());
//		return polygon;
//	}

	
	public CollisionCheck checkForXCollision(float maxTime, TiledMapTileLayer collisionLayer, float xVelocity, float xAcceleration, boolean shouldMove) {
		
		boolean collisionX = false;
		float pointOfReturn = 0f;
		float time = 0f;
		
		Rectangle tempImageBounds = new Rectangle(this.imageBounds);
		Rectangle tempGameplayBounds = new Rectangle(this.gameplayCollisionBox);
		float tempVelocity = xVelocity;
		float tempMaxTime = maxTime;
		float increment;
		EntityCollisionData entityCollisionData = null;
		CollisionType collisionType = CollisionType.None;
		
		while (tempMaxTime > 0f) {
			
			if (tempMaxTime > 1f / 400f) {
				increment = 1 / 400f;
			}
			else {
				increment = tempMaxTime;
			}
			
			tempVelocity += xAcceleration * increment;
			tempGameplayBounds.setX(tempGameplayBounds.getX() + tempVelocity * increment);
			tempImageBounds.x = -1f * (getXOffsetModifier() + (tempImageBounds.width * ((1f - this.widthCoefficient) / 2)) - tempGameplayBounds.x );
//			tempImageBounds.setX(tempImageBounds.getX() + tempVelocity * increment);
//			tempGameplayBounds.setX(this.xOffsetModifier + tempImageBounds.getX() + tempImageBounds.getWidth() * ((1f - this.widthCoefficient) / 2));

			//If on slope, then forgo tile collision till off of slope.
//			int gameplayHitBoxXIndex = ((int) ((isFacingLeft() ? gameplayHitBox.x : gameplayHitBox.x + gameplayHitBox.width) / collisionLayer.getTileWidth()));
//			int gameplayHitBoxYIndex = ((int) ((tempGameplayBounds.y) / collisionLayer.getTileHeight()));
//
//			Cell gameplayHitBoxCell = collisionLayer.getCell(gameplayHitBoxXIndex, gameplayHitBoxYIndex);
			if (!this.onSlope) {
				//has slope, don't check X collision
				CollisionInfo info = this.singletonXCheckForTileCollision(collisionLayer, tempGameplayBounds, true, tempVelocity);
				collisionX = info.didCollide;
				pointOfReturn = info.pointOfReturn;
			}
			
			if (collisionX) {
				collisionType = CollisionType.World;
			}
			
			//WorldObject
			if (!collisionX && this.isRespectingObjectCollision) {
				collisionX = this.collisionChecker.checkIfEntityCollidesWithObjects(this, tempGameplayBounds);
				
				if (collisionX && collisionType.equals(CollisionType.None)) {
					collisionType = CollisionType.Object;
				}
			}

			//Entity
			if (!collisionX) {
				EntityCollisionData additionalCollisionX = this.handleEntityXCollisionLogic(tempGameplayBounds, tempImageBounds, collisionX);
				if (additionalCollisionX != null) {
					collisionX = true;
					collisionType = CollisionType.Entity;
					entityCollisionData = additionalCollisionX;
				}
				
			}
			
			if (collisionX)
			{
				break;
			}
			
			time += increment;
			tempMaxTime -= increment;
		}
		//move on x axis
		if (shouldMove && !collisionX) {
			this.gameplayCollisionBox.x = tempGameplayBounds.x;
			this.imageBounds.x = tempImageBounds.x;
			this.velocity.x = tempVelocity;
		}
		else if (shouldMove && collisionType.equals(CollisionType.World)) {
			this.gameplayCollisionBox.x = pointOfReturn;
			this.refreshImageHitBoxX();
		}
		else if (shouldMove && collisionType.equals(CollisionType.Entity))
		{
			//Figure out reposition AND MAKE SURE IT DOESN'T HAVE TILE/OBJECT COLLISION ISSUE. If it does, just do
			//the stop movement thing.
			Rectangle sourceCollisionBox = new Rectangle(this.gameplayCollisionBox);
			Rectangle collidedEntityHitBox = new Rectangle(entityCollisionData.collidedEntity.gameplayCollisionBox);
			switch(entityCollisionData.collisionLocation) {
			case Left:
				sourceCollisionBox.x = collidedEntityHitBox.x - sourceCollisionBox.width - 2f; //buffer.
				CollisionInfo xCheckInfo = this.singletonXCheckWithoutVelocity(collisionLayer, sourceCollisionBox);
				CollisionInfo yCheckInfo = this.singletonYCheckWithoutVelocity(collisionLayer, sourceCollisionBox);
				boolean objectCheck = this.collisionChecker.checkIfEntityCollidesWithObjects(this, sourceCollisionBox);
				//May need to reenable this....
//				EntityCollisionData extraEntityCheck = this.collisionChecker.checkIfEntityCollidesWithOthers(this, sourceHitBox);
				if (!xCheckInfo.didCollide && !yCheckInfo.didCollide && !objectCheck && entityCollisionRepositionTokens > 0) {
					this.gameplayCollisionBox.x = sourceCollisionBox.x;
					this.refreshImageHitBoxX();
					entityCollisionRepositionTokens -= 1;
				}
				break;
			case Right:
				sourceCollisionBox.x = collidedEntityHitBox.x + collidedEntityHitBox.width + 2f; //buffer.
				CollisionInfo xCheckInfo2 = this.singletonXCheckWithoutVelocity(collisionLayer, sourceCollisionBox);
				CollisionInfo yCheckInfo2 = this.singletonYCheckWithoutVelocity(collisionLayer, sourceCollisionBox);
				boolean objectCheck2 = this.collisionChecker.checkIfEntityCollidesWithObjects(this, sourceCollisionBox);
				//May need to reenable this....
//				EntityCollisionData extraEntityCheck2 = this.collisionChecker.checkIfEntityCollidesWithOthers(this, sourceHitBox);
				if (!xCheckInfo2.didCollide && !yCheckInfo2.didCollide && !objectCheck2 && entityCollisionRepositionTokens > 0) {
					this.gameplayCollisionBox.x = sourceCollisionBox.x;
					this.refreshImageHitBoxX();
					entityCollisionRepositionTokens -= 1;
				}
				break;
			}
		}

		return new CollisionCheck(collisionX, tempVelocity > 0, time, collisionType, pointOfReturn);

	}
	
	public SlopeInfo provideSlopeInfoFor(int xIndex, int yIndex, TiledMapTileLayer collisionLayer, Rectangle tempGameplayBounds, float yVelocity) {
		//figure out how far up/down
		Cell tile = collisionLayer.getCell(xIndex, yIndex);
		if (tile != null 
				&& (tile.getTile().getProperties().containsKey(EntityModel.OneWaySlope) && this.isRespectingOneWayCollision) 
				||	!(tile.getTile().getProperties().containsKey(EntityModel.OneWaySlope))) 
		{
			if (tile.getTile().getProperties().containsKey(EntityModel.LeftSlopeHeight) && yVelocity <= 0) {
				float leftSlope = (Float) tile.getTile().getProperties().get(EntityModel.LeftSlopeHeight);
				float rightSlope = (Float) tile.getTile().getProperties().get(EntityModel.RightSlopeHeight);
				
				float differenceInSlope = Math.max(leftSlope, rightSlope) - Math.min(leftSlope, rightSlope);
				float howFarEntityIsInSlopeTile = ((tempGameplayBounds.x + tempGameplayBounds.width / 2) % collisionLayer.getTileWidth());
				float proportionOfDistance = 0f;
				if (rightSlope > leftSlope) {
					proportionOfDistance = howFarEntityIsInSlopeTile / collisionLayer.getTileWidth();
				}
				else {
					proportionOfDistance = 1f - (howFarEntityIsInSlopeTile / collisionLayer.getTileWidth());
				}
			    float verticalEntityDistance = differenceInSlope * proportionOfDistance + yIndex * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
			    
				return new SlopeInfo(true, verticalEntityDistance);
			}
		}

		return null;
	}
	
	public boolean checkSlopes() {
		return true;
	}
	
	public void refreshImageHitBoxX() {
		this.imageBounds.x = -1f * (getXOffsetModifier() + (this.imageBounds.width * ((1f - this.widthCoefficient) / 2)) - this.gameplayCollisionBox.x );
	}
	
	
	public void refreshImageHitBoxY() {
		this.imageBounds.y = -1f * (this.yOffsetModifier + (this.imageBounds.height * ((1f - this.heightCoefficient) / 2)) - this.gameplayCollisionBox.y );
	}
	
	public abstract void refreshHurtBoxesX();
	public abstract void refreshHurtBoxesY();
	
	public void updateHurtBoxProperties (Array <Rectangle> newHurtBoxProperties) {
		this.fixedHurtBoxProperties = newHurtBoxProperties;
	}
	
	public float getXOffsetModifier() {
		return isFacingLeft() ? this.xOffsetModifier : -this.xOffsetModifier; 
	}
	
	public boolean isInAir() {
		//Nothing.
		return false;
	}
	
	public void addToCurrentWill(float value) {
		
	}
	
	public void setIsInAir(boolean isInAir) {
		//Nothing
	}
	
	public void addToCurrentHealth(float healing) {
		//nothing.
	}

	public void removeFromCurrentHealth(float removal) {
		//nothing.
	}
	
	public void addToCurrentTension(float tension) {
		
	}
	
	public void removeFromCurrentStability(float stability, YMovementEffectSettings yReplacementMovementEffect) {
		
	}
	
	public void actionStagger(boolean stabilityStaggering) {
		
	}
	
	public Array<Rectangle> getGameplayHurtBoxes() {
		return gameplayHurtBoxes;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public Vector2 getAcceleration() {
		return acceleration;
	}

	public Rectangle getGameplayCollisionBox() {
		return gameplayCollisionBox;
	}

	public Rectangle getImageHitBox() {
		return imageBounds;
	}

	public CollisionChecker getCollisionChecker() {
		return collisionChecker;
	}

	public void setCollisionChecker(CollisionChecker itemListener) {
		this.collisionChecker = itemListener;
	}
	
	public boolean isRespectingEntityCollision() {
		return this.isRespectingEntityCollision;
	}
	
	

//	public boolean hasProcessedOverlapCorrection() {
//		return hasProcessedOverlapCorrection;
//	}

	public void setRespectingEntityCollision(boolean isRespectingEntityCollision) {
		this.isRespectingEntityCollision = isRespectingEntityCollision;
	}
	
	public ArrayList<EntityEffect> getCurrentEffects() {
		return currentEffects;
	}
	
	public void lockEntityCollisionBehavior() {
		lockEntityCollisionBehavior = true;
	}

	public void unlockEntityCollisionBehavior() {
		lockEntityCollisionBehavior = false;
	}

	public void setWidthCoefficient(float widthCoefficient) {
		this.widthCoefficient = widthCoefficient;
	}

	public void setHeightCoefficient(float heightCoefficient) {
		this.heightCoefficient = heightCoefficient;
	}

	public float getyOffsetModifier() {
		return yOffsetModifier;
	}

	public void setyOffsetModifier(float yOffsetModifier) {
		this.yOffsetModifier = yOffsetModifier;
	}

	public boolean isLockTileCollisionBehavior() {
		return lockTileCollisionBehavior;
	}

	public void setLockTileCollisionBehavior(boolean lockTileCollisionBehavior) {
		this.lockTileCollisionBehavior = lockTileCollisionBehavior;
	}

	public boolean isLockEntityCollisionBehavior() {
		return lockEntityCollisionBehavior;
	}

	public void setLockEntityCollisionBehavior(boolean lockEntityCollisionBehavior) {
		this.lockEntityCollisionBehavior = lockEntityCollisionBehavior;
	}

	public boolean isLockObjectCollisionBehavior() {
		return lockObjectCollisionBehavior;
	}

	public void setLockObjectCollisionBehavior(boolean lockObjectCollisionBehavior) {
		this.lockObjectCollisionBehavior = lockObjectCollisionBehavior;
	}

	public boolean isRespectingTileCollision() {
		return isRespectingTileCollision;
	}

	public void setRespectingTileCollision(boolean isRespectingTileCollision) {
		this.isRespectingTileCollision = isRespectingTileCollision;
	}

	public boolean isRespectingObjectCollision() {
		return isRespectingObjectCollision;
	}

	public void setRespectingObjectCollision(boolean isRespectingObjectCollision) {
		this.isRespectingObjectCollision = isRespectingObjectCollision;
	}

	public boolean isShouldRespectTileCollision() {
		return shouldRespectTileCollision;
	}

	public void setShouldRespectTileCollision(boolean shouldRespectTileCollision) {
		this.shouldRespectTileCollision = shouldRespectTileCollision;
	}

	public boolean isShouldRespectObjectCollision() {
		return shouldRespectObjectCollision;
	}

	public void setShouldRespectObjectCollision(boolean shouldRespectObjectCollision) {
		this.shouldRespectObjectCollision = shouldRespectObjectCollision;
	}

	public boolean isShouldRespectEntityCollision() {
		return shouldRespectEntityCollision;
	}

	public void setShouldRespectEntityCollision(boolean shouldRespectEntityCollision) {
		this.shouldRespectEntityCollision = shouldRespectEntityCollision;
	}
	
}
