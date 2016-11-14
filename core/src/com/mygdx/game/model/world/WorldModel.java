package com.mygdx.game.model.world;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.constants.SaveController;
import com.mygdx.game.model.actions.Attack;
import com.mygdx.game.model.characters.Character;
import com.mygdx.game.model.characters.enemies.Enemy;
import com.mygdx.game.model.characters.enemies.Enemy.EnemyModel;
import com.mygdx.game.model.characters.player.GameSave.UUIDType;
import com.mygdx.game.model.characters.player.Player;
import com.mygdx.game.model.characters.player.Player.PlayerModel;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.ObjectListener;
import com.mygdx.game.model.events.SaveListener;
import com.mygdx.game.model.projectiles.Projectile;
import com.mygdx.game.model.worldObjects.WorldObject;
import com.mygdx.game.utils.CellWrapper;


public class WorldModel implements ActionListener, ObjectListener, SaveListener{

    private final float MAX_TIMESTEP = 1 / 300f;
    private float accumulatedTime = 0f;
    final String kSpawnPoint = "SpawnPoint";
    final String kEntityType = "EntityType";
    
    TiledMapTileLayer collisionLayer;
    Array <Enemy> enemies;  
    Array <Projectile> projectiles;
    Array <SpawnPoint> spawns;
    Array <WorldObject> objects;
    Array <WorldObject> nearbyObjects;
    Player player;
    Array <WorldListener> worldListeners;
    SaveController saveController;
    
    public WorldModel(TiledMapTileLayer collisionLayer) {
    	enemies = new Array<Enemy>();   
    	projectiles = new Array<Projectile>();
    	worldListeners = new Array<WorldListener>();
    	objects = new Array <WorldObject>();
    	nearbyObjects = new Array <WorldObject>();
    	this.collisionLayer = collisionLayer;
    	this.saveController = new SaveController();
    	
//    	System.out.println("Width: " + collisionLayer.getTileWidth() + "Height: " + collisionLayer.getTileHeight());
    	searchForTiles(collisionLayer);
    }
    
    private void searchForCharacters(TiledMapTileLayer collisionLayer) {
    	for(int x = 0; x < collisionLayer.getWidth(); x++) {
    		for (int y = 0; y < collisionLayer.getHeight(); y++) {
    			//Search for "Spawn point" tiles
    			Cell tile = collisionLayer.getCell(x, y);
    			this.makeCharacterFromTile(tile, x, y);
    		}
    	}
    }
    
    private void searchForWorldObjects(TiledMapTileLayer collisionLayer) {
    	for(int x = 0; x < collisionLayer.getWidth(); x++) {
    		for (int y = 0; y < collisionLayer.getHeight(); y++) {
    			//Search for "Spawn point" tiles
    			Cell tile = collisionLayer.getCell(x, y);
    			this.makeWorldObjectFromTile(tile, x, y);
    		}
    	}
    }
    
    private void makeCharacterFromTile(Cell tile, int x, int y) {
    	if (tile.getTile().getProperties().containsKey(kSpawnPoint)) {
			String entityName = (String) tile.getTile().getProperties().get(kSpawnPoint);
			String entityType = (String) tile.getTile().getProperties().get(kEntityType);
			Character character = getCharacterFromString(entityName, entityType); 
			if (character != null) {
				if (character instanceof Player) {
					player = (Player)character; 
					player.getCharacterData().imageHitBox.x = collisionLayer.getTileWidth() * x;
					player.getCharacterData().gameplayHitBox.x = .4f*(collisionLayer.getTileWidth() * x);
					player.getCharacterData().imageHitBox.y = collisionLayer.getTileHeight() * y;
					player.getCharacterData().gameplayHitBox.y = .2f*(collisionLayer.getTileWidth() * y);
					player.getCharacterData().setActionListener(this);
					player.getCharacterData().setItemListener(this);
					Gdx.input.setInputProcessor(player);
					this.saveController.setCurrentGameSave(((PlayerModel) player.getCharacterData()).getGameSave());
				}
				else if (character instanceof Enemy){
					Enemy enemy = (Enemy) character;
					this.addEnemy(enemy, collisionLayer.getTileWidth() * x,  collisionLayer.getTileHeight() * y);
				}
			}
    	}
    }
    
