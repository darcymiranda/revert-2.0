package com.dmiranda.revert;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.GameWorld;
import com.dmiranda.revert.tools.Tools;

import java.util.ArrayList;

public class Camera extends OrthographicCamera {
	
	private Entity focusEntity;

	private float xcorner = Gdx.graphics.getWidth() * 0.5f;
	private float ycorner = Gdx.graphics.getHeight() * 0.5f;
    private ArrayList<Vector2> shakeOffset = new ArrayList<Vector2>();
	
	private final float MAX_DIST = 250;

    Matrix4 parallaxView = new Matrix4();
    Matrix4 parallaxCombined = new Matrix4();
    Vector3 tmp = new Vector3();
    Vector3 tmp2 = new Vector3();
	
	public Camera(){

		setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		position.x = xcorner;
		position.y = ycorner;
		zoom(1f);
		
	}
	
	public void zoom(float zoom){

		xcorner = Gdx.graphics.getWidth() * 0.5f * zoom;
		ycorner = Gdx.graphics.getHeight() * 0.5f * zoom;
		
		viewportWidth = Gdx.graphics.getWidth();
		viewportHeight = Gdx.graphics.getHeight();
		
	}

    public void shake(float intensity){

        int amount = MathUtils.round(intensity * 0.05f);

        // TODO: Might need to look at previous shaking values because big shakes can be overwritten by smaller ones
        shakeOffset.clear();

        for(int i = amount; i > 0; i--){
            shakeOffset.add(new Vector2(
                    ((MathUtils.random() * intensity) - intensity * 0.5f) * (((float)(i + 1) / amount)),
                    ((MathUtils.random() * intensity) - intensity * 0.5f) * (((float)(i + 1) / amount))
            ));
        }
    }

    public Matrix4 calculateParallaxMatrix (float parallaxX, float parallaxY) {
        update();
        tmp.set(position);
        tmp.x *= parallaxX;
        tmp.y *= parallaxY;

        parallaxView.setToLookAt(tmp, tmp2.set(tmp).add(direction), up);
        parallaxCombined.set(projection);
        Matrix4.mul(parallaxCombined.val, parallaxView.val);
        return parallaxCombined;
    }

    @Override
	public void update(){
        super.update();
		
		if(focusEntity != null){

            // reduce shaking intensity
            Vector2 currentShakeOffset = new Vector2();
            if(shakeOffset.size() > 0){

                Vector2 v = shakeOffset.get(0);
                v.lerp(new Vector2(), 0.85f);

                currentShakeOffset.x = v.x;
                currentShakeOffset.y = v.y;

                if(v.len() >= 0){
                    shakeOffset.remove(0);
                }
            }
			
			Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			unproject(mouse);
			
			float dx = -MAX_DIST * MathUtils.sinDeg(focusEntity.getRotation());
			float dy = MAX_DIST * MathUtils.cosDeg(focusEntity.getRotation());
			
			Vector3 focusToProj = new Vector3(focusEntity.getCenterX() - focusEntity.getVelocity().x, focusEntity.getCenterY() - focusEntity.getVelocity().y, 0);
            project(focusToProj);
			
			MathUtils.clamp(focusToProj.x, -viewportWidth, viewportWidth);
			MathUtils.clamp(focusToProj.y, -viewportHeight, viewportHeight);
			
			position.x = Tools.lerp(position.x, focusEntity.getCenterX() + dx, 0.05f) + currentShakeOffset.x;
			position.y = Tools.lerp(position.y, focusEntity.getCenterY() + dy, 0.05f) + currentShakeOffset.y;
			
			if(position.x < xcorner) position.x = xcorner;
			else if(position.x + xcorner > GameWorld.map.getWidth()){
				position.x = GameWorld.map.getWidth() - xcorner;
			}
			
			if(position.y < ycorner) position.y = ycorner;
			else if(position.y + ycorner > GameWorld.map.getHeight()){
				position.y = GameWorld.map.getHeight() - ycorner;
			}
			
		}
	}
	
	public void focusEntity(Entity entity){ focusEntity = entity; }

    public Vector2 getCenterPosition(){ return new Vector2(position.x - viewportWidth * 0.5f, position.y - viewportHeight); }
	public boolean hasFocus(){ return focusEntity != null; }
	public Entity getFocusEntity(){ return focusEntity; }
	public Vector2 getFocusPosition(){ return focusEntity.getCenterPosition(); }
	public Vector2 getTranslation(){ return new Vector2(getModifiedViewport().add(getModifiedPosition())); }
	public Vector2 getUnmodifiedPosition(){ return new Vector2(position.x, position.y); }
	public Vector2 getModifiedPosition(){ return new Vector2(position.x - xcorner, position.y - ycorner); }
	public Vector2 getModifiedViewport(){ return new Vector2(viewportWidth, viewportHeight); }

}
