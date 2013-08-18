package com.dmiranda.revert.shared;

import java.util.ArrayList;


public class SpaceStation extends Unit {
	
	private ArrayList<Turret> turrets = new ArrayList<Turret>();

	public SpaceStation(Player owner, float x, float y) {
		super(x, y, 256, 256);
		super.setOwnerPlayer(owner);
		
		setHealth(5250, 5250);
		createCollisionCircle(50);
		
		addTurret(33, 80);
		addTurret(90 + 20, 80);
		addTurret(180, 80);
		addTurret(270 - 20, 80);
		addTurret(360 - 40, 80);
		
	}
	

	@Override
	public void update(float delta){
		
		rotation += 2f * delta;
		
		for(int i = 0; i < turrets.size(); i++){
			turrets.get(i).update(delta);
		}
		
	}
	
	/**
	 * Add a turret to the building
	 * @param x offset from the center of the parent
	 * @param y offset from the center of the parent
	 */
	public void addTurret(float x, float y){
		turrets.add(new Turret(x, y, this));
	}
	
	public ArrayList<Turret> getTurrets(){ return turrets; }

}
