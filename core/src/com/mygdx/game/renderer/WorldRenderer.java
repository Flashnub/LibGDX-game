package com.mygdx.game.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.model.characters.Character;
import com.mygdx.game.model.characters.player.Player;
import com.mygdx.game.model.projectiles.Explosion;
import com.mygdx.game.model.projectiles.Projectile;
import com.mygdx.game.model.world.WorldModel;
import com.mygdx.game.model.worldObjects.WorldObject;
import com.mygdx.game.views.ResourceBar;

public class WorldRenderer implements CoordinatesHelper{
	
		//Game world sprite stuff
		SpriteBatch gameBatch;
		WorldModel worldModel;
	
		//Tiled map stuff
	   	TiledMap tiledMap;
	   	OrthographicCamera camera;
	   	ShapeRenderer debugRenderer = new ShapeRenderer();
	    TiledMapRenderer tiledMapRenderer;
	
	    public WorldRenderer(TiledMap tiledMap, WorldModel worldModel) {
	        float w = Gdx.graphics.getWidth();
	        float h = Gdx.graphics.getHeight();

	        camera = new OrthographicCamera();
	        camera.setToOrtho(false,w,h);
	        camera.update();
	        
	    	tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
	    	this.tiledMap = tiledMap;
	    	this.worldModel = worldModel;
	    	gameBatch = new SpriteBatch();
	    }
	    
	    public void render (float delta) {
	        worldModel.act(delta);

	        Gdx.gl.glClearColor(1, 0, 0, 1);
	        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
	        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	        
			camera.position.set(
					worldModel.getPlayer().getCharacterData().getImageHitBox().getX() + 
					worldModel.getPlayer().getCharacterData().getImageHitBox().getWidth() / 2, 
					worldModel.getPlayer().getCharacterData().getImageHitBox().getY() + 
					worldModel.getPlayer().getCharacterData().getImageHitBox().getHeight() / 2, 0);
			if (camera.position.x < camera.viewportWidth / 2)
				camera.position.x = camera.viewportWidth / 2;
			else if (camera.position.x > ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getTileWidth() * ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getWidth() - camera.viewportWidth / 2)
				camera.position.x = ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getTileWidth() * ((TiledMapTileLayer) tiledMap.getLayers().get(0)).getWidth() - camera.viewportWidth / 2;
			if (camera.position.y < camera.viewportHeight / 2)
				camera.position.y = camera.viewportHeight / 2;

	        
	        camera.update();
	        tiledMapRenderer.setView(camera);
	        tiledMapRenderer.render();
	        
	        debugRenderer.setProjectionMatrix(camera.combined);
	        debugRenderer.begin(ShapeType.Line);
	        debugRenderer.rect(worldModel.getPlayer().getCharacterData().getGameplayHitBox().x, 
	        		worldModel.getPlayer().getCharacterData().getGameplayHitBox().y, 
	        		worldModel.getPlayer().getCharacterData().getGameplayHitBox().width, 
	        		worldModel.getPlayer().getCharacterData().getGameplayHitBox().height);
	        for (Projectile projectile : worldModel.getProjectiles()) {
		        debugRenderer.rect(projectile.getImageHitBox().x, 
		        		projectile.getImageHitBox().y, 
		        		projectile.getImageHitBox().width, 
		        		projectile.getImageHitBox().height);
	        }
	        for (Rectangle rectangle : worldModel.getAdditionalRectangles()) {
	        	debugRenderer.rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	        }
	        debugRenderer.end();
	        
	        gameBatch.setProjectionMatrix(camera.combined);
	        gameBatch.begin();
	        drawNPCs(gameBatch);
	        drawPlayer(gameBatch);
	        drawEnemies(gameBatch);
	        drawProjectiles(gameBatch);
	        drawWorldObjects(gameBatch);
	        drawExplosions(gameBatch);
	        gameBatch.end();
	    }
	    
