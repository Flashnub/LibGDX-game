package com.mygdx.game.model.characters;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.model.characters.CollisionCheck.CollisionType;
import com.mygdx.game.model.events.CollisionChecker;
import com.mygdx.game.utils.CellWrapper;

public abstract class EntityModel {
	
	public static final String activeState = "Active";
	public static final String windupState = "Windup";
	public static final String cooldownState = "Cooldown";
	public static final float averageOverlapCooldown = 2f;
	
	public Vector2 velocity, acceleration;
	public Rectangle gameplayHitBox;
	public Rectangle imageHitBox;
	public float widthCoefficient;
	public float heightCoefficient;
	public float timeSinceOverlapCorrection;
	float xOffsetModifier;
	float yOffsetModifier;
	public int allegiance;
	CollisionChecker collisionChecker;
	boolean respectEntityCollision;
	boolean hasProcessedOverlapCorrection;
	
	
	public EntityModel() {
		super();
		this.velocity = new Vector2();
		this.acceleration = new Vector2();
		this.imageHitBox = new Rectangle();
		this.gameplayHitBox = new Rectangle();
		this.allegiance = 0;
		this.timeSinceOverlapCorrection = 0f;
		respectEntityCollision = true;
		this.hasProcessedOverlapCorrection = false;
		
		this.widthCoefficient = 1f;
		this.heightCoefficient = 1f;
		this.xOffsetModifier = 0f;
		this.yOffsetModifier = 0f;
	}
	
	//Not used by projectiles.
	public void setGameplaySize(float delta) {	
		this.gameplayHitBox.width = this.getImageHitBox().width * widthCoefficient;
		this.gameplayHitBox.height = this.getImageHitBox().height * heightCoefficient;
	}	
	
	public void handleOverlapCooldown(float delta) {
		if (this.hasProcessedOverlapCorrection)
		{
			this.timeSinceOverlapCorrection += delta;
			if (this.timeSinceOverlapCorrection > EntityModel.averageOverlapCooldown) {
				this.hasProcessedOverlapCorrection = false;
				this.timeSinceOverlapCorrection = 0f;
			}
		}
	}
	
	public void moveWithoutCollisionDetection(float delta) {
		this.imageHitBox.x = this.imageHitBox.x + this.velocity.x * delta;
		this.imageHitBox.y = this.imageHitBox.y + this.velocity.y * delta;
		this.gameplayHitBox.x = this.xOffsetModifier + (this.imageHitBox.x + this.imageHitBox.width * ((1f - this.widthCoefficient) / 2));
		this.gameplayHitBox.y = this.yOffsetModifier + (this.imageHitBox.y + this.imageHitBox.height * ((1f - this.heightCoefficient) / 2));
	}
	
	public CollisionCheck checkForYCollision(float maxTime, TiledMapTileLayer collisionLayer, float yVelocity, boolean shouldMove, boolean applyGravity) {
		float tileWidth = collisionLayer.getTileWidth();
		float tileHeight = collisionLayer.getTileHeight();
		
		boolean collisionY = false;
		float time = 0f;
		
		Rectangle tempImageBounds = new Rectangle(this.imageHitBox);
		Rectangle tempGameplayBounds = new Rectangle(this.gameplayHitBox);
		Array <CellWrapper> tilesToCheckForWorldObjects = new Array<CellWrapper>();
		float tempVelocity = yVelocity;
		float tempMaxTime = maxTime;
		float increment;
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
			
			tempImageBounds.setY(tempImageBounds.getY() + tempVelocity * increment);
			tempGameplayBounds.setY(this.yOffsetModifier + tempImageBounds.getY() + tempImageBounds.getHeight() * ((1f - this.heightCoefficient) / 2));

			

			if (tempVelocity < 0) {
				int bottomLeftXIndex = (int) (tempGameplayBounds.x / tileWidth);
				int bottomLeftYIndex = (int) ((tempGameplayBounds.y) / tileHeight);
				
				Cell bottomLeftBlock = collisionLayer.getCell(bottomLeftXIndex, bottomLeftYIndex);
				
				int bottomMiddleXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width / 2) / tileWidth);
				int bottomMiddleYIndex = (int) (tempGameplayBounds.y / tileHeight);
				
