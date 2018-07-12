package com.mygdx.game.model.world;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.constants.SaveController;
import com.mygdx.game.model.actions.ActionSegment;
import com.mygdx.game.model.actions.Attack;
import com.mygdx.game.model.characters.Character;
import com.mygdx.game.model.characters.Character.CharacterModel;
import com.mygdx.game.model.characters.EntityCollisionData;
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
import com.mygdx.game.model.events.StatsInfoListener;
import com.mygdx.game.model.globalEffects.WorldEffect;
import com.mygdx.game.model.hitSpark.HitSpark;
import com.mygdx.game.model.hitSpark.HitSparkListener;
import com.mygdx.game.model.projectiles.Explosion;
import com.mygdx.game.model.projectiles.Projectile;
import com.mygdx.game.model.worldObjects.Item;
import com.mygdx.game.model.worldObjects.WorldObject;
import com.mygdx.game.utils.MathUtils;


public class WorldModel implements ActionListener, ObjectListener, SaveListener, CollisionChecker, StatsInfoListener, HitSparkListener{

    private final float MAX_TIMESTEP = 1 / 300f;
    private float accumulatedTime = 0f;
    final String kSpawnPoint = "SpawnPoint";
    final String kEntityType = "EntityType";
    final String kPatrolWaypoint = "PatrolWaypoint";
    final String kPatrolDuration = "PatrolDuration";
    final String kBreakDuration = "BreakDuration";

    float freezeWorldDuration = 0f;
    
    TiledMapTileLayer collisionLayer;
    Array <Enemy> enemies;  
    Array <Projectile> projectiles;
    Array <SpawnPoint> spawns;
    Array <WorldObject> objects;
    Array <WorldObject> nearbyObjects;
    Array <NPCCharacter> npcCharacters;
    Array <NPCCharacter> nearbyNPCs;
    Array <Explosion> explosions;
    Array <HitSpark> hitSparks;
    Player player;
    Array <WorldListener> worldListeners;
    Array <DialogueListener> dialogueListeners;
    SaveController saveController;
    DialogueController dialogueController;
    InputMultiplexer inputHandlers;
    Array <Rectangle> collisionRectangles; //For debug purposes
    Array <Rectangle> hitBoxes; //For debug purposes
    Array <WorldEffect> worldEffects;
    float stateTime;
    boolean freezeWorld;
    float freezeTime;
    CharacterModel characterImmuneToFreeze;
    Controller controller;
    
