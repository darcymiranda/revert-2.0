package com.dmiranda.revert.server;


import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.network.Network.EntityDeath;
import com.dmiranda.revert.network.Network.EntitySpawnSelf;
import com.dmiranda.revert.network.Network.UnitUpdate;
import com.dmiranda.revert.network.properties.PUnit;
import com.dmiranda.revert.shared.*;
import com.dmiranda.revert.tools.Tools;

import java.util.ArrayList;
import java.util.Iterator;

public class GameWorldServer extends GameWorld {
	
	public RevertServer game;

    private final float updateRadius = 2000 * 2000;
	private UnitUpdate unitUpdate = new UnitUpdate();
	private float tickTime;
	
	public GameWorldServer(RevertServer game){
		super();
		this.game = game;
		create();
	}
	
	public void create(){
		
		entityManager.addEntity(
				EntityFactory.server().createEntity(Unit.UT_SPACESTATION, players.get(COMPUTER_RED), 372, 372));
		entityManager.addEntity(
				EntityFactory.server().createEntity(Unit.UT_SPACESTATION, players.get(COMPUTER_BLUE), 2188, 2188));

        //map.load();
		
	}
	
	@Override
	public void update(float delta){
		
		unitUpdate.properties.clear();
		
		entityManager.update(delta);

        // Send entity updates to clients
		if(tickTime < 1){

            // only send entity updates to those in range
            Iterator<Player> it = players.values().iterator();
            while(it.hasNext()){
                Player player = it.next();

                // TODO: Use radius based from camera if ship is dead/null
                if(player.ship == null) continue;

                UnitUpdate unitUpdateCopy = new UnitUpdate();
                unitUpdateCopy.properties = new ArrayList<PUnit>();
                unitUpdateCopy.properties.addAll(unitUpdate.properties);

                for(int i = 0; i < unitUpdateCopy.properties.size(); i++) {
                    PUnit pUnit = unitUpdateCopy.properties.get(i);
                    if(!Tools.withinDistance(pUnit.x, pUnit.y, player.ship.getCenterX(), player.ship.getCenterY(), updateRadius)){
                        unitUpdateCopy.properties.remove(i);
                    }
                }

                if(unitUpdateCopy.properties.size() > 0) {
                    game.server.sendToUDP(player.id, unitUpdateCopy);
                }
            }

			tickTime = Network.CLIENT_SEND_INTERVAL;
		}else{
			tickTime--;
		}

        // Do respawns
        Iterator<Player> it = players.values().iterator();
        while(it.hasNext()){
            Player player = it.next();

            // ignore non-players
            if(player.id == GameWorld.COMPUTER_BLUE || player.id == GameWorld.COMPUTER_RED) continue;

            if(player.isDead()){

                if(player.getRespawnTimer() < 0){

                    float x = player.getSpawnPoint().x;
                    float y = player.getSpawnPoint().y;

                    Entity spawnedEntity = EntityFactory.server().createEntity(Unit.UT_FIGHTER, player, x, y);

                    GameWorld.entityManager.addEntity(spawnedEntity);

                    EntitySpawnSelf spawn = new EntitySpawnSelf();
                    spawn.id = spawnedEntity.getId();
                    spawn.x = x;
                    spawn.y = y;
                    spawn.type = Unit.UT_FIGHTER;
                    game.server.sendToTCP(spawnedEntity.getOwnerPlayer().id, spawn);

                } else {
                    player.reduceSpawnTimer(delta);
                }
            }
        }
		
	}
	
	@Override
	public void entityUpdate(Entity entity){
		
		if(entity.isNetworkEnabled()){
			
			if(entity instanceof Unit){
				
				final Unit unit = (Unit) entity;
				
				// Confirm entity deaths that should be dead
				if(unit.getHealth() < 1){
					unit.die(unit.getLastHitBy());
					
					EntityDeath death = new EntityDeath();
					death.id = unit.getId();
					death.killerid = unit.getLastHitBy() == null ? -1 : unit.getLastHitBy().getId();	 //may sometimes return null on last hit by

					game.server.sendToAllTCP(death);

                    unit.getOwnerPlayer().setRespawnTimer(3);
					
				}
				
				if(tickTime < 1){
					
					PUnit pUnit = new PUnit();
					pUnit.playerid = unit.getOwnerPlayer().id;
					pUnit.type = unit.getType();
					pUnit.id = unit.getId();
					pUnit.x = unit.getPosition().x;
					pUnit.y = unit.getPosition().y;
					pUnit.xv = unit.getVelocity().x;
					pUnit.yv = unit.getVelocity().y;
					pUnit.rt = unit.getRotateTo();
					pUnit.health = unit.getHealth();
					pUnit.shooting = unit.isShooting();
					
					if(entity instanceof Ship){
						
						Ship ship = (Ship) entity;
						pUnit.w = ship.getW();
						pUnit.a = ship.getA();
						pUnit.d = ship.getD();
						pUnit.s = ship.getS();
					}
					
					unitUpdate.properties.add(pUnit);
				}
			}
		}
	}


}
