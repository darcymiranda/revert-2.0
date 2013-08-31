package com.dmiranda.revert.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

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

    public LightExpire(Color color, float distance, float life) {
        super(color, distance, 0, 0);
        this.life = life;
    }

    public void setExpireOption(int expireOption){
        this.expireOption = expireOption;
    }

    private void expire(){

        if(expireOption == REMOVE){
            remove();
        }
        else if(expireOption == TURN_OFF){
            activate(false);
            lifeTick = life;
        }

    }

    @Override
    public void update(){
        super.update();

        if(light.isActive()){

            if(lifeTick < 0 ){
                expire();
            } else {
                lifeTick -= Gdx.graphics.getDeltaTime() * 1000;
            }
        }

    }

}
