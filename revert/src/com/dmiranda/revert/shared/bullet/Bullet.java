package com.dmiranda.revert.shared.bullet;

import com.badlogic.gdx.math.MathUtils;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.weapon.Weapon;


public abstract class Bullet extends Entity {
	
	protected int decay;
	protected float speed;
	protected Weapon weapon;

	public Bullet(Weapon weapon, float x, float y, int width, int height, float speed, float direction) {
		super(x, y, width, height);
		
		createCollisionCircle();
		
		position = weapon.getRelativeLocation();
		position.x -= width / 2;
		position.y -= height / 2;
		
		this.weapon = weapon;
		this.speed = speed;
		this.rotation = direction;
		
		ownerEntity = weapon.getOwner();
		decay = 5000;
		setSpeed(speed, direction);
		
	}
	
	@Override
	public void update(float delta){
		super.update(delta);
		
		if(decay < 0){
			kill(null);
		}else{
			decay -= delta * 1000;
		}
		
	}
	
	@Override
	public void onHit(Entity hitter){
		super.onHit(hitter);
		
		kill(hitter);
		
	}
	
	public float getDamage(){
		return weapon.getDamageRoll();
	}
	
	public abstract Bullet newInstance(int damage, float direction, float speed);

	public void setSpeed(float speed, float direction){
		this.speed = speed;
		
		velocity.x = -(speed * (float) MathUtils.sinDeg(direction)) + weapon.getOwner().getVelocity().x;
		velocity.y = speed * (float) MathUtils.cosDeg(direction) + weapon.getOwner().getVelocity().y;
	}
	
	
}
