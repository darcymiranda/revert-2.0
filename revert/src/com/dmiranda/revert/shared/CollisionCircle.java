package com.dmiranda.revert.shared;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Vector2;

public class CollisionCircle {
	
	private Circle shape;
	
	public CollisionCircle(float x, float y, float r){
		
		shape = new Circle(x, y, r);
		
	}
	
	public void update(Vector2 position, float angle){
		
		shape.x = position.x;
		shape.y = position.y;
		
	}

	public boolean intersects(CollisionCircle other) {
		
		return Intersector.overlapCircles(this.getShape(), other.getShape());
		
	}
	
	public Circle getShape(){ return shape; }
	
}
