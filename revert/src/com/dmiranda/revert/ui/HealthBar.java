package com.dmiranda.revert.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.shared.Unit;

public class HealthBar {

	private Unit owner;
	private Texture texture;
	
	public HealthBar(Unit owner){
		this.owner = owner;
		texture = Revert.assets.get("health.png", Texture.class);
	}
	
	public void render(SpriteBatch sb){
	
		Vector2 position = owner.getPosition();
		float barLength = owner.getWidth() * (owner.getHealth() / owner.getMaxHealth());
		
		sb.draw(texture, position.x, position.y - owner.getHeight() - 5, barLength, 2);
	}
	
}
