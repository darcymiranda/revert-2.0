package com.dmiranda.revert.shared;

import java.util.ArrayList;

import com.dmiranda.revert.client.NetSimulateState;
import com.dmiranda.revert.shared.bullet.Bullet;
import com.dmiranda.revert.shared.weapon.Weapon;

public class Unit extends Entity {
	
	public static final int UT_SPACESTATION = 0,
							UT_FIGHTER = 1;
	
	protected float health, maxHealth;
	protected float rotationSpeed;
	protected float rotateTo;
	
	protected boolean shooting;
	
	private Entity lastHitBy;
	private NetSimulateState clientNetSim;
	
	private ArrayList<Weapon> weapons = new ArrayList<Weapon>();

	public Unit(float x, float y, int width, int height) {
		super(x, y, width, height);
	}
	
	public void clientStartNetSim(){
		clientNetSim = new NetSimulateState(this);
	}
	
	@Override
	public void update(float delta){
		super.update(delta);
		
		if(clientNetSim != null){
			
			clientNetSim.update(delta);
			
		}
		
		// Rotate based on the rotation speed
		float d = rotateTo - rotation;
		if(d > 180){
			d -= 360;
		}
		else if(d < -180){
			d += 360;
		}
		
		rotation += d * rotationSpeed * delta;
		
		for(int i = 0; i < weapons.size(); i++){
			weapons.get(i).update(delta);
		}
		
	}
	
	@Override
	public void onHit(Entity hitter){
		super.onHit(hitter);
		
		if(hitter instanceof Bullet){
			
			Bullet bullet = (Bullet) hitter;
		
			applyDamage(bullet.getOwnerEntity(), bullet.getDamage());
			
		}
		
	}
	
	public void onShoot(){}
	
	public ArrayList<Bullet[]> shoot(){
		
		ArrayList<Bullet[]> allBullets = new ArrayList<Bullet[]>();
		
		for(int i = 0; i < weapons.size(); i++){
			
			Bullet[] bullets = weapons.get(i).action();
			
			if(bullets != null){
				allBullets.add(bullets);
			}
			
		}
		
		if(allBullets.size() > 0){
			onShoot();
		}
		
		return allBullets;
	}
	
	public void applyDamage(Entity attacker, float damage){
		lastHitBy = attacker;
		health -= damage;
	}
	
	public void setHealth(float health){
		if(maxHealth == 0){
			setHealth(health, health);
		} else {
			setHealth(health, maxHealth);
		}
	}
	
	public void setHealth(float health, float maxHealth){
		
		this.maxHealth = maxHealth;
		this.health = health;
		
		if(health > maxHealth){
			this.health = maxHealth;
		}
		
		if(maxHealth < 1){
			this.maxHealth = 1;
		}
		
	}
	
	public void setParameters(float health, float rotationSpeed){
		setHealth(health, health);
		this.rotationSpeed = rotationSpeed;
	}
	
	public void addWeapon(Weapon weapon){ weapons.add(weapon); }
	
	public NetSimulateState getClientNetSim(){ return clientNetSim; }
	public Entity getLastHitBy(){ return lastHitBy; }
	public float getHealth(){ return health; }
	public float getMaxHealth(){ return maxHealth; }
	public ArrayList<Weapon> getWeapons(){ return weapons; }
	public Weapon getPrimaryWeapon(){ return weapons.size() > 0 ? weapons.get(0) : null; }
	
	public boolean isShooting(){ return shooting; }
	public void setShooting(boolean shooting){ this.shooting = shooting; }
	
	public void rotateTo(float rotateTo){ 
		if(rotateTo > 360) rotateTo -= 360;
		else if(rotateTo < 0) rotateTo += 360;
		this.rotateTo = rotateTo; 
	}
	public float getRotateTo(){ return rotateTo; }
	

}
