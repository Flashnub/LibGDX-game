package com.mygdx.game.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.mygdx.game.model.world.WorldModel;
import com.mygdx.game.renderer.HUDViewModel;
import com.mygdx.game.renderer.WorldViewModel;


public class GameScreen implements Screen{
	
	WorldModel worldModel;
   	TiledMap tiledMap;

	WorldViewModel renderer;
	HUDViewModel uiRenderer;
	
    public GameScreen(TiledMap tiledMap, WorldModel worldModel) {
    	this.tiledMap = tiledMap;
    	this.worldModel = worldModel;
    	renderer = new WorldViewModel(tiledMap, worldModel);
    	uiRenderer = new HUDViewModel(worldModel, renderer);
    }

    @Override
    public void render(float delta) {
    	renderer.render(delta);
    	uiRenderer.render(delta);
    }

	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
}
