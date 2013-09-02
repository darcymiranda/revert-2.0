package com.dmiranda.revert.shared;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.network.Network;

import java.util.ArrayList;

/**
 * User: dmiranda
 * Date: 9/2/13
 * Time: 2:07 PM
 */
public class Building extends Unit {

    private ArrayList<Turret> turrets = new ArrayList<Turret>();

    public Building(Player player, float x, float y, int width, int height) {
        super(x, y, width, height);
        setOwnerPlayer(player);
    }

    @Override
    public void update(float delta){
        super.update(delta);

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
        if(Network.clientSide){
            turret.setTexture(Revert.getLoadedTexture("turret.png"));
        }
        turrets.add(turret);
    }

    public ArrayList<Turret> getTurrets(){ return turrets; }

}
