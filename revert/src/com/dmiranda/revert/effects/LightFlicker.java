package com.dmiranda.revert.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.dmiranda.revert.shared.Entity;

public class LightFlicker extends LightBase {

    private boolean flicker;
    private float flickerDistance;
    private float flickerMultiplier;
    private float flickerTick;
    private float flickerSpeed;

    public LightFlicker(Color color, int rays, float flickerDistance, float flickerMultiplier, float flickerSpeed, float x, float y) {
        super(color, rays, flickerDistance, x, y);
        this.flickerDistance = flickerDistance;
        this.flickerSpeed = flickerSpeed;
        this.flickerMultiplier = flickerMultiplier;
    }

    public LightFlicker(Color color, int rays, float distance, float flickerSpeed){
        this(color, rays, distance, 0.75f, flickerSpeed, 0, 0);
    }

    public LightFlicker(int rays, Color color, float distance){
        this(color, rays, distance, 16f);
    }

    @Override
    public void update(float delta){
        super.update(delta);

        if(flickerTick < 0){
            flicker = !flicker;
            flickerTick = flickerSpeed;
        } else {
            flickerTick -= delta * 1000;
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
