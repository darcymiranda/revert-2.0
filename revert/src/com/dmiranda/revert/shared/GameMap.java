package com.dmiranda.revert.shared;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.Camera;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.shared.bullet.Bullet;

public class GameMap {
	
	public static int TILE_WIDTH = 512, TILE_HEIGHT = 512;
	
	private Tile[][] tiles;
	private int width, height;
	
	private TextureRegion texture;
	
	public GameMap(int width, int height){
		
		this.width = width;
		this.height = height;
		
		tiles = new Tile[width][height];
		
		reset();
		
	}

	public void reset(){
		
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				
				tiles[x][y] = new Tile();
				tiles[x][y].image = 0;
			}
		}
	}
	
	public void loadGraphics(){
		texture = Revert.getLoadedTexture("space.png");
	}
	
	public boolean collideEndMapBullet(Bullet bullet){
		
		Vector2 position = bullet.getPosition();
		
		if(position.x < 0 || position.x + bullet.getWidth() > width * TILE_WIDTH
				|| position.y < 0 || position.y + bullet.getHeight() > height * TILE_HEIGHT){
			
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
		
		}else if(position.x + entity.getWidth() > width * TILE_WIDTH){
			
			position.x = width * TILE_WIDTH - entity.getWidth() - 1;
			velocity.x = -velocity.x / 2;
		
		}
		
		if(position.y < 0){
			
			position.y = 1;
			velocity.y = -velocity.y / 2;
		
		}else if(position.y + entity.getHeight() > height * TILE_HEIGHT){
			
			position.y = height * TILE_HEIGHT - entity.getHeight() - 1;
			velocity.y = -velocity.y / 2;
			
		}
		
	}
	
	public void render(SpriteBatch sb, int layer, Camera camera){
		
		Vector2 camPos = camera.getModifiedPosition();
		Vector2 camView = camera.getModifiedViewport();
		
		int px = (int) camPos.x / TILE_WIDTH;
		int py = (int) camPos.y / TILE_HEIGHT;
		
		int vx = (int) camView.x / TILE_WIDTH + 2;
		int vy = (int) camView.y / TILE_HEIGHT + 2;
		
		for(int x = px; x < px + vx; x++){
			for(int y = py; y < py + vy; y++){
				if(x < 0 || y < 0 || x > width - 1 || y > height - 1) continue;
				
				sb.draw(texture, x * TILE_WIDTH, y * TILE_HEIGHT);
				//sb.draw(texture, x * TILE_WIDTH, y * TILE_HEIGHT, tile.image * TILE_WIDTH, 0, TILE_WIDTH, TILE_HEIGHT);
			}
		}
	}
	
	public int getWidth(){ return width * TILE_WIDTH; }
	public int getHeight(){ return height * TILE_HEIGHT; }
}
