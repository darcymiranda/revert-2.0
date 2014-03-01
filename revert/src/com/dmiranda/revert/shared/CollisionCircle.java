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

    public Vector2 collisionDepth(CollisionCircle collisionCircle){

        Circle circle = collisionCircle.getShape();
        float d1 = shape.radius * 0.5f;
        float d2 = circle.radius * 0.5f;

        Vector2 sep = new Vector2(
                shape.x + d1,
                shape.y + d1
        ).sub(new Vector2(
                circle.x + d2,
                circle.y + d2
        ));

        float rd = shape.radius + circle.radius;

        if(sep.len2() > rd * rd){
            return new Vector2(0, 0);
        }
        else {
            return sep.cpy().nor().scl(rd - sep.len());
        }
    }

	public boolean intersects(CollisionCircle other) {
		
		return Intersector.overlaps(this.getShape(), other.getShape());
		
	}
	
	public Circle getShape(){ return shape; }
	
}
