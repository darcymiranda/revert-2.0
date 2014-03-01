package com.dmiranda.revert.shared;

import com.badlogic.gdx.math.MathUtils;

public class Asteroid extends Entity {
	
	private int asteroidType;

	public Asteroid(int type, float x, float y, float r) {
		super(x, y, 64, 64);

        asteroidType = type;
		createCollisionCircle(width * 0.5f);
        setCollisionMass(500000);
		setRotation(r);
		
	}
	
	public void randAsteroidType(){
		asteroidType = MathUtils.random(63);
	}
	
	@Override
	public void update(float delta){
        super.update(delta);
	}
	
	public int getAsteroidType(){ return asteroidType; }
}
