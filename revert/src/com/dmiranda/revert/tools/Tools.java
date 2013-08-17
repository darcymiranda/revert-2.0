package com.dmiranda.revert.tools;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Tools {

	public static Vector2 lerp(Vector2 a, Vector2 b, float r){
		float ax = a.x, ay = a.y;
		float bx = b.x, by = b.y;
		return new Vector2(ax + (bx - ax) * r, ay + (by - ay) * r);
	}
	
	public static float lerp(float a, float b, float r){
		return a + (b - a) * r;
	}
	
	public static float getRotationToFaceTarget(Vector2 source, Vector2 target){
		float r = (float) -(Math.atan2(target.x - source.x, target.y - source.y) * (180 / Math.PI));
		if(r < 0) r += 360;
		if(r > 360) r -= 360;
		return r;
	}
	
	public static Vector2 calcMinTranslationDistance(Rectangle rect1, Rectangle rect2){
		
		float difference;
		float minTranslateDistance;
		short axis;
		short side;
		Vector2 translatedDifference = new Vector2(0,0);
		
		// Left
		difference = (rect1.getX() + rect1.getWidth()) - (rect2.getX());
		minTranslateDistance = difference;
		axis = 0;
		side = -1;
		
		// Right
		difference = (rect2.getX() + rect2.getWidth()) - rect1.getX();
		if(difference < minTranslateDistance){
			minTranslateDistance = difference;
			axis = 0;
			side = 1;
		}
		
		// Down
		difference = (rect1.getY() + rect1.getHeight()) - rect2.getY();
		if(difference < minTranslateDistance){
			minTranslateDistance = difference;
			axis = 1;
			side = -1;
		}
		
		// Up
		difference = (rect2.getY() + rect2.getHeight()) - rect1.getY();
		if(difference < minTranslateDistance){
			minTranslateDistance = difference;
			axis = 1;
			side = 1;
		}
		
		// Y
		if(axis == 1)	
			translatedDifference.y = side * minTranslateDistance;
		// X
		else
			translatedDifference.x = side * minTranslateDistance;
		
		return translatedDifference;
	}
}
