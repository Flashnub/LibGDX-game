package com.mygdx.game.model.world;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.constants.SaveController;
import com.mygdx.game.model.actions.Attack;
import com.mygdx.game.model.characters.Character;
import com.mygdx.game.model.characters.EntityModel;
import com.mygdx.game.model.characters.NPCCharacter;
import com.mygdx.game.model.characters.NPCCharacter.NPCCharacterModel;
import com.mygdx.game.model.characters.enemies.Enemy;
import com.mygdx.game.model.characters.enemies.Enemy.EnemyModel;
import com.mygdx.game.model.characters.player.GameSave.UUIDType;
import com.mygdx.game.model.characters.player.Player;
import com.mygdx.game.model.characters.player.Player.PlayerModel;
import com.mygdx.game.model.events.ActionListener;
import com.mygdx.game.model.events.CollisionChecker;
import com.mygdx.game.model.events.DialogueListener;
import com.mygdx.game.model.events.ObjectListener;
import com.mygdx.game.model.events.SaveListener;
import com.mygdx.game.model.globalEffects.WorldEffect;
import com.mygdx.game.model.projectiles.Explosion;
import com.mygdx.game.model.projectiles.Projectile;
import com.mygdx.game.model.worldObjects.WorldObject;


public class WorldModel implements ActionListener, ObjectListener, SaveListener, CollisionChecker{

    private final float MAX_TIMESTEP = 1 / 300f;
    private float accumulatedTime = 0f;
    final String kSpawnPoint = "SpawnPoint";
    final String kEntityType = "EntityType";
    final String kPatrolWaypoint = "PatrolWaypoint";
    final String kPatrolDuration = "PatrolDuration";
    final String kBreakDuration = "BreakDuration";

    
    TiledMapTileLayer collisionLayer;
    Array <Enemy> enemies;  
    Array <Projectile> projectiles;
    Array <SpawnPoint> spawns;
    Array <WorldObject> objects;
    Array <WorldObject> nearbyObjects;
    Array <NPCCharacter> npcCharacters;
    Array <NPCCharacter> nearbyNPCs;
    Array <Explosion> explosions;
    Player player;
    Array <WorldListener> worldListeners;
    Array <DialogueListener> dialogueListeners;
    SaveController saveController;
    DialogueController dialogueController;
    InputMultiplexer inputHandlers;
    Array <Rectangle> additionalRectangles;
    Array <WorldEffect> worldEffects;
    float stateTime;
    
