package com.dmiranda.revert.shared;

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
                ship.setCollisionMass(50);
				
				Weapon weapon = new Weapon(ship, "Gun", 5, 5, 125, 0.98f, 750, 100, 0, 0);
				weapon.setLocation(16, 27);
				ship.addWeapon(weapon);
				
				weapon = new Weapon(ship, "Gun", 5, 5, 125, 0.98f, 750, 100, 0, 50);
				weapon.setLocation(-16, 27);
				ship.addWeapon(weapon);
				
				player.attachShip(ship);
				
				if(side == CLIENT_SIDE){
					ship.setTexture(Revert.getLoadedTexture("fighter.png"));
				}
				
				entity = ship;
				
				break;
			case Unit.UT_SPACESTATION:
                Building spaceStation = new Building(player, x, y, 256, 256);
                spaceStation.setType(Unit.UT_SPACESTATION);
                spaceStation.setCollisionMass(500000);

                spaceStation.createCollisionCircle(50);
                spaceStation.setHealth(1250, 1250);
                spaceStation.addTurret(33, 80);
                spaceStation.addTurret(90 + 20, 80);
                spaceStation.addTurret(180, 80);
                spaceStation.addTurret(270 - 20, 80);
                spaceStation.addTurret(360 - 40, 80);

                if(side == CLIENT_SIDE){
                    spaceStation.setTexture(Revert.getLoadedTexture("spacestation.png"));
                }

                entity = spaceStation;
				
				break;
            case Unit.UT_SATGUN:

                Building satgun = new Building(player, x, y, 16, 16);
                satgun.setType(Unit.UT_SATGUN);
                satgun.setCollisionMass(500);

                satgun.setHealth(750, 750);
                satgun.createCollisionCircle();
                satgun.addTurret();

                if(side == CLIENT_SIDE){
                    satgun.setTexture(Revert.getLoadedTexture("satgun.png"));
                }

                entity = satgun;

                break;
		}
	
		return entity;
		
	}
	
}
