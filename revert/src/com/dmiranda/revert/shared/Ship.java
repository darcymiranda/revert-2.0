package com.dmiranda.revert.shared;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.GameWorldClient;
import com.dmiranda.revert.Revert;

public class Ship extends Unit {
	
	private boolean w,a,s,d;
	
	private float acceleration, topSpeed;
	
	private Vector2 engineOffset;
	private ShipEngine shipEngine;
	
	public Ship(Player owner, float x, float y, int width, int height) {
		super(x, y, width, height);
		
		setOwnerPlayer(owner);
		
		engineOffset = new Vector2();
		shipEngine = new ShipEngine(this, 20.5f, 5.5f, 350f);
		
		
	}
	
	@Override
	public void update(float delta){
		super.update(delta);
		
		if(w || s || a || d){
			actionState.changeState(EntityActionState.STATE_ON);
			if(w){
				setDirectionVelocity(acceleration, topSpeed);
			}
			else if(s){
				setDirectionVelocity(-(acceleration / 2), topSpeed);
			}
			
			if(a){
				setDirectionVelocity(acceleration / 2, topSpeed, rotation-90);
			}
			else if(d){
				setDirectionVelocity(acceleration / 2, topSpeed, rotation+90);
			}
		}
		else{
			actionState.changeState(EntityActionState.STATE_OFF);
		}
		
		engineOffset.x = -MathUtils.sinDeg(getRotation() + 180) * 20.5f;
		engineOffset.y = MathUtils.cosDeg(getRotation() + 180) * 20.5f;
		
		
	}
	
	@Override
	public void render(SpriteBatch sb){
		super.render(sb);
		
		shipEngine.render(sb);
		
		// TODO: Draw as gui so nothing goes over top
		// Draw name
		String name = getOwnerPlayer().username;
	
		if(getOwnerPlayer().team == 0)
			Revert.tFont.setColor(Color.RED);
		else if(getOwnerPlayer().team == 1)
			Revert.tFont.setColor(Color.BLUE);
		
		Revert.tFont.draw(sb, name, 
							getCenterX() - Revert.tFont.getSpaceWidth() * name.length(),
							getPosition().y + getHeight() + 5);
		
		Revert.tFont.setColor(Color.WHITE);
		
	}
	
	private void setDirectionVelocity(float acceleration, float maxSpeed){
		setDirectionVelocity(acceleration, maxSpeed, rotation);
	}
	
	private void setDirectionVelocity(float acceleration, float maxSpeed, float angle) {
		
		velocity.x -= acceleration * (float) MathUtils.sinDeg(angle);
		velocity.y += acceleration * (float) MathUtils.cosDeg(angle);
		
		float dirSpeed = (float) Math.sqrt(Math.abs(velocity.x * velocity.x + velocity.y * velocity.y));
		
		if (dirSpeed >= maxSpeed) {
			velocity.x *= maxSpeed / dirSpeed;
			velocity.y *= maxSpeed / dirSpeed;
			
		}
	}
	
	public void setParameters(float health, float rotationSpeed, float acceleration, float topSpeed){
		super.setParameters(health, rotationSpeed);
		this.acceleration = acceleration;
		this.topSpeed = topSpeed;
	}

	public void moveUp(boolean w){ this.w = w; }
	public void moveDown(boolean s){ this.s = s; }
	public void moveRight(boolean d){ this.d = d; }
	public void moveLeft(boolean a){ this.a = a; }
	
	public boolean getW(){ return w; }
	public boolean getS(){ return s; }
	public boolean getD(){ return d; }
	public boolean getA(){ return a; }
	
	public Vector2 getEngineOffset(){ return engineOffset; }

}
