package com.dmiranda.revert.shared;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Intersector.MinimumTranslationVector;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector2;

public class CollisionBox {
	
	private Polygon shape;
	
	public CollisionBox(float x, float y, float w, float h){
		
		float[] points = {
				0, 0,
				0 + w, 0,
				0 + w, 0 + h,
				0, 0 + h
		};
		
		shape = new Polygon(points);
		shape.setPosition(x, y);
		shape.setOrigin(w / 2, h / 2);
	}
	
	public void update(Vector2 position, float angle){
		
		shape.setPosition(position.x, position.y);
		shape.setRotation(angle);
		
	}
	
	public boolean intersects(CollisionBox other){
		
		return Intersector.overlapConvexPolygons(this.getShape(), other.getShape());
		
	}
	
	public boolean intersectsResult(CollisionBox other, MinimumTranslationVector minTranslation){
		
		return Intersector.overlapConvexPolygons(this.getShape(), other.getShape(), minTranslation);
	
	}
	
	public Polygon getShape(){ return shape; }
	
	public String toString(){
		return "X: " + shape.getX() + "  Y: " + shape.getY();
	}

}
