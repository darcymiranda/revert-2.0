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

    private Color originalColor;
    private float originalDistance;

    public LightBase(Color color, int rays, float distance) {
        this.originalColor = color;
        this.originalDistance = distance;

        light = new PointLight(GameWorldClient.rayHandler, rays, color, distance, 0, 0);
        light.setXray(true);
        light.setSoft(true);
        light.setSoftnessLenght(10);
    }

    public void update(){

        if(owner != null){
            light.setPosition(owner.getCenterX(), owner.getCenterY());
        }

    }

    public void setIntensity(float intensity){
        //Color temp = new Color(originalColor);
        //light.setColor(temp.mul(intensity, intensity, intensity, 1));
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

    public void remove(){
        light.remove();
    }

}
