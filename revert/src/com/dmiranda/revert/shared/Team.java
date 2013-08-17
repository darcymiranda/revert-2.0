package com.dmiranda.revert.shared;

public class Team {
	
	public static final int RED = 0;
	public static final int BLUE = 1;
	
	private int team;
	
	public Team(int team){
		this.team = team;
	}
	
	public int getTeamId(){ return team; }

}
