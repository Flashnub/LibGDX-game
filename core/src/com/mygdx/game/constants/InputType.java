package com.mygdx.game.constants;

public class InputType {
	public static final String LEFT = "LEFT";
	public static final String RIGHT = "RIGHT";
	public static final String UP = "UP"; 
	public static final String DOWN = "DOWN";
	public static final String LIGHTATTACK = "LIGHTATTACK";
	public static final String MEDIUMATTACK = "MEDIUMATTACK";
	public static final String HEAVYATTACK = "HEAVYATTACK"; 
	public static final String MOVEMENT = "MOVEMENT";
	public static final String USEITEM = "USEITEM";
	public static final String JUMP = "JUMP";
//	public static final String ACTION = "ACTION";
	public static final String ACTIONCANCEL = "ACTIONCANCEL";
	public static final String SPECIAL = "SPECIAL";
	public static final String LEFTLIGHTATTACK = "LEFTLIGHTATTACK";
	public static final String DOWNLIGHTATTACK = "DOWNLIGHTATTACK";
	public static final String UPLIGHTATTACK = "UPLIGHTATTACK";
	public static final String RIGHTLIGHTATTACK = "RIGHTLIGHTATTACK";
	public static final String LEFTHEAVYATTACK = "LEFTHEAVYATTACK";
	public static final String DOWNHEAVYATTACK = "DOWNHEAVYATTACK";
	public static final String UPHEAVYATTACK = "UPHEAVYATTACK";
	public static final String RIGHTHEAVYATTACK = "RIGHTHEAVYATTACK";
	public static final String LEFTMEDIUMATTACK = "LEFTMEDIUMATTACK";
	public static final String DOWNMEDIUMATTACK = "DOWNMEDIUMATTACK";
	public static final String UPMEDIUMATTACK = "UPMEDIUMATTACK";
	public static final String RIGHTMEDIUMATTACK = "RIGHTMEDIUMATTACK";
	public static final String LEFTMOVEMENT = "LEFTMOVEMENT";
	public static final String DOWNMOVEMENT = "DOWNMOVEMENT";
	public static final String UPMOVEMENT = "UPMOVEMENT";
	public static final String RIGHTMOVEMENT = "RIGHTMOVEMENT";
	public static final String MOVEMENTRELEASE = "MOVEMENTRELEASE"; 
	public static final String LEFTSPECIAL = "LEFTSPECIAL";
	public static final String RIGHTSPECIAL = "RIGHTSPECIAL";
	public static final String UPSPECIAL = "UPSPECIAL"; 
	public static final String DOWNSPECIAL = "DOWNSPECIAL";
	
	
	public static String flip(String type) {
		String newType = type;
		switch(type) {
		case LEFT:
			newType = RIGHT;
			break;
		case RIGHT:
			newType = LEFT;
			break;
		case LEFTLIGHTATTACK:
			newType = RIGHTLIGHTATTACK;
			break;
		case RIGHTLIGHTATTACK:
			newType = LEFTLIGHTATTACK;
			break;
		case LEFTMEDIUMATTACK:
			newType = RIGHTMEDIUMATTACK;
			break;
		case RIGHTMEDIUMATTACK:
			newType = LEFTMEDIUMATTACK;
			break;
		case LEFTHEAVYATTACK:
			newType = RIGHTHEAVYATTACK;
			break;
		case RIGHTHEAVYATTACK:
			newType = LEFTHEAVYATTACK;
			break;
		case LEFTMOVEMENT:
			newType = RIGHTMOVEMENT;
			break;
		case RIGHTMOVEMENT:
			newType = LEFTMOVEMENT;
			break;
		case LEFTSPECIAL:
			newType = RIGHTSPECIAL;
			break;
		case RIGHTSPECIAL:
			newType = LEFTSPECIAL;
			break;
		default:
			break;		
		}
		return newType;
	}
}