package com.dmiranda.revert.shared;

public class Tile {
	
	public static int TYPE_GROUND = 0;
	public static int TYPE_WALL = 1;
	
	public int layer;
	public int image;
	public int type;
	
	public Tile(){
		
	}
	
	public void setSolid(){
		
	}
	
	public boolean isBlocked(){
		return type == 1;
	}

}