    public WorldModel(TiledMapTileLayer collisionLayer) {
    	enemies = new Array<Enemy>();   
    	projectiles = new Array<Projectile>();
    	worldListeners = new Array<WorldListener>();
    	objects = new Array <WorldObject>();
    	nearbyObjects = new Array <WorldObject>();
    	npcCharacters = new Array <NPCCharacter>();
    	nearbyNPCs = new Array <NPCCharacter>();
    	explosions = new Array <Explosion> ();
    	this.additionalRectangles = new Array <Rectangle>();
    	this.worldEffects = new Array <WorldEffect>();
    	this.collisionLayer = collisionLayer;
    	saveController = new SaveController();
    	stateTime = 0f;
    	
//    	System.out.println("Width: " + collisionLayer.getTileWidth() + "Height: " + collisionLayer.getTileHeight());
    	inputHandlers = new InputMultiplexer();
    	Gdx.input.setInputProcessor(inputHandlers);
    	dialogueController = new DialogueController();
    	searchForTiles(collisionLayer);
    	dialogueController.setEntities(player, npcCharacters, enemies);
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
					player.getCharacterData().gameplayHitBox.x = (collisionLayer.getTileWidth() * x);
					player.getCharacterData().imageHitBox.y = collisionLayer.getTileHeight() * y;
					player.getCharacterData().gameplayHitBox.y = (collisionLayer.getTileWidth() * y);
					player.getCharacterData().setActionListener(this);
					player.getCharacterData().setObjectListener(this);
					player.getCharacterData().setCollisionChecker(this);
					this.inputHandlers.addProcessor(player);
					this.saveController.setCurrentGameSave(((PlayerModel) player.getCharacterData()).getGameSave());
				}
				else if (character instanceof Enemy){
					Enemy enemy = (Enemy) character;
					Array <Float> patrolWaypoints = new Array <Float>();
					Integer index = 1;
					Object wayPoint = tile.getTile().getProperties().get(kPatrolWaypoint.concat(index.toString()));
					while (wayPoint != null) {
						patrolWaypoints.add((Float)wayPoint);
						index += 1;
						wayPoint = tile.getTile().getProperties().get(kPatrolWaypoint.concat(index.toString()));
					}
					
					float patrolDuration = 0f;
					if (tile.getTile().getProperties().containsKey(kPatrolDuration)) {
						patrolDuration = (float) tile.getTile().getProperties().get(kPatrolDuration);
					}
					float breakDuration = 0f; 
					if (tile.getTile().getProperties().containsKey(kBreakDuration)) {
						breakDuration = (float) tile.getTile().getProperties().get(kBreakDuration);
					}
					
					this.addEnemy(enemy, collisionLayer.getTileWidth() * x,  collisionLayer.getTileHeight() * y);
					enemy.setPatrolInfo(patrolWaypoints, patrolDuration, breakDuration);
				}
				else if (character instanceof NPCCharacter) {
					NPCCharacter npc = (NPCCharacter) character;
					Array <Float> patrolWaypoints = new Array <Float>();
					Integer index = 1;
					Object wayPoint = tile.getTile().getProperties().get(kPatrolWaypoint.concat(index.toString()));
					while (wayPoint != null) {
						patrolWaypoints.add((Float)wayPoint);
						index += 1;
						wayPoint = tile.getTile().getProperties().get(kPatrolWaypoint.concat(index.toString()));
					}
					
					float patrolDuration = Float.MAX_VALUE;
					if (tile.getTile().getProperties().containsKey(kPatrolDuration)) {
						patrolDuration = (float) tile.getTile().getProperties().get(kPatrolDuration);
					}
					float breakDuration = Float.MAX_VALUE; 
					if (tile.getTile().getProperties().containsKey(kBreakDuration)) {
						breakDuration = (float) tile.getTile().getProperties().get(kBreakDuration);
					}
					
					this.addNPC(npc, collisionLayer.getTileWidth() * x, collisionLayer.getTileHeight() * y);
					npc.setPatrolInfo(patrolWaypoints, patrolDuration, breakDuration);
				}
			}
    	}
    }
    
    private void makeWorldObjectFromTile(Cell tile, int x, int y) {
    	MapProperties properties = tile.getTile().getProperties();
    	if (properties.containsKey(kSpawnPoint) && (!properties.containsKey(kEntityType) || (!properties.get(kEntityType).equals(Enemy.characterType) && !properties.get(kEntityType).equals(Player.characterType) && !properties.get(kEntityType).equals(NPCCharacter.characterType)))) {
			String entityName = (String) tile.getTile().getProperties().get(kSpawnPoint);
			WorldObject object = getObjectFromString(entityName, tile.getTile().getProperties());
			if (object != null) {
				object.getImageHitBox().x = collisionLayer.getTileWidth() * x;
				object.getImageHitBox().y = collisionLayer.getTileHeight() * y;
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
    			case NPCCharacter.characterType: 
    				return new NPCCharacter(name, dialogueController);
    		}
    	}
    	return null;
    }
    
    private WorldObject getObjectFromString(String typeOfObject, MapProperties properties) {
    	WorldObject worldObject = null;
    	if (properties != null) {
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
        stateTime += delta;
        if (stateTime % 10 < 0.2) {
        	this.additionalRectangles.clear();
        }
        while (accumulatedTime > 0) {
        	float timeForAction = accumulatedTime <= MAX_TIMESTEP ? accumulatedTime : MAX_TIMESTEP;
    		player.act(timeForAction, collisionLayer);
    		this.checkIfPlayerIsNearObjects();
    		this.checkIfPlayerIsNearNPCs();
    		for (int i = 0; i < enemies.size; i++) {
    			Character enemy = enemies.get(i);
    			enemy.act(timeForAction, collisionLayer);
    		}
    		for (int i = 0; i < npcCharacters.size; i++) {
    			Character npc = npcCharacters.get(i);
    			npc.act(timeForAction, collisionLayer);
    		}
    		for (int i = 0; i < projectiles.size; i++) {
    			Projectile projectile = projectiles.get(i);
    			projectile.update(timeForAction, collisionLayer);
    		}
    		for (int i = 0; i < objects.size; i++) {
    			WorldObject object = objects.get(i);
    			object.update(timeForAction, collisionLayer);
    		}
    		for (int i = 0; i < explosions.size; i++) {
    			Explosion explosion = explosions.get(i);
    			explosion.update(timeForAction);
    		}
    		for (int i = 0; i < worldEffects.size; i++) {
    			WorldEffect effect = worldEffects.get(i);
    			effect.process(delta);
    		}
    		accumulatedTime -= timeForAction;
        }

		this.dialogueController.update(delta);
    }

	public void deleteEnemy(Enemy enemy) {
		this.enemies.removeValue(enemy, true);
		for (WorldListener listener : worldListeners) {
			listener.handleDeletedEnemy(enemy);
		}
	}
	
	private void addEnemy(Enemy enemy, float xPosition, float yPosition) {
		enemy.getCharacterData().imageHitBox.x = xPosition;
		enemy.getCharacterData().gameplayHitBox.x = xPosition;
		enemy.getCharacterData().imageHitBox.y = yPosition;
		enemy.getCharacterData().gameplayHitBox.y = yPosition;
		enemies.add(enemy);
		((EnemyModel) enemy.getCharacterData()).setPlayer(player);
		((EnemyModel) enemy.getCharacterData()).setActionListener(this);
		((EnemyModel) enemy.getCharacterData()).setObjectListener(this);
		((EnemyModel) enemy.getCharacterData()).setCollisionChecker(this);
		for (WorldListener listener : worldListeners) {
			listener.handleAddedEnemy(enemy);
		}
	}
	
	private void addNPC(NPCCharacter npc, float xPosition, float yPosition) {
		npc.getCharacterData().imageHitBox.x = xPosition;
		npc.getCharacterData().gameplayHitBox.x = xPosition;
		npc.getCharacterData().imageHitBox.y = yPosition;
		npc.getCharacterData().gameplayHitBox.y = yPosition;
		npcCharacters.add(npc);
		((NPCCharacterModel) npc.getCharacterData()).setActionListener(this);
		((NPCCharacterModel) npc.getCharacterData()).setObjectListener(this);
		((NPCCharacterModel) npc.getCharacterData()).setCollisionChecker(this);
	}

	@Override
	public void deleteProjectile(Projectile projectile) {
		this.projectiles.removeValue(projectile, true);		
	}
	
	@Override
	public void deleteExplosion(Explosion explosion) {
		this.explosions.removeValue(explosion, true);
	}
	
	@Override
	public void spawnCharacter(String characterName, String characterType, float originX, float originY) {
		Character character = this.getCharacterFromString(characterName, characterType);
		if (character != null) {
			if (character instanceof Enemy){
				Enemy enemy = (Enemy) character;
				this.addEnemy(enemy, originX, originY);
			}
			else if (character instanceof NPCCharacter) {
				NPCCharacter npc = (NPCCharacter) character;
				this.addNPC(npc, originX, originY);
			}
		}
	}

	@Override
	public void addProjectile(Projectile projectile) {
		for (Projectile proj : this.projectiles) {
			if (proj.equals(projectile)) {
				return;
			}
		}
		this.projectiles.add(projectile);
	}
	
	@Override
	public void addExplosion(Explosion explosion) {
		for (Explosion explo : this.explosions) {
			if (explo.equals(explosion)) {
				return;
			}
		}
		this.explosions.add(explosion);
	}

	@Override
	public void processExplosion(Explosion explosion) {
		this.additionalRectangles.add(explosion.getGameplayHitBox());

		if (player.getCharacterData().getAllegiance() != explosion.getAllegiance()) {
			checkIfExplosionLands(player, explosion);
		}
		for (Enemy enemy : enemies) {
			if (enemy.getCharacterData().getAllegiance() != explosion.getAllegiance()) {
				checkIfExplosionLands(enemy, explosion);
			}
		}
	}

	@Override
	public void processAttack(Attack attack) {
		this.additionalRectangles.add(attack.getHitBox());
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
		if (!checkIfProjectileisActive(projectile)) {
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
	public void addWorldEffect(WorldEffect worldEffect) {
		if (worldEffect != null && this.worldEffects != null && !worldEffects.contains(worldEffect, true)) {
			worldEffects.add(worldEffect);
		}
	}

	@Override
	public void addObjectToWorld(WorldObject object) {
		if (object != null && objects != null && !objects.contains(object, true)) {
			//check if player has activated this object already.
			objects.add(object);
			object.setCollisionChecker(this);
		}
		for (WorldListener listener : worldListeners) {
			listener.handleAddedObjectToWorld(object);
		}
	}

	@Override
	public void objectToActOn(WorldObject object) {
		if (object != null && !object.isActivated()) {
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
	
	private void checkIfExplosionLands(Character character, Explosion explosion) {
		Polygon explosionPolygon = explosion.getGameplayHitBoxInPolygon();
		Polygon characterPolygon = character.getCharacterData().getGameplayHitBoxInPolygon();
		if (Intersector.overlapConvexPolygons(explosionPolygon, characterPolygon)) {
			character.getCharacterData().shouldExplosionHit(explosion);
		}
	}
	
	private void checkIfProjectileLands(Character character, Projectile projectile) {
		Polygon projectilePolygon = projectile.getGameplayHitBoxInPolygon();
		Polygon characterPolygon = character.getCharacterData().getGameplayHitBoxInPolygon();
		if (Intersector.overlapConvexPolygons(projectilePolygon, characterPolygon)) {
			character.getCharacterData().shouldProjectileHit(projectile);
		}
	}
	
	private boolean checkIfProjectileisActive(Projectile projectile) {
		return projectile.isActive();
	}
	
	@Override
	public EntityModel checkIfEntityCollidesWithOthers(EntityModel entity, Rectangle tempGameplayBounds) {
		EntityModel collidingEntity = null;
		if (entity.getAllegiance() == Player.allegiance) {
			for (Enemy enemy : this.enemies) {
				if (enemy.getCharacterData().getAllegiance() != Player.allegiance && enemy.getCharacterData().getGameplayHitBox().overlaps(tempGameplayBounds)) {
					collidingEntity = enemy.getCharacterData();
					break;
				}
			}
//			if (collidingEntity == null) {
//				for (WorldObject object : this.objects) {
//					if (object.shouldCollideWithCharacter() && object.getGameplayHitBox().overlaps(tempGameplayBounds)) {
//						collidingEntity = true;
//						break;
//					}
//				}
//			}

		}
		else if (entity instanceof NPCCharacterModel){
			for (Enemy enemy : this.enemies) {
				if (enemy.getCharacterData().getAllegiance() != entity.getAllegiance() && enemy.getCharacterData().getGameplayHitBox().overlaps(tempGameplayBounds)) {
					collidingEntity = enemy.getCharacterData();
					break;
				}
			}
			if (collidingEntity == null && entity.getAllegiance() != Player.allegiance) {
				if (player.getCharacterData().getAllegiance() != entity.getAllegiance() && player.getCharacterData().getGameplayHitBox().overlaps(tempGameplayBounds)) {
					collidingEntity = player.getCharacterData();
				}

			}
		}
//		else if (entity.getAllegiance() == WorldObject.allegiance) {
//			isColliding = player.getCharacterData().getGameplayHitBox().overlaps(tempGameplayBounds);
//			if (!isColliding) {
//				for (Enemy enemy : this.enemies) {
//					if (enemy.getCharacterData().getGameplayHitBox().overlaps(tempGameplayBounds)) {
//						isColliding = true;
//						break;
//					}
//				}
//			}
//		}
//		else {
//			isColliding = player.getCharacterData().getGameplayHitBox().overlaps(tempGameplayBounds);
//			if (!isColliding) {
//				for (WorldObject object : this.objects) {
//					if (object.shouldCollideWithCharacter() && object.getGameplayHitBox().overlaps(tempGameplayBounds)) {
//						isColliding = true;
//						break;
//					}
//				}
//			}
//		}
		return collidingEntity;
	}

	@Override
	public boolean checkIfEntityCollidesWithObjects(EntityModel entity, Rectangle tempGameplayBounds) {
		boolean isColliding = false;
		for (WorldObject object : this.objects) {
			if (object.shouldCollideWithCharacter() && object.getGameplayHitBox().overlaps(tempGameplayBounds)) {
				isColliding = true;
				break;
			}
		}
		return isColliding;
	}
	

	private void checkIfPlayerIsNearObjects() {
		nearbyObjects.clear();
		for (WorldObject object : this.objects) {
			if (object.getImageHitBox().overlaps(player.getCharacterData().getImageHitBox())) {
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
	
	private void checkIfPlayerIsNearNPCs() {
		this.nearbyNPCs.clear();
		for (NPCCharacter character : this.npcCharacters) {
			if (character.getCharacterData().getImageHitBox().overlaps(player.getCharacterData().getImageHitBox())) {
				nearbyNPCs.add(character);
			}
		}
		if (nearbyNPCs.size > 0) {
			((PlayerModel) player.getCharacterData()).setNearbyNPC(nearbyNPCs.get(0));
		}
		for (WorldListener listener : worldListeners) {
			listener.updateWithNearbyNPCs(this.nearbyNPCs);
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
	public void addUUIDToSave(Integer UUID, UUIDType type) {
		// TODO Auto-generated method stub
		PlayerModel model = (PlayerModel) player.getCharacterData();
		model.addUUIDToSave(UUID, type);

	}

	@Override
	public void triggerSave() {
		this.saveController.save();
	}

	public Array<Projectile> getProjectiles() {
		return projectiles;
	}

	public Array<Rectangle> getAdditionalRectangles() {
		return additionalRectangles;
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

	public DialogueController getDialogueController() {
		return dialogueController;
	}

	public Array<NPCCharacter> getNpcCharacters() {
		return npcCharacters;
	}

	public Array<Explosion> getExplosions() {
		return explosions;
	}






}