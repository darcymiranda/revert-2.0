package com.dmiranda.revert;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.GameWorld;
import com.dmiranda.revert.tools.Tools;

public class Camera {
	
	private OrthographicCamera orthoCam;
	
	private Entity focusEntity;
	
	private float xcorner = Gdx.graphics.getWidth() / 2;
	private float ycorner = Gdx.graphics.getHeight() / 2;
	
	private final float MAX_DIST = 250;
	
	public Camera(){
		
		orthoCam = new OrthographicCamera();
		orthoCam.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		orthoCam.position.x = xcorner;
		orthoCam.position.y = ycorner;
		zoom(1f);
		
	}
	
	public void zoom(float zoom){
		
		orthoCam.zoom = zoom;
		
		xcorner = Gdx.graphics.getWidth() / 2 * orthoCam.zoom;
		ycorner = Gdx.graphics.getHeight() / 2 * orthoCam.zoom;
		
		orthoCam.viewportWidth = Gdx.graphics.getWidth();
		orthoCam.viewportHeight = Gdx.graphics.getHeight();
		
	}
	
	public void update(){
		
		orthoCam.update();
		
		if(focusEntity != null){
			
			Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
			getCamera().unproject(mouse);
			
			float dx = -MAX_DIST * (float) MathUtils.sinDeg(focusEntity.getRotation());
			float dy = MAX_DIST * (float) MathUtils.cosDeg(focusEntity.getRotation());
			
			Vector3 focusToProj = new Vector3(focusEntity.getCenterX() - focusEntity.getVelocity().x, focusEntity.getCenterY() - focusEntity.getVelocity().y, 0);
			getCamera().project(focusToProj);
			
			MathUtils.clamp(focusToProj.x, -orthoCam.viewportWidth, orthoCam.viewportWidth);
			MathUtils.clamp(focusToProj.y, -orthoCam.viewportHeight, orthoCam.viewportHeight);
			
			orthoCam.position.x = Tools.lerp(orthoCam.position.x, focusEntity.getCenterX() + dx, 0.05f);
			orthoCam.position.y = Tools.lerp(orthoCam.position.y, focusEntity.getCenterY() + dy, 0.05f);
			
			if(orthoCam.position.x < xcorner) orthoCam.position.x = xcorner;
			else if(orthoCam.position.x + xcorner > GameWorld.map.getWidth()){
				orthoCam.position.x = GameWorld.map.getWidth() - xcorner;
			}
			
			if(orthoCam.position.y < ycorner) orthoCam.position.y = ycorner;
			else if(orthoCam.position.y + ycorner > GameWorld.map.getHeight()){
				orthoCam.position.y = GameWorld.map.getHeight() - ycorner;
			}
			
		}
	}
	
	public void focusEntity(Entity entity){ focusEntity = entity; }
	
	public boolean hasFocus(){ return focusEntity != null; }
	public OrthographicCamera getCamera(){ return orthoCam; }
	public Entity getFocusEntity(){ return focusEntity; }
	public Vector2 getFocusPosition(){ return focusEntity.getCenterPosition(); }
	public Vector2 getTranslation(){ return new Vector2(getModifiedViewport().add(getModifiedPosition())); }
	public Vector2 getUnmodifiedPosition(){ return new Vector2(orthoCam.position.x, orthoCam.position.y); }
	public Vector2 getModifiedPosition(){ return new Vector2(orthoCam.position.x - xcorner, orthoCam.position.y - ycorner); }
	public Vector2 getModifiedViewport(){ return new Vector2(orthoCam.viewportWidth, orthoCam.viewportHeight); }

}
