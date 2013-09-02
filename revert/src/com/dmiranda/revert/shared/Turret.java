package com.dmiranda.revert.shared;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.shared.weapon.Weapon;

public class Turret extends Unit {
	
	private Entity target;
	private Vector2 locOffset;
	private int range = 1100;
	private float locOffsetDegree;
	private float locOffsetDistance;
	private float targetFinderDelay = 500;
	private float targetFinderTick = 0;

	public Turret(Entity owner, float degree, float distance){
		super(owner.getCenterX(), owner.getCenterY(), 16, 16);
		
		this.locOffsetDegree = degree;
		this.locOffsetDistance = distance;
		
		locOffset = new Vector2();
		
		setOwnerEntity(owner);
		Weapon weapon = new Weapon(this, "Gun", 1, 1, MathUtils.random(350, 650), 0.99f, 1500, 100, 0, 800);
		weapon.setLocation(0, 16);
		addWeapon(weapon);
	}
	
	public void setTarget(Entity target){
		this.target = target;
	}
	
	public void update(float delta){
		super.update(delta);

		Entity ownerEntity = getOwnerEntity();
		
		if(target != null){
			rotation = -MathUtils.atan2(target.getCenterX() - getCenterX(), target.getCenterY() - getCenterY()) * (180 / MathUtils.PI);
		} else {
			rotation = ownerEntity.getRotation();
		}
		
		Vector2 ownerPosition = ownerEntity.getCenterPosition();
		
		locOffset.x = -MathUtils.sinDeg(ownerEntity.getRotation() + locOffsetDegree) * locOffsetDistance;
		locOffset.y = MathUtils.cosDeg(ownerEntity.getRotation() + locOffsetDegree) * locOffsetDistance;
		
		position.x = ownerPosition.x + locOffset.x - width / 2;
		position.y = ownerPosition.y + locOffset.y - height / 2;
		
		// Targeting
		if(targetFinderTick < 0){
			target = GameWorld.entityManager.getNearestEntity(ownerEntity, Ship.class, range, true);
			targetFinderTick = targetFinderDelay;
		}
		targetFinderTick -= delta * 1000;

		// Shoot whenever targeting
		shooting = (target != null);
		
	}
	
	public int getRange(){ return range; }

}
