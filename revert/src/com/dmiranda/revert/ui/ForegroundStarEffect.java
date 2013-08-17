package com.dmiranda.revert.ui;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.dmiranda.revert.Camera;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.shared.Entity;

public class ForegroundStarEffect {
	
	private Camera camera;
	
	private ArrayList<Entity>[] stars;
	
	public ForegroundStarEffect(Camera camera){
		this.camera = camera;
		for(int i = 0; i < 4; i++){
			stars[i] = new ArrayList<Entity>();
		}
		
		init();
	}
	
	private void init(){
		
		int density = 50;
		
		for(int i = 0; i < stars.length; i++){
			
			for(int j = 0; j < density; j++){
			
				float x = MathUtils.random(camera.getCamera().position.x, camera.getCamera().position.x + camera.getCamera().viewportWidth);
				float y = MathUtils.random(camera.getCamera().position.y, camera.getCamera().position.y + camera.getCamera().viewportHeight);
				int size = MathUtils.random(2, 6);
				
				Entity entity = new Entity(x, y, size, size);
				entity.setTexture(Revert.getLoadedTexture("health.png"));
				
				stars[i].add(entity);
			
			}
		}
	}
	
	public void update(float delta){

		Vector3 camPos = camera.getCamera().position;
		float width = camera.getCamera().viewportWidth;
		float height = camera.getCamera().viewportHeight;
		
		for(int i = 0; i < stars.length; i++){
			for(int j = 0; j < stars[i].size(); j++){
				Entity star = stars[i].get(j);
				
				if(star.getPosition().x < camPos.x){
					star.getPosition().x = camPos.x + width;
				}
				
				if(star.getPosition().y < camPos.y){
					star.getPosition().y = camPos.y + height;
				}
			}
		}
		
	}
	
	public void render(SpriteBatch sb){
		for(int i = 0; i < stars.length; i++){
			for(int j = 0; j < stars[i].size(); j++){
				stars[i].get(j).render(sb);
			}
		}
	}

}
