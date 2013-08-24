package com.dmiranda.revert;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.shared.Entity;

public class ForegroundStarEffect {
	
	private Camera camera;
	
	private ArrayList<Entity>[] stars;
	
	@SuppressWarnings("unchecked")
	public ForegroundStarEffect(Camera camera){
		this.camera = camera;
		
		stars = new ArrayList[4];
		
		for(int i = 0; i < stars.length; i++){
			stars[i] = new ArrayList<Entity>();
		}
		
	}
	
	public void init(){
		
		int density = 50;
		
		TextureRegion[] starTextures = Revert.getLoadedTexture("stars.png").split(5, 5)[0];
		
		for(int i = 0; i < stars.length; i++){
			
			for(int j = 0; j < density; j++){
			
				float x = MathUtils.random(camera.getModifiedPosition().x, camera.getModifiedPosition().x + camera.getCamera().viewportWidth);
				float y = MathUtils.random(camera.getModifiedPosition().x, camera.getModifiedPosition().x + camera.getCamera().viewportHeight);
				int type = MathUtils.random(0, 2);
				
				Entity entity = new Entity(x, y, 5, 5);
				entity.setTexture(starTextures[type]);
				
				stars[i].add(entity);
			
			}
		}
	}
	
	public void toggleHide(){
		
	}
	
	public void render(float delta, SpriteBatch sb){
		
		Vector2 camPos = camera.getModifiedPosition();
		float width = camera.getCamera().viewportWidth;
		float height = camera.getCamera().viewportHeight;
		
		for(int i = 0; i < stars.length; i++){
			
			Entity focus = camera.getFocusEntity();
			
			float vx = -(focus.getVelocity().x / i * 0.2f);
			float vy = -(focus.getVelocity().y / i * 0.2f);
			
			for(int j = 0; j < stars[i].size(); j++){
				Entity star = stars[i].get(j);
				
				if(star.getPosition().x < camPos.x){
					star.getPosition().x = camPos.x + width;
					star.getPosition().y = MathUtils.random(camPos.y, camPos.y + height);
				}
				else if(star.getPosition().x > camPos.x + width){
					star.getPosition().x = camPos.x;
					star.getPosition().y = MathUtils.random(camPos.y, camPos.y + height);
				}
				
				if(star.getPosition().y < camPos.y){
					star.getPosition().y = camPos.y + height;
					star.getPosition().x = MathUtils.random(camPos.x, camPos.x + width);
				}
				else if(star.getPosition().y > camPos.y + height){
					star.getPosition().y = camPos.y;
					star.getPosition().x = MathUtils.random(camPos.x, camPos.x + width);
				}
				
				star.setVelocity(vx,  vy);
				star.update(delta);
				star.render(sb);
			}
		}
	}

}
