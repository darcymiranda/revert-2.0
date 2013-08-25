package com.dmiranda.revert;

import com.dmiranda.revert.shared.Entity;

public class Effect extends Entity {

	private float life;
	
	public Effect(float x, float y, int width, int height, float life) {
		super(x, y, width, height);
		this.life = life;
	}
	
	public Effect(float x, float y, int width, int height) {
		this(x, y, width, height, 32);
	}
	
	@Override
	public void update(float delta){
		super.update(delta);
		
		life -= delta * 1000;
		
		if(life < 0){
			kill(null);
		}
	}
}
