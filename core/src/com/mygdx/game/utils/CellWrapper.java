package com.mygdx.game.utils;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Vector2;

public class CellWrapper {
	private Cell cell;
	private Vector2 origin;
	CellType cellType;
	
	public enum CellType {
		Left, Right, Top, Bottom
	}
	
	public CellWrapper(Cell cell, Vector2 origin, CellType cellType) {
		this.cell = cell;
		this.origin = origin;
		this.cellType = cellType;
	}

	public Cell getCell() {
		return cell;
	}

	public Vector2 getOrigin() {
		return origin;
	}

	public CellType cellType() {
		return cellType;
	}
}
