package com.mygdx.game.constants;

import com.mygdx.game.constants.InputConverter.DirectionalInput;

public class InputType {
	public static final String LEFT = "LEFT";
	public static final String UPLEFT = "UPLEFT";
	public static final String DOWNLEFT = "DOWNLEFT";
	public static final String RIGHT = "RIGHT";
	public static final String UPRIGHT = "UPRIGHT";
	public static final String DOWNRIGHT = "DOWNRIGHT";
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
	public static final String JUMPDOWNLEFT = "JUMPDOWNLEFT";
	public static final String JUMPDOWNRIGHT = "JUMPDOWNRIGHT";
	public static final String JUMPUPLEFT = "JUMPUPLEFT";
	public static final String JUMPUPRIGHT = "JUMPUPRIGHT";
//	public static final String ACTION = "ACTION";
	public static final String ACTIONCANCEL = "ACTIONCANCEL";
	public static final String SP = "SP";
	public static final String PLEFT = "PLEFT";
	public static final String PDOWN = "PDOWN";
	public static final String PDOWNLEFT = "PDOWNLEFT";
	public static final String PDOWNRIGHT = "PDOWNRIGHT";
	public static final String PUP = "PUP";
	public static final String PUPRIGHT = "PUPRIGHT";
	public static final String PUPLEFT = "PUPLEFT";
	public static final String PRIGHT = "PRIGHT";
	public static final String KLEFT = "KLEFT";
	public static final String KDOWN = "KDOWN";
	public static final String KDOWNLEFT = "KDOWNLEFT";
	public static final String KDOWNRIGHT = "KDOWNRIGHT";
	public static final String KUP = "KUP";
	public static final String KUPLEFT = "KUPLEFT";
	public static final String KUPRIGHT = "KUPRIGHT";
	public static final String KRIGHT = "KRIGHT";
	public static final String HLEFT = "HLEFT";
	public static final String HDOWN = "HDOWN";
	public static final String HDOWNLEFT = "HDOWNLEFT";
	public static final String HDOWNRIGHT = "HDOWNRIGHT";
	public static final String HUP = "HUP";
	public static final String HUPLEFT = "HUPLEFT";
	public static final String HUPRIGHT = "HUPRIGHT";
	public static final String HRIGHT = "HRIGHT";
	public static final String SLEFT = "SLEFT";
	public static final String SDOWN = "SDOWN";
	public static final String SDOWNLEFT = "SDOWNLEFT";
	public static final String SDOWNRIGHT = "SDOWNRIGHT";
	public static final String SUP = "SUP";
	public static final String SUPLEFT = "SUPLEFT";
	public static final String SUPRIGHT = "SUPRIGHT";
	public static final String SRIGHT = "SRIGHT";
	public static final String DASHLEFT = "DASHLEFT";
	public static final String DASHDOWN = "DASHDOWN";
	public static final String DASHDOWNRIGHT = "DASHDOWNRIGHT";
	public static final String DASHDOWNLEFT = "DASHDOWNLEFT";
	public static final String DASHUP = "DASHUP";
	public static final String DASHUPLEFT = "DASHUPLEFT";
	public static final String DASHUPRIGHT = "DASHUPRIGHT";
	public static final String DASHRIGHT = "DASHRIGHT";
	public static final String DASHRELEASE = "DASHRELEASE"; 
	public static final String SPLEFT = "SPLEFT";
	public static final String SPRIGHT = "SPRIGHT";
	public static final String SPUP = "SPUP"; 
	public static final String SPUPLEFT = "SPUPLEFT"; 
	public static final String SPUPRIGHT = "SPUPRIGHT"; 
	public static final String SPDOWN = "SPDOWN";
	public static final String SPDOWNLEFT = "SPDOWNLEFT";
	public static final String SPDOWNRIGHT = "SPDOWNRIGHT";
	
