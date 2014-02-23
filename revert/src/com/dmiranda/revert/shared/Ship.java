package com.dmiranda.revert.shared;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.GameWorldClient;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.effects.Effect;
import com.dmiranda.revert.network.Network;

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
    protected void onCreateClient(){
        super.onCreateClient();

        shipEngine.onCreateClient();
    }
	
	@Override
	public void update(float delta){
		super.update(delta);
		
		if(w || s || a || d){
			
			actionState.changeState(EntityActionState.STATE_ON);
			
			if(w){
				
				if(velocity.y > -topSpeed){
					velocity.y -= acceleration;
				}
				
			}
			else if(s){
				
				if(velocity.y < topSpeed){
					velocity.y += acceleration;
				}
			}
			
			if(a){
				
				if(velocity.x > -topSpeed){
					velocity.x -= acceleration;
				}
			}
			else if(d){
				
				if(velocity.x < topSpeed){
					velocity.x += acceleration;
				}
			}
			
			/* Old style movement
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
			*/
		}
		else{
			actionState.changeState(EntityActionState.STATE_OFF);
		}

        shipEngine.update(delta);
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

    @Override
    public void onDeath(){
        super.onDeath();

        shipEngine.dispose();

        if(Network.clientSide){
            for(int i = 0 ; i < MathUtils.random(4)+3; i++){

                float cx = getCenterX(), cy = getCenterY();

                Effect effect = new Effect(cx, cy, (int)(getWidth() * (MathUtils.random() * 0.8f)),
                        (int)(getHeight() * (MathUtils.random() * 0.8f)));
                effect.expire(3.5f, Effect.EXPIRE_DELETE);
                effect.setTexture(Revert.getLoadedTexture("fighter-wreck.png"));
                effect.setRotationSpeed(MathUtils.random() * 15);
                effect.rotateTo(MathUtils.random(360) + 30);
                effect.setVelocity(getVelocity().x * (MathUtils.random()) + (MathUtils.random() * -0.5f * 35f) + 10,
                        getVelocity().y * (MathUtils.random()) + (MathUtils.random() * -0.5f * 35f) + 10);

                float size = (effect.getWidth() + effect.getHeight()) * 0.5f;
                effect.addLight(8, new Color(0.8f, 0.2f, 0.2f, 0.7f), size * 3).flicker(0.5f, 0.5f);

                ParticleEffect particleEffect = GameWorldClient.particleSystem.getCachedEffect("smoke-trail");
                for(ParticleEmitter emitter : particleEffect.getEmitters()){
                    emitter.getScale().setHigh(size);
                }

                GameWorldClient.particleSystem.addNewEffectFollower(particleEffect, effect, true);
                GameWorld.entityManager.addLocalEntity(effect);
            }
        }
    }

/*	private void setDirectionVelocity(float acceleration, float maxSpeed){
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
	}*/
	
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
