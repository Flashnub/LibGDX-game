package com.mygdx.game.assets;

public class SpriteUtils {
	
	public static final String left = "-Left";
	public static final String right = "-Right";
	
	public static String animationStringWithData(AnimationData animationData, int index) {
		return animationData.getName() + index;
	}
	
	public static String animationStringWithState(String state, boolean isFacingLeft) {
		return state + (isFacingLeft ? SpriteUtils.left : SpriteUtils.right);
	}
}
