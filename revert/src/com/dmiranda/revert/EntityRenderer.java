package com.dmiranda.revert;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.shared.Asteroid;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.Unit;
import com.dmiranda.revert.shared.bullet.Bullet;

public class EntityRenderer {
	
	private TextureRegion[] asteroidTypes;
	
	private GameWorldClient world;
	
	public EntityRenderer(GameWorldClient world){
		this.world = world;
	}
	
	public void loadGraphics(){
		
		asteroidTypes = Revert.getLoadedTexture("asteroid.png").split(128, 128)[0];
		
	}
	
	public void render(SpriteBatch sb, Entity entity){
		
		if(entity instanceof Unit){
			
			Unit unit = (Unit)entity;
			
			// healthbar
			if(unit.getHealth() > 0 && unit.getHealth() != unit.getMaxHealth()){
				
				TextureRegion healthTexture = Revert.getLoadedTexture("health.png");
				
				Vector2 position = unit.getPosition();
				float barLength = unit.getWidth() * (unit.getHealth() / unit.getMaxHealth());
				
				sb.draw(healthTexture, position.x, position.y + unit.getHeight() + 3, barLength, 2);
				
			}
		
		}
		else if(entity instanceof Asteroid){
			
			Asteroid asteroid = (Asteroid) entity;
			
			TextureRegion asteroidFrame = asteroidTypes[asteroid.getAsteroidType()];
			
			sb.draw(asteroidFrame,
					asteroid.getPosition().x,
					asteroid.getPosition().y,
					asteroid.getWidth() / 2,
					asteroid.getHeight() / 2,
					asteroid.getWidth(),
					asteroid.getHeight(),
					1f, 1f,
					asteroid.getRotation());
		}
	}

}
