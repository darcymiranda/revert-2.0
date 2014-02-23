package com.dmiranda.revert.shared;

import com.badlogic.gdx.math.Vector2;


public class Player {

    public static final int RED_TEAM = 0, BLUE_TEAM = 1;
	
	public String username;
	public boolean local;
	public int id;
	public int team; // 0 red, 1 blue
	
	public Ship ship;
	
	private Vector2 spawnPoint;
    private float respawnTimer;
	
	public Player(){
		this("unknown", 0);
	}
	
	public Player(String username, int team){
		this.username = username;
	}
	
	public void attachShip(Ship ship){
		//if(ship != null){ System.err.println("Attached a ship to a player that already had a ship"); }
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
		if(player == null) return false;
		return this.team != player.team;
	}

    public float getRespawnTimer() {
        return respawnTimer;
    }

    public void setRespawnTimer(float respawnTimer) {
        this.respawnTimer = respawnTimer;
    }

    public void reduceSpawnTimer(float value){
        respawnTimer -= value;
    }

    public Vector2 getSpawnPoint() {
        return spawnPoint;
    }

    public boolean isDead(){
        return ship == null || !ship.isAlive();
    }

}

