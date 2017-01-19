package com.mygdx.game.constants;

import com.badlogic.gdx.controllers.PovDirection;

//This code was taken from http://www.java-gaming.org/index.php?topic=29223.0
//With thanks that is!

public class XBox360Pad
{
 /*
  * It seems there are different versions of gamepads with different ID 
  Strings.
  * Therefore its IMO a better bet to check for:
  * if (controller.getName().toLowerCase().contains("xbox") &&
                controller.getName().contains("360"))
  *
  * Controller (Gamepad for Xbox 360)
    Controller (XBOX 360 For Windows)
    Controller (Xbox 360 Wireless Receiver for Windows)
    Controller (Xbox wireless receiver for windows)
    XBOX 360 For Windows (Controller)
    Xbox 360 Wireless Receiver
    Xbox Receiver for Windows (Wireless Controller)
    Xbox wireless receiver for windows (Controller)
  */
 //public static final String ID = "XBOX 360 For Windows (Controller)";
 public static final int BUTTON_X = 2;
 public static final int BUTTON_Y = 3;
 public static final int BUTTON_A = 0;
 public static final int BUTTON_B = 1;
 public static final int BUTTON_BACK = 6;
 public static final int BUTTON_START = 7;
 public static final PovDirection BUTTON_DPAD_UP = PovDirection.north;
 public static final PovDirection BUTTON_DPAD_DOWN = PovDirection.south;
 public static final PovDirection BUTTON_DPAD_RIGHT = PovDirection.east;
 public static final PovDirection BUTTON_DPAD_LEFT = PovDirection.west;
 public static final int BUTTON_LB = 4;
 public static final int BUTTON_L3 = 8;
 public static final int BUTTON_RB = 5;
 public static final int BUTTON_R3 = 9;
 public static final int AXIS_LEFT_X = 1; //-1 is left | +1 is right
 public static final int AXIS_LEFT_Y = 0; //-1 is up | +1 is down
 public static final int AXIS_LEFT_TRIGGER = 4; //value 0 to 1f
 public static final int AXIS_RIGHT_X = 3; //-1 is left | +1 is right
 public static final int AXIS_RIGHT_Y = 2; //-1 is up | +1 is down
 public static final int AXIS_RIGHT_TRIGGER = 4; //value 0 to -1f
 
 public static String buttonCodeToString (int buttonCode) {
	 switch (buttonCode) {
	 case BUTTON_X:
		 return "BUTTON_X";
	 case BUTTON_Y:
		 return "BUTTON_Y";
	 case BUTTON_A:
		 return "BUTTON_A";
	 case BUTTON_B:
		 return "BUTTON_B";
	 case BUTTON_BACK:
		 return "BUTTON_BACK";
	 case BUTTON_START:
		 return "BUTTON_START";
	 case BUTTON_LB:
		 return "BUTTON_LB";
	 case BUTTON_L3:
		 return "BUTTON_L3";
	 case BUTTON_RB:
		 return "BUTTON_RB";
	 default:
		 return "";
	 }
 }
 
 public static String axisCodeToString(int axisCode, float value) {
	 switch(axisCode) {
	 case AXIS_LEFT_X:
		 if (Math.abs(value) < 0.2f)
		 {
			 return "AXIS_LEFT_X_NONE";
		 }
		 else {
			 return value > 0 ? "AXIS_LEFT_X_RIGHT" : "AXIS_LEFT_X_LEFT";
		 }
	 case AXIS_LEFT_Y:
		 if (Math.abs(value) < 0.2f)
		 {
			 return "AXIS_LEFT_Y_NONE";
		 }
		 else {
			 return value > 0 ? "AXIS_LEFT_Y_DOWN" : "AXIS_LEFT_Y_UP";
		 }
	 case AXIS_RIGHT_X:
		 if (Math.abs(value) < 0.2f)
		 {
			 return "AXIS_RIGHT_X_NONE";
		 }
		 else {
			 return value > 0 ? "AXIS_RIGHT_X_RIGHT" :  "AXIS_RIGHT_X_LEFT";
		 }
	 case AXIS_RIGHT_Y:
		 if (Math.abs(value) < 0.2f)
		 {
			 return "AXIS_RIGHT_Y_NONE";
		 }
		 else {
			 return value > 0 ? "AXIS_RIGHT_Y_DOWN" : "AXIS_RIGHT_Y_UP";
		 }
	 case AXIS_RIGHT_TRIGGER:
		 return value > 0 ? "AXIS_LEFT_TRIGGER" : "AXIS_RIGHT_TRIGGER";
	 default:
		 return "";
	 }
 }
 
 public static String povCodeToString(PovDirection povCode) {
	 switch (povCode) {
	 case north:
		 return "BUTTON_DPAD_UP";
	 case south:
		 return "BUTTON_DPAD_DOWN";
	 case east:
		 return "BUTTON_DPAD_RIGHT";
	 case west: 
		 return "BUTTON_DPAD_LEFT";
	 default:
		 return "";
	 }
 }
}
