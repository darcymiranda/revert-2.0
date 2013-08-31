package com.dmiranda.revert.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

/**
 * User: dmiranda
 * Date: 8/30/13
 * Time: 3:09 PM
 */
public class LightFlicker extends LightBase {

    private boolean flicker;
    private float flickerDistance;
    private float flickerMultiplier;
    private float flickerTick;
    private float flickerSpeed;

    public LightFlicker(Color color, float flickerDistance, float flickerMultiplier, float flickerSpeed) {
        super(color, flickerDistance, 0, 0);
        this.flickerDistance = flickerDistance;
        this.flickerSpeed = flickerSpeed;
        this.flickerMultiplier = flickerMultiplier;
    }

    public LightFlicker(Color color, float distance, float flickerSpeed){
        this(color, distance, 0.75f, flickerSpeed);
    }

    public LightFlicker(Color color, float distance){
        this(color, distance, 16f);
    }

    @Override
    public void update(){
        super.update();

        if(flickerTick < 0){
            flicker = !flicker;
            flickerTick = flickerSpeed;
        } else {
            flickerTick -= Gdx.graphics.getDeltaTime() * 1000;
        }

        if(flicker){
            light.setDistance(flickerDistance * flickerMultiplier);
        } else {
            light.setDistance(flickerDistance);
        }

    }

    public void setFlickerSpeed(float flickerSpeed){
        this.flickerSpeed = flickerSpeed;
    }

    public void setDistance(float flickerDistance){
        this.flickerDistance = flickerDistance;
    }

}