    private void makeWorldObjectFromTile(Cell tile, int x, int y) {
    	if (tile.getTile().getProperties().containsKey(kSpawnPoint)) {
			String entityName = (String) tile.getTile().getProperties().get(kSpawnPoint);
			WorldObject object = getObjectFromString(entityName, tile.getTile().getProperties());
			if (object != null) {
				object.getBounds().x = collisionLayer.getTileWidth() * x;
				object.getBounds().y = collisionLayer.getTileHeight() * y;
				this.addObjectToWorld(object);
			}
    	}
    }
    
    private void searchForTiles(TiledMapTileLayer collisionLayer) {
    	this.searchForCharacters(collisionLayer);
    	this.searchForWorldObjects(collisionLayer);
    }
    
    private Character getCharacterFromString(String name, String characterType) {
    	if (characterType != null) {
    		switch(characterType) {
    			case Player.characterType: 
    				return new Player();
    			case Enemy.characterType:
    				return new Enemy(name, this);
    		}
    	}
    	 return null;
    }
    
    private WorldObject getObjectFromString(String typeOfObject, MapProperties properties) {
    	WorldObject worldObject = null;
    	if (properties != null && !typeOfObject.equals(Player.characterType) && !typeOfObject.equals(Enemy.characterType)) {
    		Integer uuid = (Integer) properties.get(WorldObject.WorldObjUUIDKey);
			boolean isAlreadyActivated = ((PlayerModel)player.getCharacterData()).isUUIDInSave(uuid);
			if (typeOfObject != null) {
				boolean shouldAddAnyway = WorldObject.shouldAddIfActivated(typeOfObject);
				if ((isAlreadyActivated && shouldAddAnyway) || !isAlreadyActivated) {
					try {
						Class <?> type = Class.forName("com.mygdx.game.model.worldObjects." + typeOfObject);
						Constructor <?> constructor = type.getConstructor(String.class, MapProperties.class, ObjectListener.class, SaveListener.class);
						worldObject = (WorldObject) constructor.newInstance(typeOfObject, properties, this, this);

					} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
					if (isAlreadyActivated && shouldAddAnyway && worldObject != null) {
						worldObject.activateAlreadyActivatedObject(this);
					}
				}
			}
    	}
    	return worldObject;
    }
  //--------------Level METHODS====================//

    public void act(float delta) {
        // Fixed timestep
        accumulatedTime += delta;

        while (accumulatedTime > 0) {
        	float timeForAction = accumulatedTime <= MAX_TIMESTEP ? accumulatedTime : MAX_TIMESTEP;
    		player.act(timeForAction, collisionLayer);
    		this.checkIfPlayerIsNearObjects();
    		for (int i = 0; i < enemies.size; i++) {
    			Character enemy = enemies.get(i);
    			enemy.act(timeForAction, collisionLayer);
    		}
    		accumulatedTime -= timeForAction;
        }
		for (Projectile projectile : projectiles) {
			projectile.update(delta, collisionLayer);
		}
		for (WorldObject object : objects) {
			object.update(delta);
		}
    }

	public void deleteEnemy(Enemy enemy) {
		this.enemies.removeValue(enemy, true);
		for (WorldListener listener : worldListeners) {
			listener.handleDeletedEnemy(enemy);
		}
	}
	
	private void addEnemy(Enemy enemy, float xPosition, float yPosition) {
		enemy.getCharacterData().imageHitBox.x = xPosition;
		enemy.getCharacterData().gameplayHitBox.x = .4f*xPosition;
		enemy.getCharacterData().imageHitBox.y = yPosition;
		enemy.getCharacterData().gameplayHitBox.y = .2f*yPosition;
		enemies.add(enemy);
		((EnemyModel) enemy.getCharacterData()).setPlayer(player);
		((EnemyModel) enemy.getCharacterData()).setActionListener(this);
		((EnemyModel) enemy.getCharacterData()).setItemListener(this);
		for (WorldListener listener : worldListeners) {
			listener.handleAddedEnemy(enemy);
		}
	}

	@Override
	public void deleteProjectile(Projectile projectile) {
		this.projectiles.removeValue(projectile, true);		
	}

	@Override
	public void addProjectile(Projectile projectile) {
		// TODO Auto-generated method stub
		for (Projectile proj : this.projectiles) {
			if (proj.equals(projectile)) {
				return;
			}
		}
		this.projectiles.add(projectile);
	}

	@Override
	public void processAttack(Attack attack) {
		if (player.getCharacterData().getAllegiance() != attack.getAllegiance()) {
			checkIfAttackLands(player, attack);
		}
		for (Enemy enemy : enemies) {
			if (enemy.getCharacterData().getAllegiance() != attack.getAllegiance()) {
				checkIfAttackLands(enemy, attack);
			}
		}
	}

