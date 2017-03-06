package com.mygdx.game.model.characters;

import com.badlogic.gdx.math.Rectangle;

public class EntityCollisionData {
	EntityCollisionFrom collisionLocation;
	
	public enum EntityCollisionFrom {
		BelowLeft, BelowRight, AboveLeft, AboveRight, Left, Right
	}
	
	public EntityCollisionData(Rectangle oldSourceHitBox, Rectangle newSourceHitBox, Rectangle collidedEntityHitBox) {
		//Below
		if (oldSourceHitBox.y + oldSourceHitBox.height < collidedEntityHitBox.y) {
			float distanceToLeft = Math.abs(collidedEntityHitBox.x - (oldSourceHitBox.x + oldSourceHitBox.width / 2));
			float distanceToRight = Math.abs((collidedEntityHitBox.x + collidedEntityHitBox.width) - (oldSourceHitBox.x + oldSourceHitBox.width / 2));
			
			//Closer to left
			if (distanceToRight > distanceToLeft) {
				
			}
			//Closer to right
			else {
				
			}
		}
		//Above
		else if (oldSourceHitBox.y > collidedEntityHitBox.y + collidedEntityHitBox.height) {
			float distanceToLeft = Math.abs(collidedEntityHitBox.x - (oldSourceHitBox.x + oldSourceHitBox.width / 2));
			float distanceToRight = Math.abs((collidedEntityHitBox.x + collidedEntityHitBox.width) - (oldSourceHitBox.x + oldSourceHitBox.width / 2));
			
			//Closer to left
			if (distanceToRight > distanceToLeft) {
				
			}
			//Closer to right
			else {
				
			}
		}
		//Inbetween
		else {
			
		}
	}
}
