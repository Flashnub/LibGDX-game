package com.mygdx.game.assets;

public class SpriteUtils {
	
	public static final String left = "-Left";
	public static final String right = "-Right";
	//Used for animationStringWithData.
	public static final String windupState = "W";
	public static final String activeState = "A";
	public static final String cooldownState = "C";
	
	public static String animationStringWithData(AnimationData animationData,  String animationState, int index) {
		return animationData.getName() + animationState + index;
	}
	
	public static String animationStringWithState(String state, boolean isFacingLeft) {
		return state + (isFacingLeft ? SpriteUtils.left : SpriteUtils.right);
	}
	
	public static String animationStringWithSizeType(String size, String type, boolean isFacingLeft) {
		return type + size + (isFacingLeft ? SpriteUtils.left : SpriteUtils.right);
	}
}
