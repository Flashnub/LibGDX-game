package com.mygdx.game.model.characters;

public class ObservableData {
	ObservableDataType dataType;
	int weight;
	
	public ObservableData(ObservableDataType dataType, int weight) {
		this.dataType = dataType;
		this.weight = weight;
	}

	public ObservableDataType getDataType() {
		return dataType;
	}

	public int getWeight() {
		return weight;
	}
	
	public enum ObservableDataType {
		Distance, MeleeOffense, RangedOffense, Defend 
	}

}
