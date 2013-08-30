package com.dmiranda.revert.ui;

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
public abstract class BaseLight {

    protected Light light;
    protected Entity owner;

    public BaseLight(Color color, float distance, float x, float y) {
        light = new PointLight(GameWorldClient.rayHandler, 8, color, distance, 0, 0);
    }

    public void update(){

        if(owner != null){
            light.setPosition(owner.getCenterX(), owner.getCenterY());
        }

    }

    public void setOwner(Entity owner){
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
