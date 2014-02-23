package com.dmiranda.revert.shared;

import com.dmiranda.revert.GameWorldClient;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.shared.weapon.Weapon;

public class EntityFactory {
	
	private static EntityFactory instance = new EntityFactory();
	private static int CLIENT_SIDE = 0, SERVER_SIDE = 1;
	private static int side;
	
	/**
	 * Set to client mode.
	 * @return
	 */
	public static EntityFactory client(){
		side = CLIENT_SIDE;
		return instance;
	}
	
	/**
	 * Set to server mode.
	 * @return
	 */
	public static EntityFactory server(){
		side = SERVER_SIDE;
		return instance;
	}
	
	/**
	 * Chain method. Creates an entity with properties based on type. Adds additional properties depending on mode (default client). Set
	 * client() or server() to set modes prior to calling chain method.
	 * @param type
	 * @param player
	 * @param x
	 * @param y
	 * @return
	 */
	public Entity createEntity(int type, Player player, float x, float y){
		
		Entity entity = null;
		
		switch(type){
			case Unit.UT_FIGHTER:
				Ship ship = new Ship(player, x, y, 30, 40);
                ship.setType(Unit.UT_FIGHTER);
				
				ship.setParameters(50, 2.5f, 5.5f, 350f);
				ship.createCollisionCircle(24);
				
				Weapon weapon = new Weapon(ship, "Gun", 5, 5, 125, 0.98f, 750, 100, 0, 0);
				weapon.setLocation(16, 27);
				ship.addWeapon(weapon);
				
				weapon = new Weapon(ship, "Gun", 5, 5, 125, 0.98f, 750, 100, 0, 50);
				weapon.setLocation(-16, 27);
				ship.addWeapon(weapon);
				
				player.attachShip(ship);
				
				if(side == CLIENT_SIDE){
					ship.setTexture(Revert.getLoadedTexture("fighter.png"));
					//ship.clientStartNetSim();
					//GameWorldClient.particleSystem.addNewEffectFollower("smoke-trail", ship, ship.getEngineOffset(), true);
				}
				
				entity = ship;
				
				break;
			case Unit.UT_SPACESTATION:
				entity = new Building(player, x, y, 256, 256);
                entity.setType(Unit.UT_SPACESTATION);
				
				if(side == CLIENT_SIDE){
                    entity.setTexture(Revert.getLoadedTexture("spacestation.png"));
				}

                Building spaceStation = (Building)entity;

                spaceStation.createCollisionCircle(50);
                spaceStation.setHealth(1250, 1250);
                spaceStation.addTurret(33, 80);
                spaceStation.addTurret(90 + 20, 80);
                spaceStation.addTurret(180, 80);
                spaceStation.addTurret(270 - 20, 80);
                spaceStation.addTurret(360 - 40, 80);
				
				break;
            case Unit.UT_SATGUN:

                entity = new Building(player, x, y, 16, 16);
                entity.setType(Unit.UT_SATGUN);

                Building satgun = (Building)entity;
                satgun.setHealth(750, 750);
                satgun.createCollisionCircle();
                satgun.addTurret();

                if(side == CLIENT_SIDE){
                    entity.setTexture(Revert.getLoadedTexture("satgun.png"));
                }

                break;
		}
	
		return entity;
		
	}
	
}
