package com.dmiranda.revert.shared;

import java.util.HashMap;
import java.util.Iterator;

import com.dmiranda.revert.shared.bullet.Bullet;

public abstract class GameWorld {

	public static final int COMPUTER_RED = -100, COMPUTER_BLUE = -200;
	
	public static long serverSeed = System.currentTimeMillis();
	public static GameMap map;
	public static EntityManager entityManager;
	
	protected Player localPlayer;
	protected HashMap<Integer, Player> players = new HashMap<Integer, Player>();

	public GameWorld(){
		
		entityManager = new EntityManager(this);
		map = new GameMap(4096, 4096);
		
		createPlayer(COMPUTER_RED, Player.RED_TEAM, "Red Team", false);
		createPlayer(COMPUTER_BLUE, Player.BLUE_TEAM, "Blue Team", false);
		
	}
	
	public abstract void update(float delta);
	
	public void entityUpdate(Entity entity){}
	public void entityDeath(Entity entity){}
	public void entityCollision(Entity entity, Entity otherEntity){}
	public void bulletCollision(Bullet bullet){}
	
	public Player getLocalPlayer(){
		
		Iterator<Player> it = players.values().iterator();
		while(it.hasNext()){
			Player player = it.next();
			if(player.local){
				return player;
			}
		}
		
		return null;
	}
	
	public Player createPlayer(int id, int team, String username, boolean local){
		
		Player player = new Player();
		
		if(local){
			localPlayer = player;
			player.local = true;
		}
		
		player.id = id;
		player.username = username.trim();
		player.team = team;
		players.put(id, player);
		
		return player;
	}
	
	public HashMap<Integer, Player> getPlayers(){
		return players;
	}
	
	public Player getLocalPlayerFast(){ return localPlayer; }
	
	public EntityManager getEntityManager(){ return entityManager; }

}
