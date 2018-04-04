package com.mygdx.game.model.characters.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.model.characters.EntityCollisionData;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.characters.ModelListener;
import com.mygdx.game.model.characters.NPCCharacter;
import com.mygdx.game.model.characters.NPCProperties;
import com.mygdx.game.model.characters.player.Player;
import com.mygdx.game.model.world.WorldModel;

public class Enemy extends NPCCharacter{
	
	//An enemy will always monitor the player and collect
	//various data about them. They will also have abstract
	//Attack patterns that subclasses will implement.
	//Three "states": Ranged, Offensive, Defensive (Maybe more)?
	//These states will determine what attack patterns will be used
	//The states will be determined by collected player data.
	public static final String characterType = "Enemy";
	
	public Enemy(String characterName, WorldModel world) {
		super(characterName, world.getDialogueController());
		this.setCharacterData(new EnemyModel(characterName, this.getCharacterUIData(), world, this));
	}
	
	public interface PlayerObserver {
		public void handleObservation(Observation data);
	}

	public class EnemyModel extends NPCCharacterModel  {
		
		Player player;
		float pollTime;
		int pollCount;
		EnemyAI enemyAI;
		EnemyProperties enemyProperties;

		public EnemyModel(String characterName, EntityUIModel uiModel, WorldModel world, ModelListener modelListener) {
			super(characterName, uiModel, modelListener);
			this.pollTime = 0f;
			this.pollCount = 0;
			Json json = new Json();
			enemyProperties = json.fromJson(EnemyProperties.class, Gdx.files.internal("Json/" + characterName + "/enemyProperties.json"));
			if (enemyProperties != null) {
				enemyProperties.setSource(this);
			}
			enemyAI = EnemyAIInitializer.initializeAIWithKey(enemyProperties.enemyAIKey, this, world);
			this.setCurrentlyInteractable(false);
		}
		

		
		@Override
		public void update(float delta, TiledMapTileLayer collisionLayer) {
			super.update(delta, collisionLayer);
			enemyAI.process(delta);
		}
		
		public float getPollTime() {
			return pollTime;
		}

		public void setPollTime(float pollTime) {
			this.pollTime = pollTime;
		}

		public Player getPlayer() {
			return player;
		}

		public void setPlayer(Player player) {
			this.player = player;
		}



		@Override
		protected void manageAutomaticStates(float delta, TiledMapTileLayer collisionLayer) {
			// TODO Auto-generated method stub
		}

		@Override
		public int getAllegiance() {
			Integer allegiance = this.getCharacterProperties().getAllegiance();
			if (allegiance == null) {
				allegiance = 2;
			}
			return allegiance;
		}



		
		@Override
		public EntityCollisionData handleEntityXCollisionLogic(Rectangle tempGameplayBounds, boolean alreadyCollided) {
			if (alreadyCollided) {
				if (this.walking) {
					this.stopHorizontalMovement(false);
				}
				return null;
			}
			else if (this.isRespectingEntityCollision()){
				EntityCollisionData collidedEntity = this.getCollisionChecker().checkIfEntityCollidesWithOthers(this, tempGameplayBounds);
				boolean entityCollision = collidedEntity != null;
				if (entityCollision) {
					this.stopHorizontalMovement(false);
				}
//				this.stopEntityOverlapIfNeeded(collidedEntity, tempGameplayBounds, tempImageBounds);
				return collidedEntity;
			}
			return null;
		}
		
		@Override
		public EntityCollisionData handleEntityYCollisionLogic(Rectangle tempGameplayBounds, boolean alreadyCollided) {
			if (alreadyCollided) {
				return null;
			}
			else if (this.isRespectingEntityCollision()){
				EntityCollisionData collidedEntity = this.getCollisionChecker().checkIfEntityCollidesWithOthers(this, tempGameplayBounds);
//				boolean entityCollision = collidedEntity != null;
				return collidedEntity;
			}
			return null;
		}
		
		
		public void checkIfShouldAggroTarget(CharacterModel target, float damage) {
			if (damage > this.enemyProperties.damageForAggro && (this.enemyAI.currentTarget == null || !this.enemyAI.currentTarget.equals(target))) {
				this.enemyAI.currentTarget = target;
			}
		}
		
		@Override
		public NPCProperties getNPCProperties() {
			return this.enemyProperties;
		}
		
	}

	
}
