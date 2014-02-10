package com.dmiranda.revert.effects;

import box2dLight.Light;
import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;
import com.dmiranda.revert.GameWorldClient;
import com.dmiranda.revert.shared.Entity;

/**
 * User: dmiranda
 * Date: 8/30/13
 * Time: 3:49 PM
 */
public class LightBase extends Entity {

    protected Light light;
    protected Entity owner;

    private float originalDistance;

    public LightBase(Color color, int rays, float distance, float x, float y) {
        super(x, y, (int)(distance * 0.5f), (int)(distance * 0.5f));

        this.originalDistance = distance;

        light = new PointLight(GameWorldClient.rayHandler, rays, color, distance, 0, 0);
        light.setXray(true);
        light.setSoft(true);
        light.setSoftnessLenght(10);
    }

    @Override
    public void update(float delta){
        super.update(delta);

        if(owner != null){
            light.setPosition(owner.getCenterX(), owner.getCenterY());

            if(!owner.isAlive()){
                light.remove();
                owner = null;
            }
        }

    }

    @Override
    protected void onDeath(Entity killer) {
        super.onDeath(killer);
        light.remove();
    }

    public void setIntensity(float intensity){
        light.setDistance(originalDistance * intensity);
    }

    public void setColor(float r, float g, float b, float a){
        light.setColor(r, g, b, a);
    }

    public void setColor(Color color){
        light.setColor(color);
    }

    public Color getColor(){
        return light.getColor();
    }

    public void attach(Entity owner){
        this.owner = owner;
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
