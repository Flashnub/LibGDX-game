package com.mygdx.game.utils;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class CellWrapper {
	private Cell cell;
	private Vector2 origin;
	CellType cellType;
	float tileWidth;
	float tileHeight;
	
	public enum CellType {
		Left, Right, Top, Bottom
	}
	
	public CellWrapper(Cell cell, Vector2 origin, CellType cellType, float tileWidth, float tileHeight) {
		this.cell = cell;
		this.origin = origin;
		this.cellType = cellType;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
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
	
	public Rectangle getRectangleForCell() {
		return new Rectangle(origin.x * tileWidth, origin.y * tileHeight, tileWidth, tileHeight);
	}
}