	@Override
	public void processProjectile(Projectile projectile) {
		if (checkIfProjectileShouldExpire(projectile)) {
			return;
		}
		if (player.getCharacterData().getAllegiance() != projectile.getAllegiance()) {
			checkIfProjectileLands(player, projectile);
		}
		else {
			for (Enemy enemy : enemies) {
				if (enemy.getCharacterData().getAllegiance() != projectile.getAllegiance()) {
					checkIfProjectileLands(enemy, projectile);
				}
			}
		}
	}
	
	@Override
	public void addObjectToWorld(WorldObject object) {
		// TODO Auto-generated method stub
		if (object != null && objects != null && !objects.contains(object, true)) {
			//check if player has activated this object already.
			objects.add(object);
		}
		for (WorldListener listener : worldListeners) {
			listener.handleAddedObjectToWorld(object);
		}
	}

	@Override
	public void objectToActOn(WorldObject object) {
		if (!object.isActivated()) {
			for (WorldListener listener : worldListeners) {
				listener.handlePlayerInteractionWithObject(object);
			}		
			object.activateObjectOnWorld(this);
		}
	}
	
	@Override
	public void deleteObjectFromWorld(WorldObject object) {
		if (object != null && objects != null && objects.contains(object, true)) {
			objects.removeValue(object, true);
		}
	}


	
	private void checkIfAttackLands(Character character, Attack attack) {
		if (attack.getHitBox().overlaps(character.getCharacterData().getGameplayHitBox())) {
			character.getCharacterData().shouldAttackHit(attack);
		}
	}
	
	private void checkIfProjectileLands(Character character, Projectile projectile) {
		if (projectile.getImageHitBox().overlaps(character.getCharacterData().getGameplayHitBox())) {
			character.getCharacterData().shouldProjectileHit(projectile);
		}
	}
	
	private boolean checkIfProjectileShouldExpire(Projectile projectile) {
		if (projectile.getStateTime() > projectile.getSettings().getDuration()) {
			projectile.processExpirationOrHit(null);
			return true;
		}
		return false;
	}

	@Override
	public boolean doTilesCollideWithObjects(Array<CellWrapper> nearbyTiles, TiledMapTileLayer collisionLayer) {
		boolean isColliding = false;
		for (int i = 0; i < nearbyTiles.size; i++) {
			CellWrapper cell = nearbyTiles.get(i);
			Rectangle tileBounds = new Rectangle(cell.getOrigin().x, cell.getOrigin().y, 
				collisionLayer.getTileWidth(), collisionLayer.getTileHeight());
			for (WorldObject object : this.objects) {
				if (object.shouldHaveCollisionDetection() && object.getBounds().overlaps(tileBounds)) {
					isColliding = true;
				}
			}
		}
		return isColliding;
	}

	
	private void checkIfPlayerIsNearObjects() {
		nearbyObjects.clear();
		for (WorldObject object : this.objects) {
			if (object.getBounds().overlaps(player.getCharacterData().getImageHitBox())) {
				nearbyObjects.add(object);
			}
		}
		if (nearbyObjects.size > 0) {
			((PlayerModel) player.getCharacterData()).setNearbyObject(nearbyObjects.get(0));
		}
		for (WorldListener listener : worldListeners) {
			listener.updateWithNearbyObjects(nearbyObjects);
		}
	}
	
	public void addWorldListener(WorldListener listener) {
		if (!worldListeners.contains(listener, true)) {
			worldListeners.add(listener);
			for (Enemy enemy : enemies) {
				listener.handleAddedEnemy(enemy);
			}
		}
	}

	@Override
	public void triggerSave() {
		this.saveController.save();
	}

	public Array<Projectile> getProjectiles() {
		return projectiles;
	}

	public Array<Enemy> getEnemies() {
		return enemies;
	}
	
	public Array<SpawnPoint> getSpawnPoints() {
		return spawns;
	}
	
	public Array<WorldListener> getWorldListeners() {
		return worldListeners;
	}

	public Player getPlayer() {
		return player;
	}
	
	public Array<WorldObject> getObjects() {
		return objects;
	}

	@Override
	public void addUUIDToSave(Integer UUID, UUIDType type) {
		// TODO Auto-generated method stub
		PlayerModel model = (PlayerModel) player.getCharacterData();
		model.addUUIDToSave(UUID, type);

	}

}