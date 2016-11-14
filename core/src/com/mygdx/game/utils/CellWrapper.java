package com.mygdx.game.utils;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;

public class CellWrapper {
	private Cell cell;
	private Vector2 origin;
	
	public CellWrapper(Cell cell, Vector2 origin) {
		this.cell = cell;
		this.origin = origin;
	}

	public Cell getCell() {
		return cell;
	}

	public Vector2 getOrigin() {
		return origin;
	}
	
	
}