	    private void drawPlayer(SpriteBatch batch) {
	    	Player player = worldModel.getPlayer();
    		TextureRegion texture = player.getCharacterUIData().getCurrentFrame();
    		if (texture != null) {
		    	batch.draw(texture,
		    		player.getCharacterData().getImageHitBox().x,  
	    			player.getCharacterData().getImageHitBox().y,  
	    			Math.round(player.getCharacterData().getImageHitBox().width), 
	    			Math.round(player.getCharacterData().getImageHitBox().height));
    		}
	    }
	    
	    private void drawNPCs(SpriteBatch batch) {
	    	for (Character npc : worldModel.getNpcCharacters()) {
	    		TextureRegion texture = npc.getCharacterUIData().getCurrentFrame();
	    		if (texture != null) {
			    	batch.draw(texture,
		    			npc.getCharacterData().getImageHitBox().x,  
		    			npc.getCharacterData().getImageHitBox().y,  
		    			npc.getCharacterData().getImageHitBox().width, 
		    			npc.getCharacterData().getImageHitBox().height);
	    		}
	    	}
	    	
	    }
	    
	    private void drawEnemies(SpriteBatch batch) {
	    	for (Character enemy : worldModel.getEnemies()) {
	    		TextureRegion texture = enemy.getCharacterUIData().getCurrentFrame();
	    		if (texture != null) {
			    	batch.draw(texture, 
			    			enemy.getCharacterData().getImageHitBox().x,
			    			enemy.getCharacterData().getImageHitBox().y,  
			    			enemy.getCharacterData().getImageHitBox().width, 
			    			enemy.getCharacterData().getImageHitBox().height);
	    		}
	    	}
	    	
	    }
	    
	    private void drawProjectiles(SpriteBatch batch) {
	    	for (Projectile projectile : worldModel.getProjectiles()) {
	    		TextureRegion texture = projectile.getProjectileUIModel().getCurrentFrame();
	    		float angleOfRotation = projectile.getProjectileUIModel().getAngleOfRotation();
	    		if (texture != null) {
	    			batch.draw(texture, 
	    					projectile.getImageHitBox().x, 
	    					projectile.getImageHitBox().y, 
	    					projectile.getImageHitBox().width / 2f, 
	    					projectile.getImageHitBox().height / 2f, 
	    					projectile.getImageHitBox().width, 
	    					projectile.getImageHitBox().height, 
	    					1f, 
	    					1f,
	    					angleOfRotation);
	    		}
	  
		    }
	    }
	    
	    private void drawExplosions(SpriteBatch batch) {
	    	for (Explosion explosion : worldModel.getExplosions()) {
	    		TextureRegion texture = explosion.getExplosionUIModel().getCurrentFrame();
	    		if (texture != null) {
			    	batch.draw(texture, 
			    			explosion.getImageHitBox().x,
			    			explosion.getImageHitBox().y,  
			    			explosion.getImageHitBox().width, 
			    			explosion.getImageHitBox().height);
	    		}
		    }
	    }
	    
	    private void drawWorldObjects(SpriteBatch batch) {
	    	for (WorldObject object : worldModel.getObjects()) {
		    	batch.draw(object.getItemUIModel().getCurrentFrame(), 
		    			object.getImageHitBox().x,
		    			object.getImageHitBox().y,  
		    			object.getImageHitBox().width, 
		    			object.getImageHitBox().height);
	    	}
	    }

		public WorldModel getWorldModel() {
			return worldModel;
		}

		@Override
		public Vector2 getScreenCoordinatesForCharacter(Character character) {
			// TODO Auto-generated method stub
			float xOffset = (character.getCharacterData().getImageHitBox().x + (character.getCharacterData().getImageHitBox().width / 2)) - (camera.position.x - (camera.viewportWidth / 2)) - (ResourceBar.enemyHealthBarWidth / 4);
			float yOffset = (character.getCharacterData().getImageHitBox().y + (character.getCharacterData().getImageHitBox().height)) - (camera.position.y  - (camera.viewportHeight / 2));
//			float yOffset = 150f;
			
			Vector2 position = new Vector2(xOffset, yOffset);
			return position;
		}
}
