package com.mygdx.game.model.characters.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Json;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.characters.Character;
import com.mygdx.game.model.characters.player.Player;
import com.mygdx.game.model.world.WorldModel;

public class Enemy extends Character{
	
	//An enemy will always monitor the player and collect
	//various data about them. They will also have abstract
	//Attack patterns that subclasses will implement.
	//Three "states": Ranged, Offensive, Defensive (Maybe more)?
	//These states will determine what attack patterns will be used
	//The states will be determined by collected player data.
	public static final String characterType = "Enemy";
	
	public Enemy(String characterName, WorldModel world) {
		super(characterName);
		this.setCharacterData(new EnemyModel(characterName, this.getCharacterUIData(), world));
	}
	
	public interface PlayerObserver {
		public void handleObservation(Observation data);
	}

	
	public class EnemyModel extends CharacterModel  {
		
		Player player;
		float pollTime;
		int pollCount;
		EnemyAI enemyAI;
		EnemyProperties enemyProperties;

		public EnemyModel(String characterName, EntityUIModel uiModel, WorldModel world) {
			super(characterName, uiModel);
			this.pollTime = 0f;
			this.pollCount = 0;
			Json json = new Json();
			enemyProperties = json.fromJson(EnemyProperties.class, Gdx.files.internal("Json/" + characterName + "/enemyProperties.json"));
			enemyAI = EnemyAIInitializer.initializeAIWithKey(enemyProperties.enemyAIKey, this.getCharacterProperties(), this, world);
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
		public boolean handleAdditionCollisionLogic(Rectangle tempGameplayBounds) {
			return this.getCollisionChecker().checkIfEntityCollidesWithOthers(this, tempGameplayBounds);
		}
		
	}

	
}