    public WorldModel(TiledMapTileLayer collisionLayer) {
    	enemies = new Array<Enemy>();   
    	projectiles = new Array<Projectile>();
    	worldListeners = new Array<WorldListener>();
    	objects = new Array <WorldObject>();
    	nearbyObjects = new Array <WorldObject>();
    	npcCharacters = new Array <NPCCharacter>();
    	nearbyNPCs = new Array <NPCCharacter>();
    	explosions = new Array <Explosion> ();
    	hitSparks = new Array <HitSpark> ();
    	hitBoxes = new Array <Rectangle> ();
    	this.collisionRectangles = new Array <Rectangle>();
    	this.worldEffects = new Array <WorldEffect>();
    	this.collisionLayer = collisionLayer;
    	saveController = new SaveController();
    	stateTime = 0f;
    	freezeWorld = false;
    	freezeTime = 0f;
    	characterImmuneToFreeze = null;
    	
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
    	if (tile != null && tile.getTile().getProperties().containsKey(kSpawnPoint)) {
			String entityName = (String) tile.getTile().getProperties().get(kSpawnPoint);
			String entityType = (String) tile.getTile().getProperties().get(kEntityType);
			Character character = getCharacterFromString(entityName, entityType); 
			if (character != null) {
				if (character instanceof Player) {
					player = (Player)character; 
					player.getCharacterData().imageBounds.x = collisionLayer.getTileWidth() * x;
					player.getCharacterData().gameplayCollisionBox.x = (collisionLayer.getTileWidth() * x);
					player.getCharacterData().imageBounds.y = collisionLayer.getTileHeight() * y;
					player.getCharacterData().gameplayCollisionBox.y = (collisionLayer.getTileWidth() * y);
					player.getCharacterData().setActionListener(this);
					player.getCharacterData().setObjectListener(this);
					player.getCharacterData().setCollisionChecker(this);
					((PlayerModel)player.getCharacterData()).setInfoListener(this);
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
    		Integer uuid = null;
    		if (properties.get(WorldObject.WorldObjUUIDKey) != null) {
    			uuid = (Integer) properties.get(WorldObject.WorldObjUUIDKey);
    		}
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
    	this.checkForControllers();
        accumulatedTime += delta;
        stateTime += delta;
        if (stateTime % 10 < 0.2) {
        	this.collisionRectangles.clear();
        }
        while (accumulatedTime > 0) {
        	float timeForAction = accumulatedTime <= MAX_TIMESTEP ? accumulatedTime : MAX_TIMESTEP;
        	if (!this.freezeWorld || player.getCharacterData().equals(this.characterImmuneToFreeze)) {
        		player.act(timeForAction, collisionLayer);
        		this.checkIfPlayerIsNearNPCs();
        		this.checkIfPlayerIsNearObjects();
        	}

    		for (int i = 0; i < enemies.size; i++) {
    			Character enemy = enemies.get(i);
            	if (!this.freezeWorld || enemy.getCharacterData().equals(this.characterImmuneToFreeze)) {
            		enemy.act(timeForAction, collisionLayer);
            	}
    		}
    		for (int i = 0; i < npcCharacters.size; i++) {
    			Character npc = npcCharacters.get(i);
            	if (!this.freezeWorld || npc.getCharacterData().equals(this.characterImmuneToFreeze)) {
        			npc.act(timeForAction, collisionLayer);
            	}
    		}
    		for (int i = 0; i < projectiles.size; i++) {
    			Projectile projectile = projectiles.get(i);
    			if (!this.freezeWorld) 
    				projectile.update(timeForAction, collisionLayer);
    		}
    		for (int i = 0; i < objects.size; i++) {
    			WorldObject object = objects.get(i);
    			if (!this.freezeWorld) 
    				object.update(timeForAction, collisionLayer);
    		}
    		for (int i = 0; i < explosions.size; i++) {
    			Explosion explosion = explosions.get(i);
    			if (!this.freezeWorld) 
    				explosion.update(timeForAction, collisionLayer);
    		}
    		for (int i = 0; i < worldEffects.size; i++) {
    			WorldEffect effect = worldEffects.get(i);
    			if (!this.freezeWorld) 
    				effect.process(timeForAction);
    		}
    		for (int i = 0; i < hitSparks.size; i++) {
    			HitSpark hitSpark = hitSparks.get(i);
    			if (!this.freezeWorld) {
    				hitSpark.update(timeForAction);
    			}
    		}
    		accumulatedTime -= timeForAction;
        }

		this.dialogueController.update(delta);
		
		if (freezeWorld) {
			this.freezeTime += delta;
			if (this.freezeTime > this.freezeWorldDuration) {
				this.freezeTime = 0f;
				this.freezeWorldDuration = 0f;
				this.freezeWorld = false;
				this.characterImmuneToFreeze = null;
			}
		}
    }
    
    @Override
    public void deleteCharacter(CharacterModel character) {
    	for (NPCCharacter npc : this.npcCharacters) {
    		if (npc.getCharacterData().equals(character)) {
    			this.npcCharacters.removeValue(npc, true);
    			this.nearbyNPCs.removeValue(npc, true);
    			for (WorldListener listener : this.getWorldListeners()) {
    				listener.updateWithNearbyNPCs(nearbyNPCs);
    			}
    			return;
    		}
    	}
    	for (Enemy enemy : this.enemies) {
    		if (enemy.getCharacterData().equals(character)) {
    			this.deleteEnemy(enemy);
    		}
    	}
    }
    
	@Override
	public void superActivatedFrom(CharacterModel source, ActionSegment segment) {
		this.freezeWorld = true;
		this.characterImmuneToFreeze = source;
		this.freezeWorldDuration = segment.getWindUpTime();
		for (WorldListener listener : this.worldListeners) {
			listener.handleSuperAction(segment);
		}
	}

	public void deleteEnemy(Enemy enemy) {
		this.enemies.removeValue(enemy, true);
		for (WorldListener listener : worldListeners) {
			listener.handleDeletedEnemy(enemy);
		}
	}
	
	private void addEnemy(Enemy enemy, float xPosition, float yPosition) {
		enemy.getCharacterData().imageBounds.x = xPosition;
		enemy.getCharacterData().gameplayCollisionBox.x = xPosition;
		enemy.getCharacterData().imageBounds.y = yPosition;
		enemy.getCharacterData().gameplayCollisionBox.y = yPosition;
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
		npc.getCharacterData().imageBounds.x = xPosition;
		npc.getCharacterData().gameplayCollisionBox.x = xPosition;
		npc.getCharacterData().imageBounds.y = yPosition;
		npc.getCharacterData().gameplayCollisionBox.y = yPosition;
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
		this.collisionRectangles.add(explosion.getGameplayCollisionBox());

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
		for (Rectangle hitBox : attack.getHitBoxes()) {
			this.hitBoxes.add(hitBox);
		}
		
		if (player.getCharacterData().getAllegiance() != attack.getAllegiance()) {
			checkIfAttackLands(player, attack);
		}
		for (int i = 0; i < enemies.size; i++) {
			if (enemies.get(i).getCharacterData().getAllegiance() != attack.getAllegiance()) {
				checkIfAttackLands(enemies.get(i), attack);
			}
		}
		for (int i = 0; i < this.objects.size; i++) {
			checkIfAttackLands(objects.get(i), attack);
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
		for (Rectangle hitBox : character.getCharacterData().gameplayHurtBoxes){ 
			for (Rectangle attackHitBox : attack.getHitBoxes()) {
				if (attackHitBox.overlaps(hitBox)) {
					character.getCharacterData().shouldAttackHit(attack, this, attackHitBox);
					return;
				}
			}
		}
	}
	
	private void checkIfAttackLands(WorldObject object, Attack attack) {
		for (Rectangle hitBox : object.gameplayHurtBoxes){ 
			for (Rectangle attackHitBox : attack.getHitBoxes()) {
				if (attackHitBox.overlaps(hitBox)) {
					object.shouldAttackHit(attack, this, attackHitBox);
					return;
				}
			}
		}
	}
	
	private void checkIfExplosionLands(Character character, Explosion explosion) {
		for (Rectangle explosionHitBox : explosion.gameplayHurtBoxes) {
			Polygon explosionPolygon = MathUtils.rectangleToPolygon(explosionHitBox, explosion.getVelocityAngle());
			for (Rectangle characterHitBox : character.getCharacterData().gameplayHurtBoxes) {
				Polygon characterPolygon = MathUtils.rectangleToPolygon(characterHitBox, character.getCharacterData().getVelocityAngle());
				if (Intersector.overlapConvexPolygons(explosionPolygon, characterPolygon)) {
					character.getCharacterData().shouldExplosionHit(explosion, this);
					return;
				}
			}
		}
	}
	
	private void checkIfProjectileLands(Character character, Projectile projectile) {
		for (Rectangle explosionHitBox : projectile.gameplayHurtBoxes) {
			Polygon projectilePolygon = MathUtils.rectangleToPolygon(explosionHitBox, projectile.getVelocityAngle());
			for (Rectangle characterHitBox : character.getCharacterData().gameplayHurtBoxes) {
				Polygon characterPolygon = MathUtils.rectangleToPolygon(characterHitBox, character.getCharacterData().getVelocityAngle());
				if (Intersector.overlapConvexPolygons(projectilePolygon, characterPolygon)) {
					character.getCharacterData().shouldProjectileHit(projectile, this);
					return;
				}
			}
		}
	}
	
	private boolean checkIfProjectileisActive(Projectile projectile) {
		return projectile.isActive();
	}
	
	public EntityCollisionData checkIfEntityCollidesWithOthers(EntityModel entity, Rectangle tempGameplayBounds) {
		return this.checkIfEntityCollidesWithOthers(entity, tempGameplayBounds, true);
	}
	
	@Override
	public EntityCollisionData checkIfEntityCollidesWithOthers(EntityModel entity, Rectangle tempGameplayBounds, boolean useRespect) {
		EntityCollisionData collidingEntity = null;
		if (entity.getAllegiance() == Player.allegiance) {
			for (Enemy enemy : this.enemies) {
				if (enemy.getCharacterData().getAllegiance() != Player.allegiance 
				&& enemy.getCharacterData().getGameplayCollisionBox().overlaps(tempGameplayBounds)) {
					if ((useRespect && player.getCharacterData().isRespectingEntityCollision() 
						&& enemy.getCharacterData().isRespectingEntityCollision()) || !useRespect) {
						collidingEntity = new EntityCollisionData(entity, enemy.getCharacterData(), tempGameplayBounds);
						break;
					}
				}
			}
		}
		else if (entity instanceof NPCCharacterModel){
			for (Enemy enemy : this.enemies) {
				if (enemy.getCharacterData().getAllegiance() != entity.getAllegiance() 
				&& enemy.getCharacterData().getGameplayCollisionBox().overlaps(tempGameplayBounds)) {
					if ((useRespect && player.getCharacterData().isRespectingEntityCollision() 
						&& enemy.getCharacterData().isRespectingEntityCollision()) || !useRespect) {
						collidingEntity = new EntityCollisionData(entity, enemy.getCharacterData(), tempGameplayBounds);
						break;
					}
				}
			}
			if (collidingEntity == null && entity.getAllegiance() != Player.allegiance) {
				if (player.getCharacterData().getAllegiance() != entity.getAllegiance() 
				&& player.getCharacterData().getGameplayCollisionBox().overlaps(tempGameplayBounds)) {
					if ((useRespect && player.getCharacterData().isRespectingEntityCollision() 
						&& entity.isRespectingEntityCollision()) || !useRespect) {
						collidingEntity = new EntityCollisionData(entity, player.getCharacterData(), tempGameplayBounds);
					}
				}

			}
		}
		return collidingEntity;
	}

	@Override
	public boolean checkIfEntityCollidesWithObjects(EntityModel entity, Rectangle tempGameplayBounds) {
		boolean isColliding = false;
		for (WorldObject object : this.objects) {
			if (object.shouldCollideWithEntity() && object.getGameplayCollisionBox().overlaps(tempGameplayBounds) && !object.equals(entity)) {
				isColliding = true;
				break;
			}
		}
		return isColliding;
	}
	

	private void checkIfPlayerIsNearObjects() {
		nearbyObjects.clear();
		for (WorldObject object : this.objects) {
			if (object.getGameplayCollisionBox().overlaps(player.getCharacterData().getGameplayCollisionBox()) && object.canBeActedOn()) {
				nearbyObjects.add(object);
			}
		}
		if (nearbyObjects.size > 0) {
			((PlayerModel) player.getCharacterData()).setNearbyObject(nearbyObjects.get(0));
		}
		else {
			((PlayerModel) player.getCharacterData()).setNearbyObject(null);

		}
		for (WorldListener listener : worldListeners) {
			listener.updateWithNearbyObjects(nearbyObjects);
		}
	}
	
	private void checkIfPlayerIsNearNPCs() {
		this.nearbyNPCs.clear();
		for (NPCCharacter character : this.npcCharacters) {
			if (character.getCharacterData().getGameplayCollisionBox().overlaps(player.getCharacterData().getGameplayCollisionBox()) && ((NPCCharacterModel) character.getCharacterData()).canBeActedOn()) {
				nearbyNPCs.add(character);
			}
		}
		if (nearbyNPCs.size > 0) {
			((PlayerModel) player.getCharacterData()).setNearbyObject((NPCCharacterModel) nearbyNPCs.get(0).getCharacterData());
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
		PlayerModel model = (PlayerModel) player.getCharacterData();
		model.addUUIDToSave(UUID, type);

	}
	
	private void checkForControllers() {
		if (controller == null) {
			Array<Controller> controllers = Controllers.getControllers();
			if(controllers.size != 0) {
				Controller pad = null;
				for(Controller c : controllers) {
					if(c.getName().toUpperCase().contains("XBOX") && c.getName().contains("360")) {
						pad = c;
						break;
					}
				}
				if (pad != null) {
					this.controller = pad;
					this.controller.addListener(getPlayer());
				}
			}
		}
	}
	
	@Override
	public void onSwitchItem(Item item, int numberOfItems) {
		for (WorldListener listener : this.worldListeners) {
			listener.handleSwitchedItem(item, numberOfItems);
		}
	}
	

	@Override
	public void addHitSpark(HitSpark hitSpark) {
		this.hitSparks.add(hitSpark);
	}
	
	@Override
	public void deleteHitSpark(HitSpark hitSpark) {
		this.hitSparks.removeValue(hitSpark, true);		
	}
	
	@Override
	public void triggerSave() {
		this.saveController.save();
	}
	
	public Array<Rectangle> getHitBoxes() {
		return hitBoxes;
	}

	public Array<Projectile> getProjectiles() {
		return projectiles;
	}

	public Array<Rectangle> getAdditionalRectangles() {
		return collisionRectangles;
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

	public TiledMapTileLayer getCollisionLayer() {
		return this.collisionLayer;
	}

	public Array<HitSpark> getHitSparks() {
		return hitSparks;
	}



}