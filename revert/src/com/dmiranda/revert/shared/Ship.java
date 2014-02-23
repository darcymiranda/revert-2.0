package com.dmiranda.revert.shared;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.effects.Effect;
import com.dmiranda.revert.network.Network;

public class Ship extends Unit {
	
	private boolean w,a,s,d;
    private boolean previousMovement;
	
	private float acceleration, topSpeed;
	
	private Vector2 engineOffset;
	private ShipEngine shipEngine;
	
	public Ship(Player owner, float x, float y, int width, int height) {
		super(x, y, width, height);
		
		setOwnerPlayer(owner);
		
		engineOffset = new Vector2();
		shipEngine = new ShipEngine(this, 18.5f, 5.5f, 350f);
		
	}
	
	@Override
	public void update(float delta){
		super.update(delta);

        boolean move = (w || s || a || d);

        // Turn off and on engine effects
        if(Network.clientSide){
            if(previousMovement != move){
                if(move)
                    shipEngine.startEffect();
            } else {
                if(!move)
                    shipEngine.stopEffect();
            }
        }

        previousMovement = move;

		if(move){

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
            if(Network.clientSide) shipEngine.stopEffect();
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
                        (int)(getHeight() * (MathUtils.random() * 0.8f)), true);
                effect.expire(3.5f, Effect.EXPIRE_DELETE);
                effect.setTexture(Revert.getLoadedTexture("fighter-wreck.png"));
                effect.setRotationSpeed(MathUtils.random() * 15);
                effect.rotateTo(MathUtils.random(360) + 30);
                effect.setVelocity(getVelocity().x * (MathUtils.random()) + (MathUtils.random() * -0.5f * 35f) + 10,
                        getVelocity().y * (MathUtils.random()) + (MathUtils.random() * -0.5f * 35f) + 10);

                float size = (effect.getWidth() + effect.getHeight()) * 0.5f;
                effect.addLight(8, new Color(0.8f, 0.2f, 0.2f, 0.7f), size * 3).flicker(0.5f, 0.5f);

                ParticleEffect particleEffect = Revert.getLoadedParticleEffect("smoke-trail");
                for(ParticleEmitter emitter : particleEffect.getEmitters()){
                    emitter.getScale().setHigh(size);
                }
                effect.addParticleEffect(particleEffect, true);
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

    public class ShipEngine {

        private float acceleration;
        private float topSpeed;
        private float directionalSpeed;

        private Ship owner;
        private float locationOffset;

        private Effect[] engineEffect = new Effect[3];

        public ShipEngine(Ship owner, float locationOffset, float acceleration, float topSpeed){
            this.owner = owner;
            this.acceleration = acceleration;
            this.topSpeed = topSpeed;
            this.locationOffset = locationOffset;

            if(Network.clientSide){
                engineEffect[0] = new Effect(getCenterX(), getCenterY(), 8, 8, false);
                engineEffect[0].addParticleEffect(Revert.getLoadedParticleEffect("ship-engine"), false);
                engineEffect[1] = new Effect(getCenterX(), getCenterY(), 8, 8, false);
                engineEffect[1].addParticleEffect(Revert.getLoadedParticleEffect("ship-engine"), false);
                engineEffect[2] = new Effect(getCenterX(), getCenterY(), 8, 8, false);
                engineEffect[2].addParticleEffect(Revert.getLoadedParticleEffect("ship-engine"), false);
            }

        }

        public void startEffect(){
            for(int i = 0; i < engineEffect.length; i++)
                engineEffect[i].getParticleEffect().start();
        }

        public void stopEffect(){
            for(int i = 0; i < engineEffect.length; i++)
                engineEffect[i].getParticleEffect().allowCompletion();
        }

        public void update(float delta){
            if(Network.clientSide){
                for(int i = 0; i < engineEffect.length; i++)
                    engineEffect[i].update(delta);

                Vector2 posOffset = new Vector2(
                        -MathUtils.sinDeg(owner.getRotation() + 180) * locationOffset,
                        MathUtils.cosDeg(owner.getRotation() + 180) * locationOffset
                );

                Vector2 posOffset1 = new Vector2(
                        -MathUtils.sinDeg(owner.getRotation() + 180 + 16) * locationOffset,
                        MathUtils.cosDeg(owner.getRotation() + 180 + 16) * locationOffset
                );

                Vector2 posOffset2 = new Vector2(
                        -MathUtils.sinDeg(owner.getRotation() + 180 - 16) * locationOffset,
                        MathUtils.cosDeg(owner.getRotation() + 180 - 16) * locationOffset
                );

                engineEffect[0].setPosition(owner.getCenterX() + posOffset.x, owner.getCenterY() + posOffset.y);
                engineEffect[1].setPosition(owner.getCenterX() + posOffset1.x, owner.getCenterY() + posOffset1.y);
                engineEffect[2].setPosition(owner.getCenterX() + posOffset2.x, owner.getCenterY() + posOffset2.y);

            }

        }

        public void render(SpriteBatch sb){

            for(int i = 0; i < engineEffect.length; i++){
                engineEffect[i].render(sb);
            }

        }

        public void dispose(){
            if(Network.clientSide)
                for(int i = 0; i < engineEffect.length; i++)
                    engineEffect[i].die(null);

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

}
