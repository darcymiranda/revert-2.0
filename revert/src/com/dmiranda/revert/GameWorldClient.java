package com.dmiranda.revert;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.shared.Asteroid;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.GameWorld;
import com.dmiranda.revert.shared.Ship;
import com.dmiranda.revert.shared.Unit;
import com.dmiranda.revert.shared.bullet.Bullet;

public class GameWorldClient extends GameWorld {
	
	public Revert game;
	public static ParticleSystem particleSystem;
	
	private EntityRenderer entityRenderer;
	private BackgroundEffect backgroundEffect;
    private TextureRegion background;
	
	private int tickTime;
	
	public GameWorldClient(Revert game){
		super();
		
		this.game = game;

		entityRenderer = new EntityRenderer(this);
		backgroundEffect = new BackgroundEffect(game.getCamera());
	}
	
	public void create(){

        background = Revert.getLoadedTexture("bg-stars.png");
		entityRenderer.loadGraphics();
		backgroundEffect.init();
		
		particleSystem = new ParticleSystem();
		
	}
	
	public void clientCreateAsteroid(int type, float x, float y, float r){
		
		Asteroid asteroid = new Asteroid(type, x, y, r);
		entityManager.addLocalEntity(asteroid);
		
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

        // Draw the background
        sb.disableBlending();
        sb.begin();
        sb.setProjectionMatrix(camera.calculateParallaxMatrix(0.1f, 0.1f));
        sb.draw(background, -game.getCamera().getOrtho().viewportWidth / 2, -game.getCamera().getOrtho().viewportHeight / 2);
        sb.end();

        // Draw everything else
        sb.enableBlending();
        sb.setProjectionMatrix(game.getCamera().getOrtho().combined);
        sb.begin();

        //backgroundEffect.render(Gdx.graphics.getDeltaTime(), sb);

		particleSystem.render(sb, Gdx.graphics.getDeltaTime());

		Entity[] entities = entityManager.getEntities();
		for(int i = 0; i < entities.length; i++){
			if(entities[i] == null) continue;
			
			entities[i].render(sb);
			
		}
		
		Entity[] localEntities = entityManager.getLocalEntities();
		for(int i = 0; i < localEntities.length; i++){
			if(localEntities[i] == null) continue;
			
			localEntities[i].render(sb);
		}

        sb.end();
	}
	
	public void forceNextNetSend(){ tickTime = -1; }

}
