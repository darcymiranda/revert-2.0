package com.dmiranda.revert.shared.weapon;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.Effect;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.GameWorld;
import com.dmiranda.revert.shared.bullet.Bullet;
import com.dmiranda.revert.shared.bullet.GenericBullet;


public class Weapon {
	
	protected Random rand;
	
	public float bulletSpeed;
	public float fireRate, bonusFireRate;
	public float spread;
	public float aimDirection;
	
	protected int minDamage;
	protected int maxDamage;
	protected float fireRateIncr;
	protected int maxAmmo;
	protected int curAmmo;
	protected int storedAmmo;
	protected float reloadTime;
	protected float reloadTimeIncr;
	protected float startUpDelay;
	protected float startUpDelayIncr;
	
	protected boolean infTotalAmmo;
	protected boolean infAmmo;
	protected boolean canShoot;
	protected boolean reloading;
	protected boolean canInteruptReload;
	protected boolean shooting;

	protected String name;
	protected Entity owner;
	protected Bullet bullet;
	protected Vector2 locOffset;
	
	private float locOffsetDegree;
	private float locOffsetDistance;
	// protected SoundEffect soundShoot;
	// protected SoundEffect soundReload;
	
	private TextureRegion[] muzzleFlashes;

	/**
	 * A weapon that returns bullets with .action()
	 * @param owner
	 * @param name
	 * @param minDamage
	 * @param maxDamage
	 * @param fireRate
	 * @param spread
	 * @param bulletSpeed
	 * @param maxAmmo
	 * @param reloadSpeed
	 * @param startUpDelay
	 */
	public Weapon(Entity owner, String name, int minDamage, int maxDamage, float fireRate,
			float spread, float bulletSpeed, int maxAmmo, float reloadSpeed, float startUpDelay) {
		
		this.owner = owner;
		this.name = name;
		this.minDamage = minDamage;
		this.maxDamage = maxDamage;
		this.fireRate = fireRate;
		this.spread = spread;
		this.bulletSpeed = bulletSpeed;
		this.maxAmmo = maxAmmo;
		this.curAmmo = maxAmmo;
		this.storedAmmo = maxAmmo * 2;
		this.reloadTime = reloadSpeed;
		this.startUpDelay = startUpDelay;
		
		rand = new Random(GameWorld.serverSeed);
		rand.nextFloat();
		rand.nextFloat();
		
		locOffset = new Vector2(0, 0);
		
		infTotalAmmo = true;
		
		setBullet(new GenericBullet(this));
		
		if(Network.clientSide){
			muzzleFlashes = Revert.getLoadedTexture("muzzle-flash.png").split(16, 16)[0];
		}
		
	}
	
	public void setLocation(float degree, float distance){
		locOffsetDegree = degree;
		locOffsetDistance = distance;
	}
	
	private boolean tickReloadSpeed(float delta){
		
		if(startUpDelayIncr > 0){
			startUpDelayIncr -= delta;
			return false;
		}
		
		if(reloading){
			if(reloadTimeIncr > reloadTime){
				reloading = false;
				refillAmmo();
				reloadTimeIncr = 0;
			}else{
				reloadTimeIncr += delta;
				return false;
			}
		}
		return true;
	}
	
	private boolean tickFireRate(float delta){
		if(fireRateIncr >= fireRate + bonusFireRate){
			fireRateIncr = fireRate + bonusFireRate;
			return true;
		}else{
			fireRateIncr += delta;
			return false;
		}
	}
	
	public void update(float delta){
		
		delta *= 1000;
		
		locOffset.x = -MathUtils.sinDeg(owner.getRotation() + locOffsetDegree) * locOffsetDistance;
		locOffset.y = MathUtils.cosDeg(owner.getRotation() + locOffsetDegree) * locOffsetDistance;
		
		aimDirection = owner.getRotation();
		
		if(curAmmo < 1 && !reloading){
			if(tickFireRate(delta)) reload();	// Delay before able to reload
			canShoot = false;
			return;
		}
		
		canShoot = tickReloadSpeed(delta);
		if(canShoot){
			canShoot = tickFireRate(delta);
		}
		
		if(shooting){
			shoot();
		}
		
	}
	
	public int getDamageRoll(){
		return rand.nextInt(maxDamage) + minDamage;
	}
	
