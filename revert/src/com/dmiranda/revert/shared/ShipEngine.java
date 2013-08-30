package com.dmiranda.revert.shared;

import box2dLight.Light;
import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.GameWorldClient;
import com.dmiranda.revert.LightFlicker;
import com.dmiranda.revert.Revert;

public class ShipEngine {
	
	private float acceleration;
	private float topSpeed;
    private float directionalSpeed;
	
	private Ship owner;
	private Vector2 posOffset;

    private LightFlicker light;
    private boolean lightFlicker;
    private float lightFlickerTick;
    private float lightFlickerSpeed;
    private float lightFlickerDistance;
	
	public ShipEngine(Ship owner, float locationOffset, float acceleration, float topSpeed){
		this.owner = owner;
		this.acceleration = acceleration;
		this.topSpeed = topSpeed;

		posOffset = new Vector2();

        lightFlickerSpeed = 16;
        lightFlickerDistance = 40f;
	}

    public void onCreateClient(){
        light = new LightFlicker(new Color(1f, 0.5f, 0.1f, 1f), topSpeed / 8);
    }
	
	public void render(SpriteBatch sb){
		
		posOffset.x = -MathUtils.sinDeg(owner.getRotation() + 180) * 20.5f;
		posOffset.y = MathUtils.cosDeg(owner.getRotation() + 180) * 20.5f;

        light.setPosition(owner.getCenterX() + posOffset.x,  owner.getCenterY() + posOffset.y);
        light.update();

		if(owner.getEntityActionState().getCurrentState() == EntityActionState.STATE_ON){

            light.activate(true);

/*			if(GameWorldClient.particleSystem.getEffectsFollow().containsKey(this)){
				GameWorldClient.particleSystem.getEffectsFollow().get(this).getEffect().start();
			}*/
		
			TextureRegion engineFlame = Revert.animations.get("fighter-engine").getKeyFrame(owner.getEntityActionState().getStateTime(), true);
			
			sb.draw(engineFlame,
					owner.getCenterX() + posOffset.x - engineFlame.getRegionWidth() / 2,
					owner.getCenterY() + posOffset.y - engineFlame.getRegionHeight() / 2,
					engineFlame.getRegionWidth() / 2,
					engineFlame.getRegionHeight() / 2,
					engineFlame.getRegionWidth(),
					engineFlame.getRegionHeight(),
					1, 1,
					owner.getRotation());

		}else{

            light.activate(false);

/*			if(GameWorldClient.particleSystem.getEffectsFollow().containsKey(this)){
				GameWorldClient.particleSystem.getEffectsFollow().get(this).getEffect().allowCompletion();
			}*/
		}

	}

    public void dispose(){
        light.remove();
    }
	
	public Vector2 getForwardsThrust(){
		
		return calculateVelocity(owner.getRotation());
		
	}
	
	private Vector2 calculateVelocity(float angle){
	
		Vector2 velocity = new Vector2();
		
		velocity.x = acceleration * (float) MathUtils.sinDeg(angle);
		velocity.y = acceleration * (float) MathUtils.cosDeg(angle);
		
		directionalSpeed = (float) Math.sqrt(Math.abs(velocity.x * velocity.x + velocity.y * velocity.y));
		
		if (directionalSpeed >= topSpeed) {
			velocity.x *= topSpeed / directionalSpeed;
			velocity.y *= topSpeed / directionalSpeed;
		}
		
		return velocity;
		
	}
	
	public Ship getOwner(){ return owner; }
	

}
