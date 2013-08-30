package com.dmiranda.revert.shared;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.Camera;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.shared.bullet.Bullet;

public class GameMap {

	private int width, height;
	
	public GameMap(int width, int height){
		
		this.width = width;
		this.height = height;
		
	}

	public boolean collideEndMapBullet(Bullet bullet){
		
		Vector2 position = bullet.getPosition();
		
		if(position.x < 0 || position.x + bullet.getWidth() > getWidth()
				|| position.y < 0 || position.y + bullet.getHeight() > getHeight()){
			
			bullet.kill(null);
			
			return true;
			
		}

		return false;
		
	}
	
	public void collideEndMapEntity(Entity entity){
		
		Vector2 position = entity.getPosition();
		Vector2 velocity = entity.getVelocity();
		
		if(position.x < 0){
			
			position.x = 1;
			velocity.x = -velocity.x / 2;
		
		}else if(position.x + entity.getWidth() > getWidth()){
			
			position.x = getWidth() - entity.getWidth() - 1;
			velocity.x = -velocity.x / 2;
		
		}
		
		if(position.y < 0){
			
			position.y = 1;
			velocity.y = -velocity.y / 2;
		
		}else if(position.y + entity.getHeight() > getHeight()){
			
			position.y = getHeight() - entity.getHeight() - 1;
			velocity.y = -velocity.y / 2;
			
		}
		
	}

	public int getWidth(){ return width; }
	public int getHeight(){ return height; }
}