	public void refillAmmo(){
		
		if(infTotalAmmo){
			curAmmo = maxAmmo;
			return;
		}
		
		if(maxAmmo < storedAmmo){
			
			int difference = maxAmmo - curAmmo;
			curAmmo = maxAmmo;
			storedAmmo -= difference;
			
		}else{
			curAmmo = storedAmmo;
			storedAmmo = 0;
		}
		
		
	}
	
	public void reload(){
		
		if(infAmmo){
			refillAmmo();
		}
		
		if(curAmmo >= maxAmmo || reloading || storedAmmo < 1) return;
		
		reloading = true;
		
		/*
		if(soundReload != null){
			soundReload.play();
		}
		*/

	}
	
	public void aim(float aimDirection){
		this.aimDirection = aimDirection;
	}
	
	protected float getEffectedAccuracy(){
		float min = spread - 1;
		float max = 1 - spread;
		return aimDirection - (rand.nextFloat() * (max - min) + min) * 100;
	}
	
	private boolean preAction(){
		
		if(!canShoot)
			return false;
		
		
		if(reloading && curAmmo != 0 && canInteruptReload)
			reloading = false;
		
		/*
		if(soundShoot != null){
			//soundShoot.play(1,0.75f);
			soundShoot.play();
		}
		*/
			
		curAmmo--;
		fireRateIncr = 0;
		
		return true;
		
	}
	
	public void setShooting(boolean shooting){
		
		if(shooting == !this.shooting){
			startUpDelayIncr = startUpDelay;
		}
		
		this.shooting = shooting;
	}
	
	protected Bullet[] onShoot(){
		
		Bullet[] bullets = new Bullet[1];
		bullets[0] = bullet.newInstance(getDamageRoll(), getEffectedAccuracy(), bulletSpeed);
		
		if(Network.clientSide){
			
			Vector2 r = getRelativeLocation();
			
			Effect muzzle = new Effect(r.x - 16 / 2, r.y - 16 / 2, 16, 16);
			muzzle.setVelocity(new Vector2(owner.getVelocity()).scl(2));
			muzzle.setRotation(owner.getRotation());
			muzzle.setTexture(muzzleFlashes[MathUtils.random(muzzleFlashes.length - 1)]);
			
			Effect flash = new Effect(r.x - 64 / 2, r.y - 64 / 2, 64, 64);
			flash.setVelocity(new Vector2(owner.getVelocity()).scl(2));
			flash.setRotation(owner.getRotation());
			flash.setTexture(Revert.getLoadedTexture("glow.png"));
			
			GameWorld.entityManager.addLocalEntity(muzzle);
			GameWorld.entityManager.addLocalEntity(flash);
		}
		
		return bullets;
	}
	
	public Bullet[] shoot() {
		if(!preAction()) return null;
		
		Bullet[] bullets = onShoot();
		for(int i = 0; i < bullets.length; i++){
			GameWorld.entityManager.addLocalEntity(bullets[i]);
		}
		
		return bullets;
	}
	
	public Weapon newInstance(Entity owner){
		return new Weapon(owner, name, minDamage, maxDamage, fireRate, spread, bulletSpeed, maxAmmo, reloadTime, startUpDelay);
	}
	
	public String toString(){
		return this.getClass().getName() + "  Owner:" + owner;
	}
	
	public Vector2 getRelativeLocation(){ 
		return new Vector2(owner.getCenterX() + locOffset.x, owner.getCenterY() + locOffset.y);
	}
	
	public void addAmmo(int ammo){ this.storedAmmo += ammo; }
	
	// public void setSoundShoot(SoundEffect sound){ this.soundShoot = sound; }
	// public void setSoundReload(SoundEffect sound){ this.soundReload = sound; }
	public void setBullet(Bullet bullet){ this.bullet = bullet; }
	public void setInfiniteMode(boolean mode){ this.infAmmo = mode; this.infTotalAmmo = mode; }
	
	public int getMinDamage(){ return minDamage; }
	public int getMaxDamage(){ return maxDamage; }
	public int getAmmoRemaining(){ return curAmmo; }
	public int getAmmoCapacity(){ return maxAmmo; }
	public int getStoredAmmo(){ return storedAmmo; }
	public float getBulletSpeed(){ return bulletSpeed; }
	public String getName(){ return name; }
	public Random getWeaponRand(){ return rand; }
	public Entity getOwner(){ return owner; }
	
	public boolean isInfiniteAmmo(){ return infTotalAmmo; }

}
