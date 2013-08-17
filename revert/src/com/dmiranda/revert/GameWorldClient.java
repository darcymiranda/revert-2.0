package com.dmiranda.revert;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.shared.Asteroid;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.GameWorld;
import com.dmiranda.revert.shared.Player;
import com.dmiranda.revert.shared.Ship;
import com.dmiranda.revert.shared.Unit;
import com.dmiranda.revert.shared.bullet.Bullet;

public class GameWorldClient extends GameWorld {
	
	public Revert game;
	public static ParticleSystem particleSystem;
	
	private EntityRenderer entityRenderer;
	
	private int tickTime;
	
	public GameWorldClient(Revert game){
		super();
		
		this.game = game;

		entityRenderer = new EntityRenderer(this);
	}
	
	public void create(){
		
		map.loadGraphics();
		entityRenderer.loadGraphics();
		
		particleSystem = new ParticleSystem();
		
	}
	
	public void clientCreateAsteroid(int type, float x, float y, float r){
		
		Asteroid asteroid = new Asteroid(type, x, y, r);
		entityManager.addLocalEntity(asteroid);
		
	}
	
	public void clientCreateShip(Player player, int id, float x, float y){
		
		Ship ship = new Ship(player, x, y, 32, 32);
		ship.clientStartNetSim();
		player.attachShip(ship);
		
		particleSystem.addNewEffectFollower("ship_engine2", ship, ship.getEngineOffset(), true);
		
		if(player.local){
			ship.getClientNetSim().setOnlySync(true);
			game.getCamera().focusEntity(ship);
			game.getMinimap().setCenterOnShip(ship);
		}
		
		entityManager.addEntity(ship, id);
		
	}
	
	@Override
	public void entityDeath(Entity entity){
		
		if(entity instanceof Unit){
			particleSystem.addNewEffect("expo1", entity.getId(), entity.getCenterX(), entity.getCenterY());
		}
		
	}
	
	@Override
	public void bulletCollision(Bullet bullet){
		
		ParticleEffect effect = particleSystem.getCachedEffect("hit");
		ParticleEmitter emitter = effect.getEmitters().first();
		emitter.getLife().setHigh(350);
		emitter.setPosition(bullet.getCenterX() + (bullet.getVelocity().x * Gdx.graphics.getDeltaTime()),
						    bullet.getCenterY() + (bullet.getVelocity().y * Gdx.graphics.getDeltaTime()));
		emitter.getAngle().setHigh(bullet.getRotation() - 70, bullet.getRotation() - 110);
		emitter.getAngle().setLow(bullet.getRotation() - 70, bullet.getRotation() - 110);
		
		particleSystem.addNewEffect(effect, "hit", bullet.getId());
		
	}

	@Override
	public void update(float delta) {
		
		entityManager.update(delta);
		
		if(localPlayer != null && localPlayer.ship != null && localPlayer.ship.isAlive()){
		
			Ship ship = localPlayer.ship;
			
			if(tickTime < 1){
					
				Network.SingleUnitUpdate updater = new Network.SingleUnitUpdate();
				updater.playerid = ship.getOwnerPlayer().id;
				updater.id = ship.getId();
				updater.rt = ship.getRotateTo();
				updater.x = ship.getPosition().x;
				updater.y = ship.getPosition().y;
				updater.xv = ship.getVelocity().x;
				updater.yv = ship.getVelocity().y;
				updater.w = ship.getW();
				updater.a = ship.getA();
				updater.s = ship.getS();
				updater.d = ship.getD();
				updater.shooting = ship.isShooting();
				
				game.getClient().getRawClient().sendUDP(updater);
				
			}
		}
		
		if(tickTime < 1){
			tickTime = Network.CLIENT_SEND_INTERVAL;
		}
		else{
			tickTime--;
		}
		
	}
	
	public void render(SpriteBatch sb, Camera camera){
		
		sb.begin();
		{
			map.render(sb, 0, camera);
			
			Entity[] localEntities = entityManager.getLocalEntities();
			for(int i = 0; i < localEntities.length; i++){
				if(localEntities[i] == null) continue;
				
				entityRenderer.render(sb, localEntities[i]);
			}
			
			particleSystem.render(sb, Gdx.graphics.getDeltaTime());

			Entity[] entities = entityManager.getEntities();
			for(int i = 0; i < entities.length; i++){
				if(entities[i] == null) continue;
				
				entityRenderer.render(sb, entities[i]);
				
			}
			
		}
		sb.end();
		
	}
	
	public void forceNextNetSend(){ tickTime = -1; }

}
