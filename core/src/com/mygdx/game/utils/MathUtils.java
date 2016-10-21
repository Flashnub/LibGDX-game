package com.mygdx.game.utils;

import com.badlogic.gdx.math.Vector2;

public class MathUtils {
	public static Vector2 solveQuadratic(float quadraticA, float quadraticB, float quadraticC) {
		Vector2 solution = new Vector2();
		if (Math.abs(quadraticA) < 1e-6){
			if (Math.abs(quadraticB) < 1e-6) {
				solution = new Vector2(0, 0);
			}
			else {
				solution = new Vector2(-quadraticC/quadraticB, -quadraticC/quadraticB);
			}
		}
		else {
			double disc = (quadraticB * quadraticB) - (4 * quadraticA * quadraticC);
			if (disc >= 0) {
				disc = Math.sqrt(disc);
				float xSolution = ((float) (-quadraticB-disc)/(2*quadraticA));
				float ySolution = ((float) (-quadraticB+disc)/(2*quadraticA));
				solution = new Vector2(xSolution, ySolution);
			}
		}
		
		return solution;
	}
	
}
