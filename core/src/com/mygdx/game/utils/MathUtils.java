package com.mygdx.game.utils;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
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
	
	public static Polygon rectangleToPolygon(Rectangle rectangle, float velocityAngle) {
		Polygon polygon = new Polygon(new float[] {
				rectangle.x, rectangle.y,
				rectangle.x, rectangle.y + rectangle.height,
				rectangle.x + rectangle.width, rectangle.y + rectangle.height,
				rectangle.x + rectangle.width, rectangle.y
			});
			polygon.setOrigin(rectangle.x + rectangle.width / 2, rectangle.y + rectangle.height / 2);
			polygon.setRotation(velocityAngle);
			return polygon;
	}
}
