package com.dmiranda.revert.shared;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class ShipEngine {
	
	private float acceleration;
	private float topSpeed;
	
	private Ship owner;
	
	private Vector2 posOffset;
	
	public ShipEngine(Ship owner, float locationOffset, float acceleration, float topSpeed){
		this.owner = owner;
		this.acceleration = acceleration;
		this.topSpeed = topSpeed;
		
		posOffset = new Vector2();
		posOffset.x = -MathUtils.sinDeg(owner.getRotation() + 180) * 20.5f;
		posOffset.y = MathUtils.cosDeg(owner.getRotation() + 180) * 20.5f;
		
	}
	
	public Vector2 getForwardsThrust(){
		
		return calculateVelocity(owner.getRotation());
		
	}
	
	private Vector2 calculateVelocity(float angle){
	
		Vector2 velocity = new Vector2();
		
		velocity.x = acceleration * (float) MathUtils.sinDeg(angle);
		velocity.y = acceleration * (float) MathUtils.cosDeg(angle);
		
		float dirSpeed = (float) Math.sqrt(Math.abs(velocity.x * velocity.x + velocity.y * velocity.y));
		
		if (dirSpeed >= topSpeed) {
			velocity.x *= topSpeed / dirSpeed;
			velocity.y *= topSpeed / dirSpeed;
		}
		
		return velocity;
		
	}
	
	public Ship getOwner(){ return owner; }
	

}