	public static String getFromDirection(DirectionalInput input) {
		switch(input) {
		case UP:
			return InputType.UP;
		case LEFT:
			return InputType.LEFT;
		case RIGHT:
			return InputType.RIGHT;
		case DOWN:
			return InputType.DOWN;
		case DOWNLEFT:
			return InputType.DOWNLEFT;
		case DOWNRIGHT:
			return InputType.DOWNRIGHT;
		case UPLEFT:
			return InputType.UPLEFT;
		case UPRIGHT:
			return InputType.UPRIGHT;
		default:
			return "";
		}
	}
	
	public static String flip(String type) {
		String newType = type;
		switch(type) {
		case LEFT:
			newType = RIGHT;
			break;
		case UPLEFT:
			newType = UPRIGHT;
			break;
		case UPRIGHT:
			newType = UPLEFT;
			break;
		case DOWNLEFT:
			newType = DOWNRIGHT;
			break;
		case DOWNRIGHT:
			newType = DOWNLEFT;
			break;
		case RIGHT:
			newType = LEFT;
			break;
		case PLEFT:
			newType = PRIGHT;
			break;
		case PUPLEFT:
			newType = PUPRIGHT;
			break;	
		case PDOWNLEFT:
			newType = PDOWNRIGHT;
			break;
		case PRIGHT:
			newType = PLEFT;
			break;
		case PUPRIGHT:
			newType = PUPLEFT;
			break;	
		case PDOWNRIGHT:
			newType = PDOWNLEFT;
			break;
		case SLEFT:
			newType = SRIGHT;
			break;
		case SUPLEFT:
			newType = SUPRIGHT;
			break;	
		case SDOWNLEFT:
			newType = SDOWNRIGHT;
			break;
		case SRIGHT:
			newType = SLEFT;
			break;
		case SUPRIGHT:
			newType = SUPLEFT;
			break;	
		case SDOWNRIGHT:
			newType = SDOWNLEFT;
			break;
		case HLEFT:
			newType = HRIGHT;
			break;
		case HUPLEFT:
			newType = HUPRIGHT;
			break;	
		case HDOWNLEFT:
			newType = HDOWNRIGHT;
			break;
		case HRIGHT:
			newType = HLEFT;
			break;
		case HUPRIGHT:
			newType = HUPLEFT;
			break;	
		case HDOWNRIGHT:
			newType = HDOWNLEFT;
			break;
		case KLEFT:
			newType = KRIGHT;
			break;
		case KUPLEFT:
			newType = KUPRIGHT;
			break;	
		case KDOWNLEFT:
			newType = KDOWNRIGHT;
			break;
		case KRIGHT:
			newType = KLEFT;
			break;
		case KUPRIGHT:
			newType = KUPLEFT;
			break;	
		case KDOWNRIGHT:
			newType = KDOWNLEFT;
			break;
		case DASHLEFT:
			newType = DASHRIGHT;
			break;
		case DASHUPLEFT:
			newType = DASHUPRIGHT;
			break;
		case DASHDOWNLEFT:
			newType = DASHDOWNRIGHT;
			break;
		case DASHRIGHT:
			newType = DASHLEFT;
			break;
		case DASHUPRIGHT:
			newType = DASHUPLEFT;
			break;
		case DASHDOWNRIGHT:
			newType = DASHDOWNLEFT;
			break;
		case SPLEFT:
			newType = SPRIGHT;
			break;
		case SPUPLEFT:
			newType = SPUPRIGHT;
			break;	
		case SPDOWNLEFT:
			newType = SPDOWNRIGHT;
			break;
		case SPRIGHT:
			newType = SPLEFT;
			break;
		case SPUPRIGHT:
			newType = SPUPLEFT;
			break;	
		case SPDOWNRIGHT:
			newType = SPDOWNLEFT;
			break;
		case JUMPLEFT:
			newType = JUMPRIGHT;
			break;
		case JUMPUPLEFT:
			newType = JUMPUPRIGHT;
			break;
		case JUMPDOWNLEFT:
			newType = JUMPDOWNRIGHT;
			break;
		case JUMPRIGHT: 
			newType = JUMPLEFT;
			break;
		case JUMPUPRIGHT:
			newType = JUMPUPLEFT;
			break;
		case JUMPDOWNRIGHT:
			newType = JUMPDOWNLEFT;
			break;
		default:
			break;		
		}
		return newType;
	}
}