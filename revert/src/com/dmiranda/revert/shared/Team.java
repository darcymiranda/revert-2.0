package com.dmiranda.revert.shared;

import com.badlogic.gdx.math.Vector2;

public class Team {
	
	public static final int RED = 0;
	public static final int BLUE = 1;

    private Vector2 spawnPoint;
	private int team;
	
	public Team(int team){
		this.team = team;
	}
	
	public int getTeamId(){ return team; }
}
