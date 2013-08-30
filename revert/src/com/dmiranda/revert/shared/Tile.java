package com.dmiranda.revert.shared;

import com.badlogic.gdx.graphics.Texture;

public class Tile {
	
	public static int TYPE_GROUND = 0;
	public static int TYPE_WALL = 1;
	
	public int layer;
	public int type;
	public Texture texture;
	
	public Tile(){
		
	}
	
	public void setSolid(){
		
	}
	
	public boolean isBlocked(){
		return type == 1;
	}

}
