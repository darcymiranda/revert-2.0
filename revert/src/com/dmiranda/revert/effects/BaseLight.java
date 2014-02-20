package com.dmiranda.revert.effects;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.dmiranda.revert.GameWorldClient;

public class BaseLight extends PointLight {

    private float flickerTick, flickerInterval, flickerMultiplier, flickerDistance;
    private boolean flickerFlip;

    private Effect parent;

    public BaseLight(Effect parent, int rays, Color color, float distance, float x, float y) {
        super(GameWorldClient.rayHandler, rays, color, distance, x, y);
        this.parent = parent;
    }

    public void update(float delta){

        delta *= 1000;

        flickerUpdate(delta);

    }

    private void flickerUpdate(float delta){
        if(flickerInterval < 0) return;

        if(flickerTick < 0){
            flickerTick = flickerInterval;
        } else {
            flickerTick -= delta;
            flickerFlip = !flickerFlip;
        }

        distance = flickerFlip ? flickerDistance * flickerMultiplier : flickerDistance;
    }

    public BaseLight flicker(float interval, float multiplier){
        this.flickerInterval = interval;
        this.flickerMultiplier = multiplier;
        this.flickerDistance = distance;
        return this;
    }
}