				Cell bottomMiddleBlock = collisionLayer.getCell(bottomMiddleXIndex, bottomMiddleYIndex);
				
				int bottomRightXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth);
				int bottomRightYIndex = (int) (tempGameplayBounds.y / tileHeight);
				
				Cell bottomRightBlock = collisionLayer.getCell(bottomRightXIndex, bottomRightYIndex);
				//bottom left block
				if (bottomLeftBlock != null)
					collisionY = ((Boolean)bottomLeftBlock.getTile().getProperties().get("Impassable")).equals(true);
					
				//bottom middle block
				if(!collisionY && bottomMiddleBlock != null)
					collisionY = ((Boolean)bottomMiddleBlock.getTile().getProperties().get("Impassable")).equals(true);
				
				//bottom right block
				if(!collisionY && bottomRightBlock != null)
					collisionY = ((Boolean)bottomRightBlock.getTile().getProperties().get("Impassable")).equals(true);
				
				tilesToCheckForWorldObjects.add(new CellWrapper(bottomLeftBlock, new Vector2(bottomLeftXIndex * tileWidth, bottomLeftYIndex * tileHeight)));
				tilesToCheckForWorldObjects.add(new CellWrapper(bottomMiddleBlock, new Vector2(bottomMiddleXIndex * tileWidth, bottomMiddleYIndex * tileHeight)));
				tilesToCheckForWorldObjects.add(new CellWrapper(bottomRightBlock, new Vector2(bottomRightXIndex * tileWidth, bottomRightYIndex * tileHeight)));
		
			} 
			else if (tempVelocity > 0) {
				
				int topLeftXIndex = (int) (tempGameplayBounds.x / tileWidth);
				int topLeftYIndex = (int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight);
				
				Cell topLeftBlock = collisionLayer.getCell(topLeftXIndex, topLeftYIndex);
				
				int topMiddleXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width / 2) / tileWidth);
				int topMiddleYIndex = (int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight);
				
				Cell topMiddleBlock = collisionLayer.getCell(topMiddleXIndex, topMiddleYIndex);
				
				int topRightXIndex = (int) ((tempGameplayBounds.x + tempGameplayBounds.width) / tileWidth);
				int topRightYIndex = (int) ((tempGameplayBounds.y + tempGameplayBounds.height) / tileHeight);
				
				Cell topRightBlock = collisionLayer.getCell(topRightXIndex, topRightYIndex);
				
				//top left block
				if (topLeftBlock != null)
					collisionY = ((Boolean)topLeftBlock.getTile().getProperties().get("Impassable")).equals(true);
				
				//top middle block
				if(!collisionY && topMiddleBlock != null)
					collisionY = ((Boolean)topMiddleBlock.getTile().getProperties().get("Impassable")).equals(true);
				
				//top right block
				if(!collisionY && topRightBlock != null)
					collisionY = ((Boolean)topRightBlock.getTile().getProperties().get("Impassable")).equals(true);
				
				tilesToCheckForWorldObjects.add(new CellWrapper(topLeftBlock, new Vector2(topLeftXIndex * tileWidth, topLeftYIndex * tileHeight)));
				tilesToCheckForWorldObjects.add(new CellWrapper(topMiddleBlock, new Vector2(topMiddleXIndex * tileWidth, topMiddleYIndex * tileHeight)));
				tilesToCheckForWorldObjects.add(new CellWrapper(topRightBlock, new Vector2(topRightXIndex * tileWidth, topRightYIndex * tileHeight)));
			}
			if (collisionY) {
				collisionType = CollisionType.World;
			}
			
			collisionY = collisionY	|| this.collisionChecker.checkIfEntityCollidesWithObjects(this, tempGameplayBounds);
			if (collisionY && collisionType.equals(CollisionType.None)) {
				collisionType = CollisionType.Object;
			}
			
			boolean additionalCollisionY = this.handleAdditionalYCollisionLogic(tempGameplayBounds, tempImageBounds, collisionY);
			if (!collisionY && additionalCollisionY) {
				collisionY = true;
				collisionType = CollisionType.Entity;
			}

			
			if (collisionY)
			{
				break;
			}
			else {
				tilesToCheckForWorldObjects.clear();
			}
			
			time += increment;
			tempMaxTime -= increment;
		}

		if (shouldMove && !collisionY) {
			this.gameplayHitBox.y = tempGameplayBounds.y;
			this.imageHitBox.y = tempImageBounds.y;
			this.velocity.y = tempVelocity;
		}

		//react to X collision
		return new CollisionCheck(collisionY, time, collisionType);
	}
	
	public abstract boolean handleAdditionalXCollisionLogic(Rectangle tempGameplayBounds, Rectangle tempImageBounds, boolean alreadyCollided);
	public abstract boolean handleAdditionalYCollisionLogic(Rectangle tempGameplayBounds, Rectangle tempImageBounds, boolean alreadyCollided);

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
		float tileWidth = collisionLayer.getTileWidth();
		float tileHeight = collisionLayer.getTileHeight();
		
		boolean collisionX = false;
		float time = 0f;
		
		Rectangle tempImageBounds = new Rectangle(this.imageHitBox);
		Rectangle tempGameplayBounds = new Rectangle(this.gameplayHitBox);
		Array <CellWrapper> tilesToCheckForWorldObjects = new Array<CellWrapper>();
		float tempVelocity = xVelocity;
		float tempMaxTime = maxTime;
		float increment;
		CollisionType collisionType = CollisionType.None;
		
		while (tempMaxTime > 0f) {
			
			if (tempMaxTime > 1f / 400f) {
				increment = 1 / 400f;
			}
			else {
				increment = tempMaxTime;
			}
			
			tempVelocity += xAcceleration * increment;
			tempImageBounds.setX(tempImageBounds.getX() + tempVelocity * increment);
			tempGameplayBounds.setX(this.xOffsetModifier + tempImageBounds.getX() + tempImageBounds.getWidth() * ((1f - this.widthCoefficient) / 2));

			

			if (tempVelocity < 0) {
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
				
				if (topLeftBlock != null)
					collisionX = ((Boolean)topLeftBlock.getTile().getProperties().get("Impassable")).equals(true);
					
				
				//middle left block
				if(!collisionX && middleLeftBlock != null)
					collisionX = ((Boolean)middleLeftBlock.getTile().getProperties().get("Impassable")).equals(true);
				
				//lower left block
				if(!collisionX && lowerLeftBlock != null )
					collisionX = ((Boolean)lowerLeftBlock.getTile().getProperties().get("Impassable")).equals(true);
				
				tilesToCheckForWorldObjects.add(new CellWrapper(topLeftBlock, new Vector2(topLeftXIndex * tileWidth, topLeftYIndex * tileHeight)));
				tilesToCheckForWorldObjects.add(new CellWrapper(middleLeftBlock, new Vector2(middleLeftXIndex * tileWidth, middleLeftYIndex * tileHeight)));
				tilesToCheckForWorldObjects.add(new CellWrapper(lowerLeftBlock, new Vector2(lowerLeftXIndex * tileWidth, lowerLeftYIndex * tileHeight)));

			}
			else if (tempVelocity > 0) {
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
				
				// top right block
				if (topRightBlock != null)
					collisionX = ((Boolean)topRightBlock.getTile().getProperties().get("Impassable")).equals(true);
				
				//middle right block
				if(!collisionX && middleRightBlock != null)
					collisionX = ((Boolean)middleRightBlock.getTile().getProperties().get("Impassable")).equals(true);
				
				//lower right block
				if(!collisionX && lowerRightBlock != null)
					collisionX = ((Boolean)lowerRightBlock.getTile().getProperties().get("Impassable")).equals(true);
				
				tilesToCheckForWorldObjects.add(new CellWrapper(topRightBlock, new Vector2(topRightXIndex * tileWidth, topRightYIndex * tileHeight)));
				tilesToCheckForWorldObjects.add(new CellWrapper(middleRightBlock, new Vector2(middleRightXIndex * tileWidth, middleRightYIndex * tileHeight)));
				tilesToCheckForWorldObjects.add(new CellWrapper(lowerRightBlock, new Vector2(lowerRightXIndex * tileWidth, lowerRightYIndex * tileHeight)));
			}
			
			if (collisionX) {
				collisionType = CollisionType.World;
			}
			
			collisionX = collisionX 
					|| this.collisionChecker.checkIfEntityCollidesWithObjects(this, tempGameplayBounds);
			
			if (collisionX && collisionType.equals(CollisionType.None)) {
				collisionType = CollisionType.Object;
			}
			
			boolean additionalCollisionX = this.handleAdditionalXCollisionLogic(tempGameplayBounds, tempImageBounds, collisionX);
			
			if (!collisionX && additionalCollisionX) {
				collisionX = true;
				collisionType = CollisionType.Entity;
			}
			
			if (collisionX)
			{
				break;
			}
			else {
				tilesToCheckForWorldObjects.clear();
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
		//react to X collision
		return new CollisionCheck(collisionX, time, collisionType);
	}
	
	public void stopEntityOverlapIfNeeded(EntityModel entity, Rectangle tempGameplayBounds, Rectangle tempImageBounds) {
		if (entity != null && this.respectEntityCollision && entity.respectEntityCollision && this.gameplayHitBox.overlaps(entity.gameplayHitBox) && tempGameplayBounds.overlaps(entity.gameplayHitBox) && (this.velocity.x == 0 || entity.velocity.x == 0)) {
			if (tempGameplayBounds.x > entity.gameplayHitBox.x){
			
				float positionalDifference = tempGameplayBounds.x - entity.gameplayHitBox.x;
				if (positionalDifference + tempGameplayBounds.x + tempGameplayBounds.width > entity.gameplayHitBox.x + entity.gameplayHitBox.width) {
					this.imageHitBox.x += tempGameplayBounds.width;
				}
				else {
					this.imageHitBox.x += entity.gameplayHitBox.width;
				}
			}
			else {
				float positionalDifference = tempGameplayBounds.x - entity.gameplayHitBox.x;
				if (positionalDifference + tempGameplayBounds.x + tempGameplayBounds.width < entity.gameplayHitBox.x + entity.gameplayHitBox.width) {
					this.imageHitBox.x -= tempGameplayBounds.width;
				}
				else {
					this.imageHitBox.x -= entity.gameplayHitBox.width;
				}
			}
			this.gameplayHitBox.x = this.imageHitBox.x + this.imageHitBox.getWidth() * ((1f - this.widthCoefficient) / 2);		
			this.hasProcessedOverlapCorrection = true;
		}

	}
	
	public boolean isHitBoxModified() {
		return this.xOffsetModifier != 0 || this.yOffsetModifier != 0;
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
	
	public boolean respectEntityCollision() {
		return respectEntityCollision;
	}

	public void setRespectEntityCollision(boolean respectEntityCollision) {
		this.respectEntityCollision = respectEntityCollision;
	}

	public boolean hasProcessedOverlapCorrection() {
		return hasProcessedOverlapCorrection;
	}

	public void setWidthCoefficient(float widthCoefficient) {
		this.widthCoefficient = widthCoefficient;
	}

	public void setHeightCoefficient(float heightCoefficient) {
		this.heightCoefficient = heightCoefficient;
	}

	public float getxOffsetModifier() {
		return xOffsetModifier;
	}

	public void setxOffsetModifier(float xOffsetModifier) {
		this.xOffsetModifier = xOffsetModifier;
	}

	public float getyOffsetModifier() {
		return yOffsetModifier;
	}

	public void setyOffsetModifier(float yOffsetModifier) {
		this.yOffsetModifier = yOffsetModifier;
	}
}
