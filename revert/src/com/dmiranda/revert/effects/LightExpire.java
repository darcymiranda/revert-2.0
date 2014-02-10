package com.dmiranda.revert.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.dmiranda.revert.shared.Entity;

/**
 * User: dmiranda
 * Date: 8/30/13
 * Time: 3:48 PM
 */
public class LightExpire extends LightBase {

    public static final int REMOVE = 0, TURN_OFF = 1;

    private int expireOption;
    private float lifeTick;
    private float life;

    public LightExpire(Color color, int rays, float distance, float life, float x, float y) {
        super(color, rays, distance, x, y);
        this.life = life;
    }

    public void setExpireOption(int expireOption){
        this.expireOption = expireOption;
    }

    private void expire(){

        if(expireOption == REMOVE){
            kill(null);
        }
        else if(expireOption == TURN_OFF){
            setActive(false);
            lifeTick = life;
        }

    }

    @Override
    public void update(float delta){
        super.update(delta);

        if(isActive()){

            if(lifeTick < 0 ){
                expire();
            } else {
                lifeTick -= delta * 1000;
            }
        }

    }

}
