package com.dmiranda.revert.client;

import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.EntityState;
import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.shared.Entity;

public class NetSimulateState {
	
	private EntityState previous, render, simulate;
	private Entity entity;
	private Vector2 serverPosition;
	private Vector2 serverVelocity;
	private float smoothing;
	private float latency;
	private boolean onlySync;
	private float delta;
	
	public NetSimulateState(Entity entity){
		this.entity = entity;
		previous = new EntityState(entity);
		render = new EntityState(entity);
		simulate = new EntityState(entity);
		serverPosition = new Vector2(entity.getPosition());
		serverVelocity = new Vector2(entity.getVelocity());
	}
	
	public void input(float latency, float x, float y, float vx, float vy){

        this.latency = latency;
		
		smoothing = 1;

		serverPosition.x = x + (vx * latency * 0.001f);
		serverPosition.y = y + (vy * latency * 0.001f);
		serverVelocity.x = vx;
		serverVelocity.y = vy;
		
		previous.setState(simulate);

		simulate.setPosition(serverPosition.x, serverPosition.y);
		simulate.setVelocity(vx, vy);

		simulate.update(delta);

	}

	public void update(float delta){
		
		smoothing -= 1f / Network.CLIENT_SEND_INTERVAL;
		if(smoothing < 0 ) smoothing = 0;

        render.velocity = new Vector2(entity.getVelocity()).lerp(simulate.velocity, smoothing);
        render.position = new Vector2(entity.getPosition()).lerp(simulate.position, smoothing);

        float distance = render.getPosition().dst2(serverPosition);
        if(distance > 15000 + (latency * 100) || distance < -15000 - (latency * 100) ){
            entity.setPosition(render.position);
            entity.setVelocity(render.velocity);
        }

        if(!onlySync){
            entity.setVelocity(render.velocity);
        }

        this.delta = delta;
		
	}
	
	public Vector2 getRawServerPosition(){ return new Vector2(serverPosition); }
	public Vector2 getSimulatedPosition(){ return new Vector2(render.position); }
	
	public void setOnlySync(boolean sync){ this.onlySync = sync; }

}
