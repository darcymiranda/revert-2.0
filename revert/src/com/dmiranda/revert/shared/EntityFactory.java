package com.dmiranda.revert.shared;

import com.dmiranda.revert.GameWorldClient;
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
				Ship ship = new Ship(player, x, y, 32, 32);
				
				ship.setParameters(50, 2.5f, 5.5f, 350f);
				
				Weapon weapon = new Weapon(ship, "Gun", 5, 5, 125, 0.98f, 500, 100, 0, 800);
				weapon.setLocation(45, 10);
				ship.addWeapon(weapon);
				
				weapon = new Weapon(ship, "Gun", 5, 5, 125, 0.98f, 500, 100, 0, 800);
				weapon.setLocation(-45, 10);
				ship.addWeapon(weapon);
				
				player.attachShip(ship);
				
				if(side == CLIENT_SIDE){
					// TODO: Reimplement the way we render
					ship.setTexture(null);
					ship.clientStartNetSim();
					GameWorldClient.particleSystem.addNewEffectFollower("ship_engine2", ship, ship.getEngineOffset(), true);
				}
				
				entity = ship;
				
				break;
		}
		
		return entity;
		
	}
	
}
