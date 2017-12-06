package com.mygdx.game.model.characters;

import com.badlogic.gdx.math.Rectangle;

public class EntityCollisionData {
	RepositionTo collisionLocation;
	EntityModel source;
	EntityModel collidedEntity;
	
	public enum RepositionTo {
		Left, Right
	}
	
	public EntityCollisionData(EntityModel source, EntityModel collidedEntity, Rectangle newSourceHitBox) {
		this.source = source;
		this.collidedEntity = collidedEntity;
		
		Rectangle oldSourceHitBox = source.getGameplayCollisionBox();
		Rectangle collidedEntityHitBox = collidedEntity.getGameplayCollisionBox();
		
//		//Below
//		if (oldSourceHitBox.y + oldSourceHitBox.height < collidedEntityHitBox.y) {
//			float distanceToLeft = Math.abs(collidedEntityHitBox.x - (oldSourceHitBox.x + oldSourceHitBox.width / 2));
//			float distanceToRight = Math.abs((collidedEntityHitBox.x + collidedEntityHitBox.width) - (oldSourceHitBox.x + oldSourceHitBox.width / 2));
//			
//			//Closer to left
//			if (distanceToRight > distanceToLeft) {
////				newSourceHitBox.x = oldSourceHitBox.x - (newSourceHitBox.x + newSourceHitBox.width);
//				this.collisionLocation = RepositionTo.BelowLeft;
//			}
//			//Closer to right
//			else {
//				this.collisionLocation = RepositionTo.BelowRight;
//			}
//		}
		//Above
//		else if (oldSourceHitBox.y > collidedEntityHitBox.y + collidedEntityHitBox.height) {
//			float distanceToLeft = Math.abs(collidedEntityHitBox.x - (oldSourceHitBox.x + oldSourceHitBox.width / 2));
//			float distanceToRight = Math.abs((collidedEntityHitBox.x + collidedEntityHitBox.width) - (oldSourceHitBox.x + oldSourceHitBox.width / 2));
//			
//			//Closer to left
//			if (distanceToRight > distanceToLeft) {
//				this.collisionLocation = RepositionTo.AboveLeft;
//			}
//			//Closer to right
//			else {
//				this.collisionLocation = RepositionTo.AboveRight;
//			}
//		}
		//Inbetween
//		else {
		float distanceToLeft = Math.abs(collidedEntityHitBox.x - (oldSourceHitBox.x + oldSourceHitBox.width / 2));
		float distanceToRight = Math.abs((collidedEntityHitBox.x + collidedEntityHitBox.width) - (oldSourceHitBox.x + oldSourceHitBox.width / 2));
			
		//Closer to left
		if (distanceToRight > distanceToLeft) {
			this.collisionLocation = RepositionTo.Left;
		}
		//Closer to right
		else {
			this.collisionLocation = RepositionTo.Right;
		}
//		}
	}
}
