package com.dmiranda.revert.effects;

import com.badlogic.gdx.graphics.Color;
import com.dmiranda.revert.shared.Entity;

public class Effect extends Entity {

    public static final int EXPIRE_DELETE = 1, EXPIRE_HIDE = 2;
    private int expireOption;
    private float expireTime;

    private BaseLight light;

	public Effect(float x, float y, int width, int height) {
		super(x, y, width, height);
	}

	@Override
	public void update(float delta){
		super.update(delta);

        if(light != null){
            light.setPosition(getCenterX(), getCenterY());
        }

        expireUpdate(delta);

	}

    @Override
    public void setPosition(float x, float y){
        super.setPosition(x, y);
        if(light != null) light.setPosition(x, y);
    }

    @Override
    public void onDeath(Entity killer){
        super.onDeath(killer);
        light.remove();
    }

    private void expireUpdate(float delta){
        if(expireOption == 0) return;
        if(expireOption == EXPIRE_HIDE && isHidden()) return;

        if(expireTime < 0){
            switch(expireOption){
                case EXPIRE_DELETE:
                    kill(null);
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

    public Effect expire(float time, int option){
        this.expireTime = time;
        this.expireOption = option;
        return this;
    }

    public BaseLight addLight(int rays, Color color, float distance){
        light = new BaseLight(this, rays, color, distance, getCenterX(), getCenterY());
        light.setXray(true);
        light.setSoft(true);
        light.setSoftnessLenght(5);
        return light;
    }

    public BaseLight getLight(){ return light; }
}
