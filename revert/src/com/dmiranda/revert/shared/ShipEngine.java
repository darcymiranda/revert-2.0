package com.dmiranda.revert.shared;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.GameWorldClient;
import com.dmiranda.revert.Revert;

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
	}
	
	public void render(SpriteBatch sb){
		
		posOffset.x = -MathUtils.sinDeg(owner.getRotation() + 180) * 20.5f;
		posOffset.y = MathUtils.cosDeg(owner.getRotation() + 180) * 20.5f;

		if(owner.getEntityActionState().getCurrentState() == EntityActionState.STATE_ON){
			
			if(GameWorldClient.particleSystem.getEffectsFollow().containsKey(this)){
				GameWorldClient.particleSystem.getEffectsFollow().get(this).getEffect().start();
			}
		
			TextureRegion engineFlame = Revert.animations.get("fighter-engine").getKeyFrame(owner.getEntityActionState().getStateTime(), true);
			TextureRegion engineLight = Revert.animations.get("fighter-engine-light").getKeyFrame(owner.getEntityActionState().getStateTime(), true);
			
			sb.draw(engineFlame,
					owner.getCenterX() + posOffset.x - engineFlame.getRegionWidth() / 2,
					owner.getCenterY() + posOffset.y - engineFlame.getRegionHeight() / 2,
					engineFlame.getRegionWidth() / 2,
					engineFlame.getRegionHeight() / 2,
					engineFlame.getRegionWidth(),
					engineFlame.getRegionHeight(),
					1, 1,
					owner.getRotation());
			
			sb.draw(engineLight,
					owner.getCenterX() + posOffset.x - engineLight.getRegionWidth() / 2,
					owner.getCenterY() + posOffset.y - engineLight.getRegionHeight() / 2,
					engineLight.getRegionWidth() / 2,
					engineLight.getRegionHeight() / 2,
					engineLight.getRegionWidth(),
					engineLight.getRegionHeight(),
					1, 1,
					owner.getRotation());
			
		}else{
			
			if(GameWorldClient.particleSystem.getEffectsFollow().containsKey(this)){
				GameWorldClient.particleSystem.getEffectsFollow().get(this).getEffect().allowCompletion();
			}
		}

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
