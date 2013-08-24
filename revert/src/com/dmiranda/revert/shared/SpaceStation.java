package com.dmiranda.revert.shared;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dmiranda.revert.Revert;


public class SpaceStation extends Unit {
	
	private ArrayList<Turret> turrets = new ArrayList<Turret>();

	public SpaceStation(Player owner, float x, float y) {
		super(x, y, 256, 256);
		super.setOwnerPlayer(owner);
		
		setHealth(5250, 5250);
		createCollisionCircle(50);
		
	}
	

	@Override
	public void update(float delta){
		
		rotation += 2f * delta;
		
		for(int i = 0; i < turrets.size(); i++){
			turrets.get(i).update(delta);
		}
		
	}
	
	@Override
	public void render(SpriteBatch sb){
		super.render(sb);
		for(int i = 0; i < turrets.size(); i++){
			turrets.get(i).render(sb);
		}
	}
	
	/**
	 * Add a turret to the building
	 * @param x offset from the center of the parent
	 * @param y offset from the center of the parent
	 */
	public void addTurret(float x, float y){
		Turret turret = new Turret(this, x, y);
		if(Revert.CLIENT_SIDE){
			turret.setTexture(Revert.getLoadedTexture("turret.png"));
		}
		turrets.add(turret);
	}
	
	public ArrayList<Turret> getTurrets(){ return turrets; }

}
