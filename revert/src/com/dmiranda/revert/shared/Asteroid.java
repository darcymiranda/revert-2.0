package com.dmiranda.revert.shared;

import com.badlogic.gdx.math.MathUtils;

public class Asteroid extends Entity {
	
	private int asteroidType;

	public Asteroid(float type, float x, float y, float r) {
		super(x, y, 64, 64);
		
		createCollisionCircle();
		
		setRotation(r);
		
	}
	
	public void randAsteroidType(){
		asteroidType = MathUtils.random(64);
	}
	
	@Override
	public void update(float delta){
	}
	
	public int getAsteroidType(){ return asteroidType; }
}
