package com.dmiranda.revert.effects;

import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.dmiranda.revert.GameWorldClient;
import com.dmiranda.revert.tools.Tools;

public class BaseLight extends PointLight {

    private float flickerTick, flickerInterval, flickerMultiplier, flickerDistance;
    private boolean flickerFlip;

    private float size, sizeTick, sizeDuration;

    private Effect parent;

    public BaseLight(Effect parent, int rays, Color color, float distance, float x, float y) {
        super(GameWorldClient.rayHandler, rays, color, distance, x, y);
        this.parent = parent;
    }

    public void update(float delta){
        flickerUpdate(delta);
        sizeUpdate(delta);
    }

    private void flickerUpdate(float delta){
        if(flickerInterval <= 0) return;

        if(flickerTick < 0){
            flickerTick = flickerInterval;
        } else {
            flickerTick -= delta;
            flickerFlip = !flickerFlip;
        }

        setDistance(flickerFlip ? flickerDistance * flickerMultiplier : flickerDistance);
    }

    private void sizeUpdate(float delta){
        if(sizeDuration == 0) return;

        if(sizeTick < sizeDuration){
            setDistance(Tools.lerp(getDistance(), size, sizeTick/sizeDuration));
            sizeTick += delta;
        } else {
            sizeTick = 0;
            sizeDuration = 0;
        }
    }

    public BaseLight resize(float size, float overDuration){
        this.size = size;
        this.sizeDuration = overDuration;
        return this;
    }

    public BaseLight flicker(float interval, float multiplier){
        this.flickerInterval = interval;
        this.flickerMultiplier = multiplier;
        this.flickerDistance = distance;
        return this;
    }
}
