package com.dmiranda.revert.shared;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.shared.bullet.Bullet;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GameMap {

	private int width, height;
	
	public GameMap(int width, int height){
		
		this.width = width;
		this.height = height;
		
	}

    public void load(){

        BufferedImage bufferedImage = null;
        File path = new File(new File(GameMap.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParentFile().getParentFile() + "/revert-desktop/assets/data/maps/map.png");

        try {
            bufferedImage = ImageIO.read(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int x = 0; x < bufferedImage.getWidth(); x++){
            for(int y = 0; y < bufferedImage.getHeight(); y++){
                int rgb = bufferedImage.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;

                if(r == 0 && g == 0 && b == 0){
                    Asteroid asteroid = new Asteroid(MathUtils.random(63), x * 64, y * 64, MathUtils.random(365));
                    if(Network.clientSide){
                        asteroid.setTexture(Revert.getLoadedTexture("asteroid.png").split(64*128, 128)[MathUtils.random(63)][0]);
                    }
                    GameWorld.entityManager.addLocalEntity(asteroid);
                }
            }
        }

        width = bufferedImage.getWidth() * 64;
        height = bufferedImage.getHeight() * 64;
    }

	public boolean collideEndMapBullet(Bullet bullet){
		
		Vector2 position = bullet.getPosition();
		
		if(position.x < 0 || position.x + bullet.getWidth() > getWidth()
				|| position.y < 0 || position.y + bullet.getHeight() > getHeight()){
			
			bullet.die(null);
			
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
