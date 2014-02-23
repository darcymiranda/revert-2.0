package com.dmiranda.revert.shared.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.dmiranda.revert.GameWorldClient;
import com.dmiranda.revert.effects.Effect;
import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.GameWorld;
import com.dmiranda.revert.shared.weapon.Weapon;


public abstract class Bullet extends Entity {
	
	protected int decay;
	protected float speed;
	protected Weapon weapon;

	public Bullet(Weapon weapon, float x, float y, int width, int height, float speed, float direction) {
		super(x, y, width, height);
		
		createCollisionCircle(x, y, 4);
		
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
			die(null);
		}else{
			decay -= delta * 1000;
		}
		
	}
	
	@Override
	public void onHit(Entity hitter){
		super.onHit(hitter);

        if(Network.clientSide){

            ParticleEffect effect = GameWorldClient.particleSystem.getCachedEffect("hit");
            ParticleEmitter emitter = effect.getEmitters().first();
            emitter.getLife().setHigh(150);
            emitter.setPosition(getCenterX() + (getVelocity().x * Gdx.graphics.getDeltaTime()),
                    getCenterY() + (getVelocity().y * Gdx.graphics.getDeltaTime()));
            emitter.getAngle().setHigh(getRotation() - 70, getRotation() - 110);
            emitter.getAngle().setLow(getRotation() - 70, getRotation() - 110);

            GameWorldClient.particleSystem.addNewEffect(effect, "hit", getId());

            Effect light = new Effect(getCenterX(), getCenterY(), 0, 0);
            light.expire(0.1f, Effect.EXPIRE_DELETE);
            light.addLight(6, new Color(1f, 1f, 0.4f, 0.8f), 64);

            GameWorld.entityManager.addLocalEntity(light);
        }

		die(hitter);
		
	}

    @Override
    protected void onDeath() {
        super.onDeath();
    }

    @Override
	public void render(SpriteBatch sb){
		super.render(sb);
		
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
