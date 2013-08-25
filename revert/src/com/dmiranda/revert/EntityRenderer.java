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
			
/*			if(entity instanceof Ship){
				
				Vector2 locOffsetEngine = new Vector2();
				locOffsetEngine.x = -MathUtils.sinDeg(entity.getRotation() + 180) * 20.5f;
				locOffsetEngine.y = MathUtils.cosDeg(entity.getRotation() + 180) * 20.5f;
				
				// engine effects
				if(unit.getEntityActionState().getCurrentState() == EntityActionState.STATE_ON){
					
					if(GameWorldClient.particleSystem.getEffectsFollow().containsKey(unit)){
						GameWorldClient.particleSystem.getEffectsFollow().get(unit).getEffect().start();
					}
					
					TextureRegion engineFrame = anfighterEngine.getKeyFrame(unit.getEntityActionState().getStateTime(), true);
					TextureRegion engineLight = anfighterEngineLight.getKeyFrame(unit.getEntityActionState().getStateTime(), true);
					
					sb.draw(engineFrame,
							unit.getCenterX() + locOffsetEngine.x - engineFrame.getRegionWidth() / 2,
							unit.getCenterY() + locOffsetEngine.y - engineFrame.getRegionHeight() / 2,
							engineFrame.getRegionWidth() / 2,
							engineFrame.getRegionHeight() / 2,
							engineFrame.getRegionWidth(),
							engineFrame.getRegionHeight(),
							1, 1,
							unit.getRotation());
					
					sb.draw(engineLight,
							unit.getCenterX() + locOffsetEngine.x - engineLight.getRegionWidth() / 2,
							unit.getCenterY() + locOffsetEngine.y - engineLight.getRegionHeight() / 2,
							engineLight.getRegionWidth() / 2,
							engineLight.getRegionHeight() / 2,
							engineLight.getRegionWidth(),
							engineLight.getRegionHeight(),
							1, 1,
							unit.getRotation());
				
				}
				else{
					
					if(GameWorldClient.particleSystem.getEffectsFollow().containsKey(unit)){
						GameWorldClient.particleSystem.getEffectsFollow().get(unit).getEffect().allowCompletion();
					}
					
				}
				
				// the actual ship
				sb.draw(Revert.getLoadedTexture("fighter.png"),
						entity.getPosition().x,
						entity.getPosition().y, 
						entity.getWidth() / 2,
						entity.getHeight() / 2,
						entity.getWidth(),
						entity.getHeight(),
						1, 1,
						entity.getRotation(),
						0, 0,
						(int)entity.getWidth(),
						(int)entity.getHeight(),
						false, false);
					
				
				// name
				if(entity.getOwnerPlayer() != null){
					String name = entity.getOwnerPlayer().username;
					
					if(entity.getOwnerPlayer().team == 0)
						world.game.getFontTiny().setColor(Color.RED);
					else if(entity.getOwnerPlayer().team == 1)
						world.game.getFontTiny().setColor(Color.BLUE);
					
					world.game.getFontTiny().draw(sb, name, 
												   entity.getCenterX() - world.game.getFontTiny().getSpaceWidth() * name.length(),
												   entity.getPosition().y + entity.getHeight() + 5);
					
					world.game.getFontTiny().setColor(Color.WHITE);
				}
			}*/
			
/*			else if(unit instanceof SpaceStation){
				
				sb.draw(Revert.getLoadedTexture("spacestation.png"),
						unit.getPosition().x,
						unit.getPosition().y, 
						unit.getWidth() / 2,
						unit.getHeight() / 2,
						unit.getWidth(),
						unit.getHeight(),
						1, 1,
						unit.getRotation(),
						0, 0,
						(int)unit.getWidth(),
						(int)unit.getHeight(),
						false, false);
				
				SpaceStation spaceStation = (SpaceStation)unit;
				for(int i = 0; i < spaceStation.getTurrets().size(); i++){
					Turret turret = spaceStation.getTurrets().get(i);
					
					sb.draw(Revert.getLoadedTexture("turret.png"),
							turret.getPosition().x,
							turret.getPosition().y, 
							turret.getWidth() / 2,
							turret.getHeight() / 2,
							turret.getWidth(),
							turret.getHeight(),
							1, 1,
							turret.getRotation(),
							0, 0,
							(int)turret.getWidth(),
							(int)turret.getHeight(),
							false, false);
				}

				
			}*/
		
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
		else if(entity instanceof Bullet){
			
			sb.draw(Revert.getLoadedTexture("gbullet.png"),
					entity.getPosition().x,
					entity.getPosition().y, 
					entity.getWidth() / 2,
					entity.getHeight() / 2,
					entity.getWidth(),
					entity.getHeight(),
					1, 1,
					entity.getRotation());
			
/*			TextureRegion glow = Revert.getLoadedTexture("glow.png");
			
			sb.draw(glow,
					entity.getCenterX() - glow.getRegionWidth() / 2,
					entity.getCenterY() - glow.getRegionHeight() / 2, 
					glow.getRegionWidth() / 2,
					glow.getRegionHeight() / 2,
					glow.getRegionWidth(),
					glow.getRegionHeight(),
					1, 1,
					entity.getRotation());*/
		
		}	
		

	}

}
