package com.dmiranda.revert.shared;

import com.badlogic.gdx.math.Vector2;


public class Player {
	
	public String username;
	public boolean local;
	public int id;
	public int team; // 0 red, 1 blue
	
	public Ship ship;
	
	private Vector2 spawnPoint;
	
	public Player(){
		this("unknown", 0);
	}
	
	public Player(String username, int team){
		this.username = username;
	}
	
	public void attachShip(Ship ship){
		if(ship != null){ System.err.println("Attached a ship to a player that already had a ship"); }
		this.ship = ship;
		ship.setOwnerPlayer(this);
	}
	
	public void setSpawnPoint(float x, float y){
		spawnPoint = new Vector2(x, y);
	}
	
	public String toString(){
		return "{Name: " + username + ", ID: " + id + ", Local?: " + local + "}";
	}
	
	public boolean isAllies(Player player){
		return !isEnemies(player);
	}
	
	public boolean isEnemies(Player player){
		return this.team != player.team;
	}
	
	public Vector2 getSpawnPoint(){ return spawnPoint; }

}
