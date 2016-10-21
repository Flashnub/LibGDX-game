package com.mygdx.game.assets;

public class SpriteUtils {
	
	public static final String left = "-Left";
	public static final String right = "-Right";
	
	public static String animationStringWithData(String characterName, AnimationData animationData, int index) {
		return characterName + "-" + animationData.getName() + index;
	}
	
	public static String animationStringWithState(String characterName, String state, boolean isFacingLeft) {
		return characterName + "-" + state + (isFacingLeft ? SpriteUtils.left : SpriteUtils.right);
	}
	
	public static String animationStringWithState(String characterName, String state) {
		return characterName + "-" + state;
	}
}
