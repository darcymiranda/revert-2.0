package com.dmiranda.revert.shared;

public class EntityActionState {
	
	public static final int STATE_ON = 1;
	public static final int STATE_OFF = 0;
	
	private float stateTime;
	private int currentState;
	
	public EntityActionState(){
	}
	
	public void update(float delta){
		stateTime += delta * 1000;
	}
	
	public void changeState(int state){
		if(state != currentState){
			stateTime = 0;
			currentState = state;
		}
	}
	
	public float getStateTime(){ return stateTime; }
	public int getCurrentState(){ return currentState; }
	
}
