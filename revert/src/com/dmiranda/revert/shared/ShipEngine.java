package com.dmiranda.revert.shared;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.GameWorldClient;
import com.dmiranda.revert.effects.Effect;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.network.Network;

public class ShipEngine {
	
	private float acceleration;
	private float topSpeed;
    private float directionalSpeed;
	
	private Ship owner;
	private Vector2 posOffset;
    private float locationOffset;

    private Effect engineEffect;

	public ShipEngine(Ship owner, float locationOffset, float acceleration, float topSpeed){
		this.owner = owner;
		this.acceleration = acceleration;
		this.topSpeed = topSpeed;
        this.locationOffset = locationOffset;

		posOffset = new Vector2();

        posOffset.x = -MathUtils.sinDeg(owner.getRotation() + 180) * locationOffset;
        posOffset.y = MathUtils.cosDeg(owner.getRotation() + 180) * locationOffset;

        if(Network.clientSide){
            engineEffect = new Effect(0, 0, 0, 0);
            engineEffect.addLight(8, new Color(1f, 1f, 0.4f, 1f), 42).setActive(false);
        }

	}

    public void onCreateClient(){

    }

    public void update(float delta){
        if(Network.clientSide)
            engineEffect.update(delta);
    }

	public void render(SpriteBatch sb){

		if(owner.getEntityActionState().getCurrentState() == EntityActionState.STATE_ON){

            posOffset.x = -MathUtils.sinDeg(owner.getRotation() + 180) * locationOffset;
            posOffset.y = MathUtils.cosDeg(owner.getRotation() + 180) * locationOffset;

            engineEffect.setPosition(owner.getCenterX() + posOffset.x, owner.getCenterY() + posOffset.y);
            engineEffect.getLight().setActive(true);

			if(GameWorldClient.particleSystem.getEffectsFollow().containsKey(this))
                GameWorldClient.particleSystem.getEffectsFollow().get(this).getEffect().start();

			TextureRegion engineFlame = Revert.animations.get("fighter-engine").getKeyFrame(owner.getEntityActionState().getStateTime(), true);

			sb.draw(engineFlame,
					owner.getCenterX() + posOffset.x - engineFlame.getRegionWidth() * 0.5f,
					owner.getCenterY() + posOffset.y - engineFlame.getRegionHeight() * 0.5f,
					engineFlame.getRegionWidth() * 0.5f,
					engineFlame.getRegionHeight() * 0.5f,
					engineFlame.getRegionWidth(),
					engineFlame.getRegionHeight(),
					1, 1,
					owner.getRotation());

		}else{

            engineEffect.getLight().setActive(false);

			if(GameWorldClient.particleSystem.getEffectsFollow().containsKey(this))
                GameWorldClient.particleSystem.getEffectsFollow().get(this).getEffect().allowCompletion();
		}

	}

    public void dispose(){

        // TODO: newUnsafeByteBuffer exception

        if(Network.clientSide)
            engineEffect.die(null);

    }
	
	public Vector2 getForwardsThrust(){
		return calculateVelocity(owner.getRotation());
	}
	
	private Vector2 calculateVelocity(float angle){
	
		Vector2 velocity = new Vector2();
		
		velocity.x = acceleration * MathUtils.sinDeg(angle);
		velocity.y = acceleration * MathUtils.cosDeg(angle);
		
		directionalSpeed = (float) Math.sqrt(Math.abs(velocity.x * velocity.x + velocity.y * velocity.y));
		
		if (directionalSpeed >= topSpeed) {
			velocity.x *= topSpeed / directionalSpeed;
			velocity.y *= topSpeed / directionalSpeed;
		}
		
		return velocity;
		
	}
	
	public Ship getOwner(){ return owner; }
}
