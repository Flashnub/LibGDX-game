//package com.mygdx.game.model.characters.enemies;
//
//import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
//import com.badlogic.gdx.math.Vector2;
//import com.mygdx.game.model.characters.EntityUIModel;
//import com.mygdx.game.model.projectiles.Projectile;
//
//public class BasicEnemy extends Enemy{
//	
//	float actionTimer = 4.8f;
//	
//	public static final String characterName = "BasicEnemy";
//
//	public BasicEnemy(int maxHealth, int maxWill, int attack) {
//		super(characterName);
//		setCharacterData(new BasicEnemyModel(characterName, this.getCharacterUIData()));
//		this.getCharacterData().setName(characterName);
//	}
//	
//	public class BasicEnemyModel extends EnemyModel {
//
//		public BasicEnemyModel(String characterName, EntityUIModel uiModel) {
//			super(characterName, uiModel);
//			this.setGravity(950f);
//			this.acceleration.y = -this.getGravity();
//			this.gameplayHitBoxWidthModifier = 0.19f;
//			this.gameplayHitBoxHeightModifier = 0.6f;
//		}
//
//		protected void executePattern(float delta) {
//			actionTimer += delta;
//			if (actionTimer > 4f) {
//				Projectile newProjectile = new Projectile(characterName,
//						new Vector2(this.getGameplayHitBox().x, this.getGameplayHitBox().y),
//						this.getPlayer(), this,
//						this.getProjListener());
//				newProjectile.setAllegiance(this.getAllegiance());
//				this.getProjListener().addProjectile(newProjectile);
//				actionTimer = 0f;
//			}
//		}
//
//		@Override
//		protected void manageAutomaticStates(float delta, TiledMapTileLayer collisionLayer) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		protected void landed() {
//			// TODO Auto-generated method stub
//			
//		}
//		
//		@Override
//		public void shouldProjectileHit(Projectile projectile) {
////			if (!isImmuneToInjury()) {
////				projectile.process(this);
////			}
////			getProjListener().deleteProjectile(projectile);
//			super.shouldProjectileHit(projectile);
//		}
//
//		@Override
//		public int getAllegiance() {
//			return 1;
//		}
//
//	}
//}
