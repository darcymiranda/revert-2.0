package com.dmiranda.revert.shared;

public class SpaceStation extends Building {

	public SpaceStation(Player owner, float x, float y) {
		super(owner, x, y, 256, 256);
		super.setOwnerPlayer(owner);

		setHealth(1250, 1250);
        createCollisionCircle(50);
		
	}

	@Override
	public void update(float delta){
        super.update(delta);

		rotation += 2f * delta;
		
	}

}
