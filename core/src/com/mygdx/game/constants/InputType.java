package com.mygdx.game.constants;

public class InputType {
	public static final String LEFT = "LEFT";
	public static final String RIGHT = "RIGHT";
	public static final String UP = "UP"; 
	public static final String DOWN = "DOWN";
	public static final String P = "P";
	public static final String S = "S";
	public static final String K = "K";
	public static final String H = "H"; 
	public static final String DASH = "DASH";
	public static final String USEITEM = "USEITEM";
	public static final String JUMP = "JUMP";
	public static final String JUMPRIGHT = "JUMPRIGHT";
	public static final String JUMPLEFT = "JUMPLEFT";
	public static final String JUMPUP = "JUMPUP";
	public static final String JUMPDOWN = "JUMPDOWN";
//	public static final String ACTION = "ACTION";
	public static final String ACTIONCANCEL = "ACTIONCANCEL";
	public static final String SP = "SP";
	public static final String PLEFT = "PLEFT";
	public static final String PDOWN = "PDOWN";
	public static final String PUP = "PUP";
	public static final String PRIGHT = "PRIGHT";
	public static final String KLEFT = "KLEFT";
	public static final String KDOWN = "HSDOWN";
	public static final String KUP = "KUP";
	public static final String KRIGHT = "KRIGHT";
	public static final String HLEFT = "HLEFT";
	public static final String HDOWN = "HDOWN";
	public static final String HUP = "HUP";
	public static final String HRIGHT = "HRIGHT";
	public static final String SLEFT = "SLEFT";
	public static final String SDOWN = "SDOWN";
	public static final String SUP = "SUP";
	public static final String SRIGHT = "SRIGHT";
	public static final String DASHLEFT = "DASHLEFT";
	public static final String DASHDOWN = "DASHDOWN";
	public static final String DASHUP = "DASHUP";
	public static final String DASHRIGHT = "DASHRIGHT";
	public static final String DASHRELEASE = "DASHRELEASE"; 
	public static final String SPLEFT = "SPLEFT";
	public static final String SPRIGHT = "SPRIGHT";
	public static final String SPUP = "SPUP"; 
	public static final String SPDOWN = "SPDOWN";
	
	
	public static String flip(String type) {
		String newType = type;
		switch(type) {
		case LEFT:
			newType = RIGHT;
			break;
		case RIGHT:
			newType = LEFT;
			break;
		case PLEFT:
			newType = PRIGHT;
			break;
		case PRIGHT:
			newType = PLEFT;
			break;
		case SLEFT:
			newType = SRIGHT;
			break;
		case SRIGHT:
			newType = SLEFT;
			break;
		case HLEFT:
			newType = HRIGHT;
			break;
		case HRIGHT:
			newType = HLEFT;
			break;
		case KLEFT:
			newType = KRIGHT;
			break;
		case KRIGHT:
			newType = KLEFT;
			break;	
		case DASHLEFT:
			newType = DASHRIGHT;
			break;
		case DASHRIGHT:
			newType = DASHLEFT;
			break;
		case SPLEFT:
			newType = SPRIGHT;
			break;
		case SPRIGHT:
			newType = SPLEFT;
			break;
		case JUMPLEFT:
			newType = JUMPRIGHT;
			break;
		case JUMPRIGHT: 
			newType = JUMPLEFT;
			break;
		default:
			break;		
		}
		return newType;
	}
}