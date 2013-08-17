package com.dmiranda.revert;

import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.shared.Entity;

public class EntityState {
	
	public Vector2 position;
	public Vector2 velocity;
	
	public float rotation;
	
	public EntityState(Entity entity){
		position = new Vector2(entity.getPosition());
		velocity = new Vector2(entity.getVelocity());
		rotation = entity.getRotation();
	}
	
	public EntityState(Vector2 position, Vector2 velocity, float rotation){
		this.position = new Vector2(position);
		this.velocity = new Vector2(velocity);
		this.rotation = rotation;
	}
	
	public void update(float delta){
		position.x += velocity.x * delta;
		position.y += velocity.y * delta;
	}
	
	public void setState(EntityState state){
		position = state.getPosition();
		velocity = state.getVelocity();
		rotation = state.getRotation();
	}

	public void setPosition(float x, float y){ position.x = x; position.y = y; }
	public void setVelocity(float x, float y){ velocity.x = x; velocity.y = y; }
	public void setRotation(float r){ rotation = r; }
	
	public Vector2 getPosition(){ return new Vector2(position); }
	public Vector2 getVelocity(){ return new Vector2(velocity); }
	public float getRotation(){ return rotation; }
	
	public String toString(){
		return "Position: " + position + "  Velocity: " + velocity + "  Rotation: " + rotation;
	}

}
