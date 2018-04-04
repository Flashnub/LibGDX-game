package com.mygdx.game.assets;

import com.mygdx.game.model.hitSpark.HitSparkData;

public class HitSparkUtils {
	
	//Constants
	private final static String defaultName = "Default";
	private final static String blockName = "Block";
	private final static String defaultSize = "Medium";
	
	
	public static HitSparkData blockData(String size) {
		return new HitSparkData(blockName, size);
	}
	
	public static HitSparkData defaultData() {
		return new HitSparkData(defaultName, defaultSize);
	}
}
