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
	public static final String RightSlopeHeight = "RightSlopeHeight";
	public static final String LeftSlopeHeight = "LeftSlopeHeight";
	
	public Vector2 velocity, acceleration;
	public Rectangle gameplayHitBox;
	public Rectangle imageHitBox;
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
	
	//SHOULD NOT BE MODIFIED
	boolean shouldRespectTileCollision;
	boolean shouldRespectObjectCollision;
	boolean shouldRespectEntityCollision;
	int entityCollisionRepositionTokens;
	SlopeSide slopeSide;
	

	
	ArrayList <EntityEffect> currentEffects;
	ArrayList <Integer> indicesToRemove;

	
	
	public EntityModel() {
		super();
		this.velocity = new Vector2();
		this.acceleration = new Vector2();
		this.imageHitBox = new Rectangle();
		this.gameplayHitBox = new Rectangle();
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
		
		this.widthCoefficient = 1f;
		this.heightCoefficient = 1f;
		this.xOffsetModifier = 0f;
		this.yOffsetModifier = 0f;
		this.entityCollisionRepositionTokens = 1;
		
		this.currentEffects = new ArrayList <EntityEffect>();
		this.indicesToRemove = new ArrayList<Integer>();
		this.slopeSide = SlopeSide.NAN;

	}
	
	//Not used by projectiles.
	public void setGameplaySize(float delta) {	
		this.gameplayHitBox.width = this.getImageHitBox().width * widthCoefficient;
		this.gameplayHitBox.height = this.getImageHitBox().height * heightCoefficient;
	}	
	
	public void handleCollisionRespectChecks() {
		if (!this.lockEntityCollisionBehavior && 
			this.isRespectingEntityCollision != this.shouldRespectEntityCollision)
		{
			EntityCollisionData entityCollisionData = this.collisionChecker.checkIfEntityCollidesWithOthers(this, this.gameplayHitBox, false);
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
						pointOfReturn = tile.getOrigin().x * tileWidth - (this.gameplayHitBox.width + 0.25f);
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
				pointOfReturn = tile.getOrigin().x * tileWidth - (this.gameplayHitBox.width + 0.25f);
				break;
			}
		}
			
		return new CollisionInfo(pointOfReturn, collision);
	}
	
	public CollisionCheck checkForYCollision(float maxTime, TiledMapTileLayer collisionLayer, float yVelocity, boolean shouldMove, boolean applyGravity) {
		boolean collisionY = false;
		float pointOfReturn = 0f;
		float time = 0f;
		
		Rectangle tempImageBounds = new Rectangle(this.imageHitBox);
		Rectangle tempGameplayBounds = new Rectangle(this.gameplayHitBox);
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
			int rightSideXIndex = ((int) ((tempGameplayBounds.x + tempGameplayBounds.width) / collisionLayer.getTileWidth()));
			int leftSideXIndex = ((int) ((tempGameplayBounds.x) / collisionLayer.getTileWidth()));
			int yIndex = (int) ((this.gameplayHitBox.y) / collisionLayer.getTileHeight());
			

//			Cell leftSideCell = collisionLayer.getCell(leftSideXIndex, yIndex);
//			Cell rightSideCell = collisionLayer.getCell(rightSideXIndex, yIndex);
			SlopeInfo leftCellInfo = this.provideSlopeInfoFor(true, leftSideXIndex, yIndex, collisionLayer, tempGameplayBounds, tempVelocity);
			SlopeInfo rightCellInfo = this.provideSlopeInfoFor(false, rightSideXIndex, yIndex, collisionLayer, tempGameplayBounds, tempVelocity);

			if (this.isFacingLeft() && this.checkSlopes()) {
				if (leftCellInfo != null) {
					pointOfReturn = leftCellInfo.pointOfReturn;
					slopeSide = leftCellInfo.slopeSide;
					collisionY = true;
				}
				else if (rightCellInfo != null) {
					pointOfReturn = rightCellInfo.pointOfReturn;
					slopeSide = rightCellInfo.slopeSide;
					collisionY = true;
				}
				else if (this.slopeSide.equals(SlopeSide.RIGHT)) {
					SlopeInfo aboveRightCellInfo = this.provideSlopeInfoFor(false, rightSideXIndex, yIndex + 1, collisionLayer, tempGameplayBounds, tempVelocity);
					SlopeInfo belowRightCellInfo = this.provideSlopeInfoFor(false, rightSideXIndex, yIndex - 1, collisionLayer, tempGameplayBounds, tempVelocity);

					if (aboveRightCellInfo != null) {
						pointOfReturn = aboveRightCellInfo.pointOfReturn;
						slopeSide = aboveRightCellInfo.slopeSide;
						collisionY = true;
					}
					else if (belowRightCellInfo != null) {
						pointOfReturn = belowRightCellInfo.pointOfReturn;
						slopeSide = belowRightCellInfo.slopeSide;
						collisionY = true;
					}
				}
				else if (this.slopeSide.equals(SlopeSide.LEFT)) {
					SlopeInfo aboveLeftCellInfo = this.provideSlopeInfoFor(true, leftSideXIndex, yIndex + 1, collisionLayer, tempGameplayBounds, tempVelocity);
					SlopeInfo belowLeftCellInfo = this.provideSlopeInfoFor(true, leftSideXIndex, yIndex - 1, collisionLayer, tempGameplayBounds, tempVelocity);

					if (aboveLeftCellInfo != null) {
						pointOfReturn = aboveLeftCellInfo.pointOfReturn;
						slopeSide = aboveLeftCellInfo.slopeSide;
						collisionY = true;
					}
					else if (belowLeftCellInfo != null) {
						pointOfReturn = belowLeftCellInfo.pointOfReturn;
						slopeSide = belowLeftCellInfo.slopeSide;
						collisionY = true;
					}
				}
				
			}
			else if (this.checkSlopes()) {
				if (rightCellInfo != null) {
					pointOfReturn = rightCellInfo.pointOfReturn;
					slopeSide = rightCellInfo.slopeSide;
					collisionY = true;
				}
				else if (leftCellInfo != null) {
					pointOfReturn = leftCellInfo.pointOfReturn;
					slopeSide = leftCellInfo.slopeSide;
					collisionY = true;
				}
				else if (this.slopeSide.equals(SlopeSide.RIGHT)) {
					SlopeInfo aboveRightCellInfo = this.provideSlopeInfoFor(false, rightSideXIndex, yIndex + 1, collisionLayer, tempGameplayBounds, tempVelocity);
					SlopeInfo belowRightCellInfo = this.provideSlopeInfoFor(false, rightSideXIndex, yIndex - 1, collisionLayer, tempGameplayBounds, tempVelocity);

					if (aboveRightCellInfo != null) {
						pointOfReturn = aboveRightCellInfo.pointOfReturn;
						slopeSide = aboveRightCellInfo.slopeSide;
						collisionY = true;
					}
					else if (belowRightCellInfo != null) {
						pointOfReturn = belowRightCellInfo.pointOfReturn;
						slopeSide = belowRightCellInfo.slopeSide;
						collisionY = true;
					}
				}
				else if (this.slopeSide.equals(SlopeSide.LEFT)) {
					SlopeInfo aboveLeftCellInfo = this.provideSlopeInfoFor(true, leftSideXIndex, yIndex + 1, collisionLayer, tempGameplayBounds, tempVelocity);
					SlopeInfo belowLeftCellInfo = this.provideSlopeInfoFor(true, leftSideXIndex, yIndex - 1, collisionLayer, tempGameplayBounds, tempVelocity);

					if (aboveLeftCellInfo != null) {
						pointOfReturn = aboveLeftCellInfo.pointOfReturn;
						slopeSide = aboveLeftCellInfo.slopeSide;
						collisionY = true;
					}
					else if (belowLeftCellInfo != null) {
						pointOfReturn = belowLeftCellInfo.pointOfReturn;
						slopeSide = belowLeftCellInfo.slopeSide;
						collisionY = true;
					}
				}
			}
//				if (leftSideCell.getTile().getProperties().containsKey(EntityModel.LeftSlopeHeight)) {
//					//figure out how far up/down
//					float leftSlope = (Float) leftSideCell.getTile().getProperties().get(EntityModel.LeftSlopeHeight);
//					float rightSlope = (Float) leftSideCell.getTile().getProperties().get(EntityModel.RightSlopeHeight);
//					
//					float differenceInSlope = Math.max(leftSlope, rightSlope) - Math.min(leftSlope, rightSlope);
//					float howFarEntityIsInSlopeTile = (tempGameplayBounds.x) % collisionLayer.getTileWidth();
//					float verticalEntityDistance = 0f;
//					if (rightSlope > leftSlope) {
//						float proportionOfDistance = howFarEntityIsInSlopeTile / collisionLayer.getTileWidth();
//						verticalEntityDistance = differenceInSlope * proportionOfDistance + yIndex * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//					}
//					else {
//						float proportionOfDistance = 1f - (howFarEntityIsInSlopeTile / collisionLayer.getTileWidth());
//						verticalEntityDistance = differenceInSlope * proportionOfDistance + yIndex * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//					}
//
//					pointOfReturn = verticalEntityDistance;
//					this.slopeSide = SlopeSide.LEFT;
//					collisionY = true;
//
//				}
//				else if (rightSideCell.getTile().getProperties().containsKey(EntityModel.LeftSlopeHeight)) {
//					//figure out how far up/down
//					float leftSlope = (Float) rightSideCell.getTile().getProperties().get(EntityModel.LeftSlopeHeight);
//					float rightSlope = (Float) rightSideCell.getTile().getProperties().get(EntityModel.RightSlopeHeight);
//					
//					float differenceInSlope = Math.max(leftSlope, rightSlope) - Math.min(leftSlope, rightSlope);
//					float howFarEntityIsInSlopeTile = (tempGameplayBounds.x + tempGameplayBounds.width) % collisionLayer.getTileWidth();
//					float verticalEntityDistance = 0f;
//					if (rightSlope > leftSlope) {
//						float proportionOfDistance = howFarEntityIsInSlopeTile / collisionLayer.getTileWidth();
//						verticalEntityDistance = differenceInSlope * proportionOfDistance + yIndex * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//					}
//					else {
//						float proportionOfDistance = 1f - (howFarEntityIsInSlopeTile / collisionLayer.getTileWidth());
//						verticalEntityDistance = differenceInSlope * proportionOfDistance + yIndex * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//					}
//					
//					pointOfReturn = verticalEntityDistance;
//					collisionY = true;
//					this.slopeSide = SlopeSide.RIGHT;
//				}
//				else if (!this.slopeSide.equals(SlopeSide.NAN)) {
//					if (this.slopeSide.equals(SlopeSide.RIGHT)) {
//						Cell aboveRightSideCell = collisionLayer.getCell(rightSideXIndex, yIndex + 1);
//						Cell belowRightSideCell = collisionLayer.getCell(rightSideXIndex, yIndex - 1);
//						
//
//						if (aboveRightSideCell.getTile().getProperties().containsKey(EntityModel.LeftSlopeHeight)) {
//							//figure out how far up/down
//							float leftSlope = (Float) aboveRightSideCell.getTile().getProperties().get(EntityModel.LeftSlopeHeight);
//							float rightSlope = (Float) aboveRightSideCell.getTile().getProperties().get(EntityModel.RightSlopeHeight);
//							
//							float differenceInSlope = Math.max(leftSlope, rightSlope) - Math.min(leftSlope, rightSlope);
//							float howFarEntityIsInSlopeTile = (tempGameplayBounds.x + tempGameplayBounds.width) % collisionLayer.getTileWidth();
//							float verticalEntityDistance = 0f;
//							if (rightSlope > leftSlope) {
//								float proportionOfDistance = howFarEntityIsInSlopeTile / collisionLayer.getTileWidth();
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex + 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							else {
//								float proportionOfDistance = 1f - (howFarEntityIsInSlopeTile / collisionLayer.getTileWidth());
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex + 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							
//							pointOfReturn = verticalEntityDistance;
//							collisionY = true;
//							this.slopeSide = SlopeSide.RIGHT;
//						}
//						else if (belowRightSideCell.getTile().getProperties().containsKey(EntityModel.LeftSlopeHeight)) {
//							//figure out how far up/down
//							float leftSlope = (Float) belowRightSideCell.getTile().getProperties().get(EntityModel.LeftSlopeHeight);
//							float rightSlope = (Float) belowRightSideCell.getTile().getProperties().get(EntityModel.RightSlopeHeight);
//							
//							float differenceInSlope = Math.max(leftSlope, rightSlope) - Math.min(leftSlope, rightSlope);
//							float howFarEntityIsInSlopeTile = (tempGameplayBounds.x + tempGameplayBounds.width) % collisionLayer.getTileWidth();
//							float verticalEntityDistance = 0f;
//							if (rightSlope > leftSlope) {
//								float proportionOfDistance = howFarEntityIsInSlopeTile / collisionLayer.getTileWidth();
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex - 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							else {
//								float proportionOfDistance = 1f - (howFarEntityIsInSlopeTile / collisionLayer.getTileWidth());
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex - 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							
//							pointOfReturn = verticalEntityDistance;
//							collisionY = true;
//							this.slopeSide = SlopeSide.RIGHT;
//						}
//					}
//					else if (this.slopeSide.equals(SlopeSide.LEFT)) {
//						Cell aboveLeftSideCell = collisionLayer.getCell(leftSideXIndex, yIndex + 1);
//						Cell belowLeftSideCell = collisionLayer.getCell(leftSideXIndex, yIndex - 1);
//
//						if (aboveLeftSideCell.getTile().getProperties().containsKey(EntityModel.LeftSlopeHeight)) {
//							//figure out how far up/down
//							float leftSlope = (Float) aboveLeftSideCell.getTile().getProperties().get(EntityModel.LeftSlopeHeight);
//							float rightSlope = (Float) aboveLeftSideCell.getTile().getProperties().get(EntityModel.RightSlopeHeight);
//							
//							float differenceInSlope = Math.max(leftSlope, rightSlope) - Math.min(leftSlope, rightSlope);
//							float howFarEntityIsInSlopeTile = (tempGameplayBounds.x + tempGameplayBounds.width) % collisionLayer.getTileWidth();
//							float verticalEntityDistance = 0f;
//							if (rightSlope > leftSlope) {
//								float proportionOfDistance = howFarEntityIsInSlopeTile / collisionLayer.getTileWidth();
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex + 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							else {
//								float proportionOfDistance = 1f - (howFarEntityIsInSlopeTile / collisionLayer.getTileWidth());
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex + 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							
//							pointOfReturn = verticalEntityDistance;
//							collisionY = true;
//							this.slopeSide = SlopeSide.LEFT;
//						}
//						else if (belowLeftSideCell.getTile().getProperties().containsKey(EntityModel.LeftSlopeHeight)) {
//							//figure out how far up/down
//							float leftSlope = (Float) belowLeftSideCell.getTile().getProperties().get(EntityModel.LeftSlopeHeight);
//							float rightSlope = (Float) belowLeftSideCell.getTile().getProperties().get(EntityModel.RightSlopeHeight);
//							
//							float differenceInSlope = Math.max(leftSlope, rightSlope) - Math.min(leftSlope, rightSlope);
//							float howFarEntityIsInSlopeTile = (tempGameplayBounds.x + tempGameplayBounds.width) % collisionLayer.getTileWidth();
//							float verticalEntityDistance = 0f;
//							if (rightSlope > leftSlope) {
//								float proportionOfDistance = howFarEntityIsInSlopeTile / collisionLayer.getTileWidth();
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex - 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							else {
//								float proportionOfDistance = 1f - (howFarEntityIsInSlopeTile / collisionLayer.getTileWidth());
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex - 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							
//							pointOfReturn = verticalEntityDistance;
//							collisionY = true;
//							this.slopeSide = SlopeSide.LEFT;
//						}
//					}
//				}
//			}
//			else if (this.checkSlopes()){
//				if (rightSideCell.getTile().getProperties().containsKey(EntityModel.LeftSlopeHeight)) {
//					//figure out how far up/down
//					float leftSlope = (Float) rightSideCell.getTile().getProperties().get(EntityModel.LeftSlopeHeight);
//					float rightSlope = (Float) rightSideCell.getTile().getProperties().get(EntityModel.RightSlopeHeight);
//					
//					float differenceInSlope = Math.max(leftSlope, rightSlope) - Math.min(leftSlope, rightSlope);
//					float howFarEntityIsInSlopeTile = (tempGameplayBounds.x + tempGameplayBounds.width) % collisionLayer.getTileWidth();
//					float verticalEntityDistance = 0f;
//					if (rightSlope > leftSlope) {
//						float proportionOfDistance = howFarEntityIsInSlopeTile / collisionLayer.getTileWidth();
//						verticalEntityDistance = differenceInSlope * proportionOfDistance + yIndex * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//					}
//					else {
//						float proportionOfDistance = 1f - (howFarEntityIsInSlopeTile / collisionLayer.getTileWidth());
//						verticalEntityDistance = differenceInSlope * proportionOfDistance + yIndex * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//					}
//					
//					pointOfReturn = verticalEntityDistance;
//					collisionY = true;
//					this.slopeSide = SlopeSide.RIGHT;
//				}
//				else if (leftSideCell.getTile().getProperties().containsKey(EntityModel.LeftSlopeHeight)) {
//					//figure out how far up/down
//					float leftSlope = (Float) leftSideCell.getTile().getProperties().get(EntityModel.LeftSlopeHeight);
//					float rightSlope = (Float) leftSideCell.getTile().getProperties().get(EntityModel.RightSlopeHeight);
//					
//					float differenceInSlope = Math.max(leftSlope, rightSlope) - Math.min(leftSlope, rightSlope);
//					float howFarEntityIsInSlopeTile = (tempGameplayBounds.x) % collisionLayer.getTileWidth();
//					float verticalEntityDistance = 0f;
//					if (rightSlope > leftSlope) {
//						float proportionOfDistance = howFarEntityIsInSlopeTile / collisionLayer.getTileWidth();
//						verticalEntityDistance = differenceInSlope * proportionOfDistance + yIndex * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//					}
//					else {
//						float proportionOfDistance = 1f - (howFarEntityIsInSlopeTile / collisionLayer.getTileWidth());
//						verticalEntityDistance = differenceInSlope * proportionOfDistance + yIndex * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//					}
//
//					pointOfReturn = verticalEntityDistance;
//					collisionY = true;
//					this.slopeSide = SlopeSide.LEFT;
//
//				}
//				else if (!this.slopeSide.equals(SlopeSide.NAN)) {
//					if (this.slopeSide.equals(SlopeSide.RIGHT)) {
//						Cell aboveRightSideCell = collisionLayer.getCell(rightSideXIndex, yIndex + 1);
//						Cell belowRightSideCell = collisionLayer.getCell(rightSideXIndex, yIndex - 1);
//						
//
//						if (aboveRightSideCell.getTile().getProperties().containsKey(EntityModel.LeftSlopeHeight)) {
//							System.out.println("SlopeARight");
//							//figure out how far up/down
//							float leftSlope = (Float) aboveRightSideCell.getTile().getProperties().get(EntityModel.LeftSlopeHeight);
//							float rightSlope = (Float) aboveRightSideCell.getTile().getProperties().get(EntityModel.RightSlopeHeight);
//							
//							float differenceInSlope = Math.max(leftSlope, rightSlope) - Math.min(leftSlope, rightSlope);
//							float howFarEntityIsInSlopeTile = (tempGameplayBounds.x + tempGameplayBounds.width) % collisionLayer.getTileWidth();
//							float verticalEntityDistance = 0f;
//							if (rightSlope > leftSlope) {
//								float proportionOfDistance = howFarEntityIsInSlopeTile / collisionLayer.getTileWidth();
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex + 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							else {
//								float proportionOfDistance = 1f - (howFarEntityIsInSlopeTile / collisionLayer.getTileWidth());
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex + 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							
//							pointOfReturn = verticalEntityDistance;
//							collisionY = true;
//							this.slopeSide = SlopeSide.RIGHT;
//						}
//						else if (belowRightSideCell.getTile().getProperties().containsKey(EntityModel.LeftSlopeHeight)) {
//							System.out.println("SlopeBRight");
//							//figure out how far up/down
//							float leftSlope = (Float) belowRightSideCell.getTile().getProperties().get(EntityModel.LeftSlopeHeight);
//							float rightSlope = (Float) belowRightSideCell.getTile().getProperties().get(EntityModel.RightSlopeHeight);
//							
//							float differenceInSlope = Math.max(leftSlope, rightSlope) - Math.min(leftSlope, rightSlope);
//							float howFarEntityIsInSlopeTile = (tempGameplayBounds.x + tempGameplayBounds.width) % collisionLayer.getTileWidth();
//							float verticalEntityDistance = 0f;
//							if (rightSlope > leftSlope) {
//								float proportionOfDistance = howFarEntityIsInSlopeTile / collisionLayer.getTileWidth();
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex - 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							else {
//								float proportionOfDistance = 1f - (howFarEntityIsInSlopeTile / collisionLayer.getTileWidth());
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex - 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							
//							pointOfReturn = verticalEntityDistance;
//							collisionY = true;
//							this.slopeSide = SlopeSide.RIGHT;
//						}
//					}
//					else if (this.slopeSide.equals(SlopeSide.LEFT)) {
//						Cell aboveLeftSideCell = collisionLayer.getCell(leftSideXIndex, yIndex + 1);
//						Cell belowLeftSideCell = collisionLayer.getCell(leftSideXIndex, yIndex - 1);
//
//						if (aboveLeftSideCell.getTile().getProperties().containsKey(EntityModel.LeftSlopeHeight)) {
//							//figure out how far up/down
//							float leftSlope = (Float) aboveLeftSideCell.getTile().getProperties().get(EntityModel.LeftSlopeHeight);
//							float rightSlope = (Float) aboveLeftSideCell.getTile().getProperties().get(EntityModel.RightSlopeHeight);
//							
//							float differenceInSlope = Math.max(leftSlope, rightSlope) - Math.min(leftSlope, rightSlope);
//							float howFarEntityIsInSlopeTile = (tempGameplayBounds.x + tempGameplayBounds.width) % collisionLayer.getTileWidth();
//							float verticalEntityDistance = 0f;
//							if (rightSlope > leftSlope) {
//								float proportionOfDistance = howFarEntityIsInSlopeTile / collisionLayer.getTileWidth();
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex + 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							else {
//								float proportionOfDistance = 1f - (howFarEntityIsInSlopeTile / collisionLayer.getTileWidth());
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex + 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							
//							pointOfReturn = verticalEntityDistance;
//							collisionY = true;
//							this.slopeSide = SlopeSide.LEFT;
//						}
//						else if (belowLeftSideCell.getTile().getProperties().containsKey(EntityModel.LeftSlopeHeight)) {
//							//figure out how far up/down
//							float leftSlope = (Float) belowLeftSideCell.getTile().getProperties().get(EntityModel.LeftSlopeHeight);
//							float rightSlope = (Float) belowLeftSideCell.getTile().getProperties().get(EntityModel.RightSlopeHeight);
//							
//							float differenceInSlope = Math.max(leftSlope, rightSlope) - Math.min(leftSlope, rightSlope);
//							float howFarEntityIsInSlopeTile = (tempGameplayBounds.x + tempGameplayBounds.width) % collisionLayer.getTileWidth();
//							float verticalEntityDistance = 0f;
//							if (rightSlope > leftSlope) {
//								float proportionOfDistance = howFarEntityIsInSlopeTile / collisionLayer.getTileWidth();
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex - 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							else {
//								float proportionOfDistance = 1f - (howFarEntityIsInSlopeTile / collisionLayer.getTileWidth());
//								verticalEntityDistance = differenceInSlope * proportionOfDistance + (yIndex - 1) * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
//							}
//							
//							pointOfReturn = verticalEntityDistance;
//							collisionY = true;
//							this.slopeSide = SlopeSide.LEFT;
//						}
//					}
//				}
//			}
			if (!collisionY) {
				//Tile
				if (!this.slopeSide.equals(SlopeSide.NAN)) {
					this.slopeSide = SlopeSide.NAN;
					System.out.println("Slope");
				}
				else {
					CollisionInfo info = this.singletonYCheckForTileCollision(collisionLayer, tempGameplayBounds, true, tempVelocity);
					collisionY = info.didCollide;
					pointOfReturn = info.pointOfReturn;
				}
			}

			if (collisionY) {
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
			this.gameplayHitBox.y = tempGameplayBounds.y;
			this.imageHitBox.y = tempImageBounds.y;
			this.velocity.y = tempVelocity;
		}
		else if (shouldMove && collisionType.equals(CollisionType.World)) {
			this.gameplayHitBox.y = pointOfReturn;
			this.refreshImageHitBoxY();
		}
		else if (shouldMove && collisionType.equals(CollisionType.Entity))
		{
			//Figure out reposition AND MAKE SURE IT DOESN'T HAVE TILE/OBJECT COLLISION ISSUE. If it does, just do
			//the stop movement thing.
			Rectangle sourceHitBox = new Rectangle(this.gameplayHitBox);
			Rectangle collidedEntityHitBox = new Rectangle(entityCollisionData.collidedEntity.gameplayHitBox);
			switch(entityCollisionData.collisionLocation) {
			case Left:
				sourceHitBox.x = collidedEntityHitBox.x - sourceHitBox.width - 2f; //buffer.
				CollisionInfo xCheckInfo = this.singletonXCheckWithoutVelocity(collisionLayer, sourceHitBox);
				CollisionInfo yCheckInfo = this.singletonYCheckWithoutVelocity(collisionLayer, sourceHitBox);
				boolean objectCheck = this.collisionChecker.checkIfEntityCollidesWithObjects(this, sourceHitBox);
				//May need to reenable this....
//				EntityCollisionData extraEntityCheck = this.collisionChecker.checkIfEntityCollidesWithOthers(this, sourceHitBox);
				if (!xCheckInfo.didCollide && !yCheckInfo.didCollide && !objectCheck && entityCollisionRepositionTokens > 0) {
					this.gameplayHitBox.x = sourceHitBox.x;
					this.refreshImageHitBoxX();
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
					this.gameplayHitBox.x = sourceHitBox.x;
					this.refreshImageHitBoxX();
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
	
	public Polygon getGameplayHitBoxInPolygon() {
		Polygon polygon = new Polygon(new float[] {
			this.gameplayHitBox.x, this.gameplayHitBox.y,
			this.gameplayHitBox.x, this.gameplayHitBox.y + this.gameplayHitBox.height,
			this.gameplayHitBox.x + this.gameplayHitBox.width, this.gameplayHitBox.y + this.gameplayHitBox.height,
			this.gameplayHitBox.x + this.gameplayHitBox.width, this.gameplayHitBox.y
		});
		polygon.setOrigin(this.gameplayHitBox.x + this.gameplayHitBox.width / 2, this.gameplayHitBox.y + this.gameplayHitBox.height / 2);
		polygon.setRotation(this.getVelocityAngle());
		return polygon;
	}

	
	public CollisionCheck checkForXCollision(float maxTime, TiledMapTileLayer collisionLayer, float xVelocity, float xAcceleration, boolean shouldMove) {
		
		boolean collisionX = false;
		float pointOfReturn = 0f;
		float time = 0f;
		
		Rectangle tempImageBounds = new Rectangle(this.imageHitBox);
		Rectangle tempGameplayBounds = new Rectangle(this.gameplayHitBox);
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
			if (this.slopeSide.equals(SlopeSide.NAN)) {
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
			this.gameplayHitBox.x = tempGameplayBounds.x;
			this.imageHitBox.x = tempImageBounds.x;
			this.velocity.x = tempVelocity;
		}
		else if (shouldMove && collisionType.equals(CollisionType.World)) {
			this.gameplayHitBox.x = pointOfReturn;
			this.refreshImageHitBoxX();
		}
		else if (shouldMove && collisionType.equals(CollisionType.Entity))
		{
			//Figure out reposition AND MAKE SURE IT DOESN'T HAVE TILE/OBJECT COLLISION ISSUE. If it does, just do
			//the stop movement thing.
			Rectangle sourceHitBox = new Rectangle(this.gameplayHitBox);
			Rectangle collidedEntityHitBox = new Rectangle(entityCollisionData.collidedEntity.gameplayHitBox);
			switch(entityCollisionData.collisionLocation) {
			case Left:
				sourceHitBox.x = collidedEntityHitBox.x - sourceHitBox.width - 2f; //buffer.
				CollisionInfo xCheckInfo = this.singletonXCheckWithoutVelocity(collisionLayer, sourceHitBox);
				CollisionInfo yCheckInfo = this.singletonYCheckWithoutVelocity(collisionLayer, sourceHitBox);
				boolean objectCheck = this.collisionChecker.checkIfEntityCollidesWithObjects(this, sourceHitBox);
				//May need to reenable this....
//				EntityCollisionData extraEntityCheck = this.collisionChecker.checkIfEntityCollidesWithOthers(this, sourceHitBox);
				if (!xCheckInfo.didCollide && !yCheckInfo.didCollide && !objectCheck && entityCollisionRepositionTokens > 0) {
					this.gameplayHitBox.x = sourceHitBox.x;
					this.refreshImageHitBoxX();
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
					this.gameplayHitBox.x = sourceHitBox.x;
					this.refreshImageHitBoxX();
					entityCollisionRepositionTokens -= 1;
				}
				break;
			}
		}

		return new CollisionCheck(collisionX, tempVelocity > 0, time, collisionType, pointOfReturn);

	}
	
	public SlopeInfo provideSlopeInfoFor(boolean isLeft, int xIndex, int yIndex, TiledMapTileLayer collisionLayer, Rectangle tempGameplayBounds, float yVelocity) {
		//figure out how far up/down
		Cell tile = collisionLayer.getCell(xIndex, yIndex);
		if (tile != null && tile.getTile().getProperties().containsKey(EntityModel.LeftSlopeHeight) && yVelocity <= 0) {
			float leftSlope = (Float) tile.getTile().getProperties().get(EntityModel.LeftSlopeHeight);
			float rightSlope = (Float) tile.getTile().getProperties().get(EntityModel.RightSlopeHeight);
			
			float differenceInSlope = Math.max(leftSlope, rightSlope) - Math.min(leftSlope, rightSlope);
			float howFarEntityIsInSlopeTile = (isLeft ? tempGameplayBounds.x : (tempGameplayBounds.x + tempGameplayBounds.width)) % collisionLayer.getTileWidth();
			float proportionOfDistance = 0f;
			if (rightSlope > leftSlope) {
				proportionOfDistance = howFarEntityIsInSlopeTile / collisionLayer.getTileWidth();
			}
			else {
				proportionOfDistance = 1f - (howFarEntityIsInSlopeTile / collisionLayer.getTileWidth());
			}
		    float verticalEntityDistance = differenceInSlope * proportionOfDistance + yIndex * collisionLayer.getTileHeight() + Math.min(leftSlope, rightSlope);
		    
			return new SlopeInfo(isLeft ? SlopeSide.LEFT : SlopeSide.RIGHT, verticalEntityDistance);
		}
		return null;
	}
	
	public boolean checkSlopes() {
		return true;
	}
	
	public void refreshImageHitBoxX() {
		this.imageHitBox.x = -1f * (getXOffsetModifier() + (this.imageHitBox.width * ((1f - this.widthCoefficient) / 2)) - this.gameplayHitBox.x );
	}
	
	public void refreshImageHitBoxY() {
		this.imageHitBox.y = -1f * (this.yOffsetModifier + (this.imageHitBox.height * ((1f - this.heightCoefficient) / 2)) - this.gameplayHitBox.y );
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
	
	public Vector2 getVelocity() {
		return velocity;
	}

	public Vector2 getAcceleration() {
		return acceleration;
	}

	public Rectangle getGameplayHitBox() {
		return gameplayHitBox;
	}

	public Rectangle getImageHitBox() {
		return imageHitBox;
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
