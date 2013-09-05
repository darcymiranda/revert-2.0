package com.dmiranda.revert.shared;

/**
 * User: dmiranda
 * Date: 9/2/13
 * Time: 2:25 PM
 */
public class SatGun extends Building {

    public SatGun(Player player, float x, float y, int width, int height) {
        super(player, x, y, 32, 32);

        setHealth(750, 750);
        createCollisionCircle();
        addTurret(getCenterX(), getCenterY());
    }

    @Override
    public void update(float delta){
        super.update(delta);

    }

}
