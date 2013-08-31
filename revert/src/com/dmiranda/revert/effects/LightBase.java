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
public class LightBase {

    protected Light light;
    protected Entity owner;

    public LightBase(Color color, float distance, float x, float y) {
        light = new PointLight(GameWorldClient.rayHandler, 8, color, distance, 0, 0);
        light.setXray(true);
    }

    public void update(){

        if(owner != null){
            light.setPosition(owner.getCenterX(), owner.getCenterY());
        }

    }

    public void setColor(float r, float g, float b, float a){
        light.setColor(r, g, b, a);
    }

    public void setColor(Color color){
        light.setColor(color);
    }

    public void attachToEntity(Entity owner){
        this.owner = owner;
    }

    public void setPosition(float x, float y){
        light.setPosition(x, y);
    }

    public void activate(boolean activate){
        light.setActive(activate);
    }

    public void remove(){
        light.remove();
    }

}
