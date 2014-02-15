package com.dmiranda.revert;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.shared.Entity;

public class BackgroundEffect {
	
	private Camera camera;
	private ArrayList<ArrayList<Star>> stars;

	public BackgroundEffect(Camera camera){
		this.camera = camera;

		stars = new ArrayList<ArrayList<Star>>();

        final int LAYERS = 4;
		for(int i = 0; i < LAYERS; i++){
			stars.add(new ArrayList<Star>());
		}
		
	}
	
	public void init(Texture texture){

		final int STAR_DENSITY = 44;

        TextureRegion[] starTextures;

        if(texture == null)
		    starTextures = Revert.getLoadedTexture("stars.png").split(5, 5)[0];
		else
            starTextures = TextureRegion.split(texture, 5, 5)[0];

		for(int i = 0; i < stars.size(); i++){

            ArrayList<Star> layer = stars.get(i);
			
			for(int j = 0; j < STAR_DENSITY; j++){
			
				float x = MathUtils.random(camera.getModifiedPosition().x, camera.getModifiedPosition().x + camera.viewportWidth);
				float y = MathUtils.random(camera.getModifiedPosition().x, camera.getModifiedPosition().x + camera.viewportHeight);
				int type = MathUtils.random(0, 2);
				
				Star star = new Star(x, y, 5, 5);
				star.setTexture(starTextures[type]);

				layer.add(star);
			}
		}
	}
	
	public void toggleHide(){
		
	}
	
	public void render(float delta, SpriteBatch sb){

        Entity focus = camera.getFocusEntity();
        if(focus == null){
            focus = new Entity(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f, 32, 32);
            focus.setVelocity(1, 0);
        }

		Vector2 camPos = camera.getModifiedPosition();
		float width = camera.viewportWidth;
		float height = camera.viewportHeight;

		for(int i = 0; i < stars.size(); i++){

			float vx = -(focus.getVelocity().x / (i + 1) * 0.3f);
			float vy = -(focus.getVelocity().y / (i + 1) * 0.3f);

            ArrayList<Star> layer = stars.get(i);

			for(int j = 0; j < layer.size(); j++){
				Star star = layer.get(j);
				
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
	
	private class Star extends Entity {
		
		private Color color = new Color (1,1,1,1);
		
		public Star(float x, float y, int w, int h){
			super(x, y, w, h);
		}
		
/*		public void setColor(Color color){ this.color = color; }
		public Color getColor(){ return color; }*/
		
	}

}
