package com.dmiranda.revert.shared;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.GameWorldClient;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.client.NetSimulateState;
import com.dmiranda.revert.effects.LightExpire;
import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.shared.bullet.Bullet;
import com.dmiranda.revert.shared.weapon.Weapon;

public class Unit extends Entity {
	
	public static final int UT_SPACESTATION = 1,
							UT_FIGHTER = 2,
                            UT_SATGUN = 3;
	
	protected float health, maxHealth;
	
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
    protected void onCreateClient(){
        super.onCreateClient();
    }
	
	@Override
	public void update(float delta){
		super.update(delta);
		
		if(clientNetSim != null){
			clientNetSim.update(delta);
		}
		
		for(int i = 0; i < weapons.size(); i++){
			
			Weapon weapon = weapons.get(i);
			weapon.update(delta);
			weapon.setShooting(shooting);
		}
		
	}

    @Override
    public void render(SpriteBatch sb){
        super.render(sb);

        for(int i = 0; i < weapons.size(); i++){
            Weapon weapon = weapons.get(i);
            weapon.render(sb);
        }
    }

    @Override
    protected void onDeath(Entity killer){
        for(int i = 0; i < weapons.size(); i++){
            weapons.get(i).remove();
        }

        if(Network.clientSide){

            float cx = getCenterX(), cy = getCenterY();

            LightExpire light = new LightExpire(new Color(1f, 0.9f, 0.9f, 1f), 16, (getWidth() + getHeight()) / 2 * 5, 32, cx, cy);
            light.setExpireOption(LightExpire.REMOVE);
            GameWorld.entityManager.addLocalEntity(light);
            GameWorldClient.particleSystem.addNewEffect("expo1", getId(), cx, cy);
        }

    }
	
	@Override
	protected void onHit(Entity hitter){
		super.onHit(hitter);
		
		if(hitter instanceof Bullet){
			
			Bullet bullet = (Bullet) hitter;
		
			applyDamage(bullet.getOwnerEntity(), bullet.getDamage());
			
		}
		
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
	
	public void addWeapon(Weapon weapon){
        weapons.add(weapon);
        if(Network.clientSide){
            weapon.onClientCreate();
        }
    }
	
	public NetSimulateState getClientNetSim(){ return clientNetSim; }
	public Entity getLastHitBy(){ return lastHitBy; }
	public float getHealth(){ return health; }
	public float getMaxHealth(){ return maxHealth; }
	public ArrayList<Weapon> getWeapons(){ return weapons; }
	public Weapon getPrimaryWeapon(){ return weapons.size() > 0 ? weapons.get(0) : null; }
	
	public boolean isShooting(){ return shooting; }
	public void setShooting(boolean shooting){ this.shooting = shooting; }
	

}
