package com.mygdx.game.model.characters.enemies;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.mygdx.game.model.characters.EntityUIModel;
import com.mygdx.game.model.characters.InteractableCharacter;
import com.mygdx.game.model.characters.ObservableData;
import com.mygdx.game.model.characters.player.Player;

public class Enemy extends InteractableCharacter{
	
	//An enemy will always monitor the player and collect
	//various data about them. They will also have abstract
	//Attack patterns that subclasses will implement.
	//Three "states": Ranged, Offensive, Defensive (Maybe more)?
	//These states will determine what attack patterns will be used
	//The states will be determined by collected player data.
	public static final String characterType = "Enemy";
	
	public Enemy(String characterName) {
		super(characterName);
	}
	
	public interface PlayerObserver {
		public void handleObservation(ObservableData data);
	}

	
	public class EnemyModel extends InteractableCharacterModel  {
		
		Player player;
		float pollTime;
		int pollCount;

		public EnemyModel(String characterName, EntityUIModel uiModel) {
			super(characterName, uiModel);
			this.pollTime = 0f;
			this.pollCount = 0;
		}
		

		
		@Override
		public void update(float delta, TiledMapTileLayer collisionLayer) {
			super.update(delta, collisionLayer);
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
		protected void landed() {
			// TODO Auto-generated method stub
			
		}



		@Override
		public int getAllegiance() {
			// TODO Auto-generated method stub
			return 0;
		}

		
	}

	
}
