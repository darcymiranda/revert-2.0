package com.dmiranda.revert.server;


import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.network.Network.EntityDeath;
import com.dmiranda.revert.network.Network.EntitySpawnSelf;
import com.dmiranda.revert.network.Network.UnitUpdate;
import com.dmiranda.revert.network.properties.PUnit;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.EntityFactory;
import com.dmiranda.revert.shared.GameWorld;
import com.dmiranda.revert.shared.Player;
import com.dmiranda.revert.shared.Ship;
import com.dmiranda.revert.shared.SpaceStation;
import com.dmiranda.revert.shared.Unit;
import com.esotericsoftware.kryonet.Connection;

public class GameWorldServer extends GameWorld {
	
	public RevertServer game;
	
	private UnitUpdate unitUpdate = new UnitUpdate();
	private float tickTime;
	
	public GameWorldServer(RevertServer game){
		super();

		this.game = game;
		
		create();
	}
	
	public void create(){
		
		/*
		Asteroid asteroid = new Asteroid(0, 500, 500, 85);
		asteroid.randAsteroidType();
		entityManager.addEntity(asteroid);
		
		asteroid = new Asteroid(0, 100, 500, 125);
		asteroid.randAsteroidType();
		entityManager.addEntity(asteroid);
		
		asteroid = new Asteroid(0, 750, 100, 190);
		asteroid.randAsteroidType();
		entityManager.addEntity(asteroid);
		
		asteroid = new Asteroid(0, 250, 800, 2);
		asteroid.randAsteroidType();
		entityManager.addEntity(asteroid);
		
		asteroid = new Asteroid(0, 300, 350, 30);
		asteroid.randAsteroidType();
		entityManager.addEntity(asteroid);
		*/
		
		entityManager.addEntity(new SpaceStation(players.get(COMPUTER_RED), 372, 372));
		entityManager.addEntity(new SpaceStation(players.get(COMPUTER_BLUE), 2188, 2188));
		
	}
	
	public int serverCreateShip(Player player, float x, float y){
		
		Entity entity = EntityFactory.server().createEntity(Unit.UT_FIGHTER, player, x, y);
		
		return entityManager.addEntity(entity);
		
	}
	
	
	@Override
	public void update(float delta){
		
		unitUpdate.properties.clear();
		
		entityManager.update(delta);
		
		if(tickTime < 1){
		
			game.server.sendToAllUDP(unitUpdate);
		
			tickTime = Network.CLIENT_SEND_INTERVAL;
		}
		else{
		
			tickTime--;
		}
		
	}
	
	@Override
	public void entityUpdate(Entity entity){
		
		if(entity.isNetworkEnabled()){
			
			if(entity instanceof Unit){
				
				Unit unit = (Unit) entity;
				
				// Confirm entity deaths that should be dead
				if(unit.getHealth() < 1){
					unit.kill(unit.getLastHitBy());
					
					EntityDeath death = new EntityDeath();
					death.id = unit.getId();
					death.killerid = unit.getLastHitBy() == null ? -1 : unit.getLastHitBy().getId();	 //may sometimes return null on last hit by
					game.server.sendToAllTCP(death);
					
					// Temp - respawn the killed entity for the player
					float x = unit.getOwnerPlayer().getSpawnPoint().x;
					float y = unit.getOwnerPlayer().getSpawnPoint().y;
					int entityId = game.world.serverCreateShip(unit.getOwnerPlayer(), x, y);
					
					EntitySpawnSelf spawn = new EntitySpawnSelf();
					spawn.id = entityId;
					spawn.x = x;	
					spawn.y = y;
					spawn.type = 0;
					game.server.sendToTCP(unit.getOwnerPlayer().id, spawn);
					
				}
				
				if(tickTime < 1){
					
					PUnit pUnit = new PUnit();
					
					pUnit.playerid = unit.getOwnerPlayer().id;
					pUnit.type = unit.getUnitType();
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
