package com.dmiranda.revert.shared;

import box2dLight.ConeLight;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.GameWorldClient;
import com.dmiranda.revert.effects.LightBase;
import com.dmiranda.revert.effects.LightFlicker;
import com.dmiranda.revert.Revert;

public class ShipEngine {
	
	private float acceleration;
	private float topSpeed;
    private float directionalSpeed;
    private float boosterAcceleration;
    private float boosterTopSpeed;
    private boolean booster;
	
	private Ship owner;
	private Vector2 posOffset;
    private float locationOffset;

    private LightFlicker light;
    private ConeLight lightBooster;
    private float lightFlickerSpeed;
    private float lightFlickerDistance;

	public ShipEngine(Ship owner, float locationOffset, float acceleration, float topSpeed){
		this.owner = owner;
		this.acceleration = acceleration;
		this.topSpeed = topSpeed;
        this.locationOffset = locationOffset;

        boosterAcceleration = acceleration * 1.5f;
        boosterTopSpeed = topSpeed * 2f;

		posOffset = new Vector2();

        lightFlickerSpeed = 16;
        lightFlickerDistance = 65f;

        posOffset.x = -MathUtils.sinDeg(owner.getRotation() + 180) * locationOffset;
        posOffset.y = MathUtils.cosDeg(owner.getRotation() + 180) * locationOffset;

        GameWorldClient.particleSystem.addNewEffectFollower("ship_engine2", owner, posOffset, true);
	}

    public void onCreateClient(){
        lightBooster = new ConeLight(GameWorldClient.rayHandler, 32, new Color(1f, 0.8f, 0.8f, 1), lightFlickerDistance * 3.7f, owner.getCenterX(), owner.getCenterY(), owner.getRotation(), 25);
        lightBooster.setSoft(false);
        lightBooster.setActive(false);
        light = new LightFlicker(new Color(1f, 0.5f, 0.1f, 0.8f), 16, lightFlickerDistance, 0.5f, lightFlickerSpeed);
        light.setActive(false);
    }

    public void booster(boolean booster){
        this.booster = booster;

        lightBooster.setActive(booster);

        if(booster){
            lightBooster.setDistance(55);
            lightBooster.setColor(0, 0.2f, 1f, 1.0f);
        } else {
            lightBooster.setDistance(40);
            lightBooster.setColor(1f, 0.5f, 0.1f, 1f);
        }
    }

	public void render(SpriteBatch sb){

		posOffset.x = -MathUtils.sinDeg(owner.getRotation() + 180) * locationOffset;
		posOffset.y = MathUtils.cosDeg(owner.getRotation() + 180) * locationOffset;

        Vector2 lightOffset = new Vector2(
                -MathUtils.sinDeg(owner.getRotation() + 180 * locationOffset),
                 MathUtils.cosDeg(owner.getRotation() + 180 * locationOffset)
        );


       // staticLight.setPosition(owner.getCenterX() + posOffset.x, owner.getCenterY() + posOffset.y);
       // staticLight.setDistance(88 * MathUtils.random() + 0.8f);
       // staticLight.setDirection(owner.getRotation() - 90);

		if(owner.getEntityActionState().getCurrentState() == EntityActionState.STATE_ON){

            light.setPosition(owner.getCenterX() + posOffset.x, owner.getCenterY() + posOffset.y);
            light.setIntensity(Math.abs(owner.getVelocity().len()) * 0.01f);
            light.update();
            light.setActive(true);

			if(GameWorldClient.particleSystem.getEffectsFollow().containsKey(this)){
				GameWorldClient.particleSystem.getEffectsFollow().get(this).getEffect().start();
			}

            Color curColor = sb.getColor();
            if(booster){
                lightBooster.setDirection(owner.getRotation() - 90);
                lightBooster.setPosition(owner.getCenterX() + posOffset.x - 5, owner.getCenterY() + posOffset.y - 5);
            }

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

            light.setActive(false);
            lightBooster.setActive(false);

			if(GameWorldClient.particleSystem.getEffectsFollow().containsKey(this)){
				GameWorldClient.particleSystem.getEffectsFollow().get(this).getEffect().allowCompletion();
			}
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
