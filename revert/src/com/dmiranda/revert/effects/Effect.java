package com.dmiranda.revert.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.GameWorld;
import com.dmiranda.revert.tools.Tools;

public class Effect extends Entity {

    public static final int EXPIRE_DELETE = 1, EXPIRE_HIDE = 2;
    private int expireOption;
    private float expireTime;

    private BaseLight light;

    private ParticleEffect particleEffect;
    private boolean particleEffectFollow;

	public Effect(float x, float y, int width, int height, boolean global) {
		super(x, y, width, height);
        if(global) GameWorld.entityManager.addLocalEntity(this);
	}

	@Override
	public void update(float delta){
		super.update(delta);

        if(light != null){
            light.setPosition(getCenterX(), getCenterY());
            light.update(delta);
        }

        if(particleEffect != null){
            if(particleEffectFollow) particleEffect.setPosition(getCenterX(), getCenterY());
            particleEffect.update(delta);
        }

        expireUpdate(delta);

	}

    @Override
    public void render(SpriteBatch sb) {
        super.render(sb);
        if(isHidden()) return;

        if(particleEffect != null){
            particleEffect.draw(sb);
        }
    }

    @Override
    public void setPosition(float x, float y){
        super.setPosition(x, y);
        if(light != null) light.setPosition(x, y);
        if(particleEffect != null) particleEffect.setPosition(x, y);
    }

    @Override
    protected void onDeath() {
        super.onDeath();
        if(light != null) light.remove();
        if(particleEffect != null) particleEffect.dispose();
    }

    private void expireUpdate(float delta){
        if(expireOption == 0) return;
        if(expireOption == EXPIRE_HIDE && isHidden()) return;

        if(expireTime < 0){
            switch(expireOption){
                case EXPIRE_DELETE:
                    die(null);
                    break;
                case EXPIRE_HIDE:
                    if(light != null) light.setActive(false);
                    hide();
                    break;
            }
        } else {
            expireTime -= delta;
        }
    }

    public Effect addParticleEffect(ParticleEffect effect, boolean follow){
        this.particleEffect = effect;
        this.particleEffect.start();
        this.particleEffect.setPosition(getCenterX(), getCenterY());
        this.particleEffectFollow = follow;
        return this;
    }

    public Effect expire(float time, int option){
        this.expireTime = time;
        this.expireOption = option;
        return this;
    }

    public BaseLight addLight(int rays, Color color, float distance){
        light = new BaseLight(this, rays, color, distance, getCenterX(), getCenterY());
        light.setXray(true);
        light.setSoft(true);
        light.setSoftnessLenght(3);
        return light;
    }

    public BaseLight getLight(){ return light; }

    public ParticleEffect getParticleEffect() {
        return particleEffect;
    }
}
