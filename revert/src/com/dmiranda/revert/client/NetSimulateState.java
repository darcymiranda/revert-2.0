package com.dmiranda.revert.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.EntityState;
import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.tools.Tools;

public class NetSimulateState {
	
	private EntityState previous, render, simulate;
	private Entity entity;
	private Vector2 serverPosition;
	private Vector2 serverVelocity;
	private float smoothing;
	private float lag;
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
	
	public void input(float lag, float x, float y, float vx, float vy){
		
		this.lag = lag;
		
		smoothing = 1;
		
		serverPosition.x = x;
		serverPosition.y = y;
		serverVelocity.x = vx;
		serverVelocity.y = vy;
		
		previous.setState(simulate);
		simulate.setPosition(x, y);
		simulate.setVelocity(vx, vy);
		
		previous.update(delta);
		simulate.update(delta);
		
		entity.getPosition().lerp(serverPosition, lag);
		entity.getVelocity().lerp(serverVelocity, lag);
		
		float distance = entity.getPosition().dst2(serverPosition);
		if(distance > 5000 || distance < -5000){
		
			//Gdx.app.log("Simulation", "Telported " + entity + " because it was " + distance + " behind from server.");
			entity.setPosition(serverPosition.x, serverPosition.y);
			entity.setVelocity(serverVelocity.x, serverVelocity.y);
		
		}
		
	}

	public void update(float delta){
		
		this.delta = delta;
		
		smoothing -= 1f / Network.CLIENT_SEND_INTERVAL;
		if(smoothing < 0 ) smoothing = 0;
		
		render.velocity = simulate.velocity;
		
		// interpolate
		//render.position = new Vector2(previous.position.lerp(simulate.position, smoothing));
		render.velocity = new Vector2(previous.velocity.lerp(simulate.velocity, smoothing));
		
		
			
		if(!onlySync){
			
			entity.setVelocity(render.velocity);
			
			//System.err.println(previous + "           " + simulate);
			//System.out.println(render + "    " + entity.getPosition() + " - " + entity.getVelocity());
		
			//entity.getPosition().lerp(render.position, smoothing);

			
			// set new positions
			//entity.setPosition(render.position);
			//entity.setVelocity(render.velocity);
			//entity.setRotation(render.rotation);
		
		}
		
	}
	
	public Vector2 getRawServerPosition(){ return new Vector2(serverPosition); }
	public Vector2 getSimulatedPosition(){ return new Vector2(render.position); }
	
	public void setOnlySync(boolean sync){ this.onlySync = sync; }

}
