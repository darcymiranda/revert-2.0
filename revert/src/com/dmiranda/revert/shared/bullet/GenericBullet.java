package com.dmiranda.revert.shared.bullet;

import com.dmiranda.revert.Revert;
import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.shared.weapon.Weapon;

public class GenericBullet extends Bullet {
	
	public GenericBullet(Weapon weapon){
		this(weapon, 0, 0, 0, 0, 0, 0);
	}

	public GenericBullet(Weapon weapon, float x, float y, int width, int height,
			float speed, float direction) {
		super(weapon, x, y, width, height, speed, direction);
		
		if(Network.clientSide){
			setTexture(Revert.getLoadedTexture("gbullet.png"));
		}
	}

	@Override
	public Bullet newInstance(int damage, float direction, float speed) {
		return new GenericBullet(weapon, position.x, position.y, 8, 16, speed, direction);
	}

}
