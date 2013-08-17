package com.dmiranda.revert.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.shared.Asteroid;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.Player;

public class Minimap {
	
	private Texture background;
	private TextureRegion[] icons;
	private Vector2 mapPosition;
	private float iconSize;
	private float border;
	private float width, height;
	private float scale;
	private float distance;
	
	private Revert game;
	private Entity centerEntity;
	
	public Minimap(Revert game){
		
		this.game = game;
		
		iconSize = 2;
		width = 200;
		height = 200;
		border = 2;
		distance = 2000;
		scale = width / 2 / distance;
		
		mapPosition = new Vector2( Gdx.graphics.getWidth() - 210, 10);
		
	}
	
	public void loadGraphics(){
		
		icons = TextureRegion.split(Revert.getLoadedTexture("minimap-icons.png"), 3, 3)[0];
		background = Revert.getLoadedTexture("mm_background.png");
		
	}
	
	public void setCenterOnShip(Entity entity){
		this.centerEntity = entity;
	}
	
	public void renderUI(SpriteBatch sb){
		sb.draw(background, mapPosition.x, mapPosition.y);
	}
	
	public void render(SpriteBatch sb, Entity entity){
		
		if(entity == null || centerEntity == null) return;
		
		Vector2 relativePosition = getRelativePosition(entity.getPosition().cpy(), centerEntity.getCenterPosition().cpy());
		
		if(relativePosition != null){
			
			if(entity instanceof Asteroid){
				
				sb.draw(icons[3], relativePosition.x, relativePosition.y);
				
			}else{
			
				Player localPlayer = game.world.getLocalPlayerFast();
				if(localPlayer != null){
					if(localPlayer.isAllies(entity.getOwnerPlayer())){
						sb.draw(icons[0], relativePosition.x, relativePosition.y);
					}else{
						sb.draw(icons[1], relativePosition.x, relativePosition.y);
					}
				}
				
			}
		}
		
	}
	
	private Vector2 getRelativePosition(Vector2 position, Vector2 center){
		
		float mpx = mapPosition.x;
		float mpy = mapPosition.y;
		
		float x = mpx + ( width / 2 ) + ( position.x - center.x ) * scale;
		float y = mpy + ( height / 2 ) + ( position.y - center.y ) * scale;
		
		if(x > mpx && (x + iconSize) < (mpx + width - border) &&
		   y > mpy && (y + iconSize) < (mpy + height - border)){
			   
			   return new Vector2(x, y);
			   
		}
		
		return null;
		
	}

}
