package com.dmiranda.revert.effects;

import box2dLight.Light;
import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.dmiranda.revert.GameWorldClient;
import com.dmiranda.revert.shared.Entity;

public class LightBase extends Entity {

    protected Light light;

    private float originalDistance;

    public LightBase(Color color, int rays, float distance, float x, float y) {
        super(x, y, (int)(distance * 0.5f), (int)(distance * 0.5f));

        this.originalDistance = distance;

        light = new PointLight(GameWorldClient.rayHandler, rays, color, distance, 0, 0);
        light.setPosition(x, y);
        light.setXray(true);
        light.setSoft(true);
        light.setSoftnessLenght(5);
    }

    @Override
    public void update(float delta){
        super.update(delta);
        if(ownerEntity != null && !ownerEntity.isAlive()) kill(null);
    }

    @Override
    protected void onDeath(Entity killer) {
        super.onDeath(killer);
        light.remove();
    }

    public void setIntensity(float intensity){
        light.setDistance(originalDistance * intensity);
    }

    public void setColor(Color color){
        light.setColor(color);
    }

    public Color getColor(){
        return light.getColor();
    }

    public void setPosition(float x, float y){
        light.setPosition(x, y);
    }

    public void setActive(boolean activate){
        light.setActive(activate);
    }

    public boolean isActive(){
        return light.isActive();
    }

}
