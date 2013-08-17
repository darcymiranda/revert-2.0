package com.dmiranda.revert.shared;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.shared.bullet.Bullet;


public class EntityManager {
	
	private Entity[] entities = new Entity[1024];
	private Entity[] localEntities = new Bullet[2048];
	public final Entity[] getEntities() { return entities; }
	public final Entity[] getLocalEntities(){ return localEntities; }
	
	private GameWorld world;
	
	public EntityManager(GameWorld world){
		this.world = world;
	}
	
	private void doCollision(Entity entity){
	
		for(int i = 0; i < entities.length; i++){
			if(entities[i] == null) continue;
			if(entities[i] == entity) continue;
			if(entities[i].isAllieTo(entity)) continue;
			if(entities[i].getCollisionCircle() == null) continue;
			
			Entity otherEntity = entities[i];
			
			if(entity.getCollisionCircle().intersects(otherEntity.getCollisionCircle())){
				
				world.entityCollision(entity, otherEntity);
				
				Vector2 collision = new Vector2(
						entity.getCenterX() - otherEntity.getCenterX(),
						entity.getCenterY() - otherEntity.getCenterY());
				
				
				float distance = collision.len();
				
				if(distance == 0){
					collision = new Vector2(1, 0);
					distance = 1;
				}
			
				collision.x /= distance;
				collision.y /= distance;
				
				Vector2 vel = new Vector2(entity.getVelocity());
				Vector2 otherVel = new Vector2(otherEntity.getVelocity());
				
				float aci = vel.dot(collision);
				float bci = otherVel.dot(collision);
				
				float acf = bci;
				float bcf = aci;
				
				entity.velocity.x = (acf - aci) * collision.x / 2;
				entity.velocity.y = (acf - aci) * collision.y / 2;
				otherEntity.velocity.x = (bcf - bci) * collision.x / 2;
				otherEntity.velocity.y = (bcf - bci) * collision.y / 2;
				
				entity.onHit(otherEntity);
				otherEntity.onHit(entity);
				
			}
		}
	
	}
	
	public void update(float delta){
		
		/**
		 * Local Entities
		 */
		for(int i = 0; i < localEntities.length; i++){
			if(localEntities[i] == null) continue;
			
			Entity entity = localEntities[i];
			
			
			if(!entity.isAlive()){
				
				world.entityDeath(entity);
				
				localEntities[entity.getId()] = null;
				
				continue;
				
			}
			
			if(entity instanceof Bullet){
				
				Bullet bullet = (Bullet) entity;
			
				if(GameWorld.map.collideEndMapBullet(bullet)){
					continue;
				}
			
				for(int j = 0; j < entities.length; j++){
					if(entities[j] == null) continue;
					if(entities[j].isAllieTo(bullet)) continue;
					if(entities[j].getCollisionCircle() == null) continue;
					
					Entity otherEntity = entities[j];
					
					if(bullet.getCollisionCircle().intersects(otherEntity.getCollisionCircle())){
						
						world.bulletCollision(bullet);
						
						otherEntity.onHit(bullet);
						bullet.onHit(otherEntity);
						
					}
				}
			}
			else{
				
				if(entity.getCollisionCircle() != null){
					doCollision(entity);
				}
				
			}
			
			world.entityUpdate(entity);
			entity.update(delta);
			
		}
		
		
		/**
		 * Entities
		 */
		for(int i = 0; i < entities.length; i++){
			if(entities[i] == null) continue;
			
			Entity entity = entities[i];
		
			if(!entity.isAlive()){
				
				world.entityDeath(entity);
				
				entities[entity.getId()] = null;
				
				continue;
				
			}
			
			GameWorld.map.collideEndMapEntity(entity);
			
			if(entity instanceof Ship){
				
				Ship ship = (Ship)entity;
				
				if(ship.isShooting()){
					
					ArrayList<Bullet[]> allBullets = ship.shoot();
					for(int j = 0; j < allBullets.size(); j++){
						
						Bullet[] bullets = allBullets.get(j);
						for(int k = 0; k < bullets.length; k++){
							
							addLocalEntity(bullets[k]);
							
						}
					}
				}
			}
			
			if(entity instanceof SpaceStation){
				SpaceStation spaceStation = (SpaceStation)entity;
				ArrayList<Turret> turrets = spaceStation.getTurrets();
				for(int z = 0; z < turrets.size(); z++){
					Turret turret = turrets.get(z);
					
					if(turret.isShooting()){
						
						ArrayList<Bullet[]> allBullets = turret.shoot();
						for(int j = 0; j < allBullets.size(); j++){
							
							Bullet[] bullets = allBullets.get(j);
							for(int k = 0; k < bullets.length; k++){
								
								addLocalEntity(bullets[k]);
								
							}
						}
						
					}
					
				}
			}
			
			if(entity.getCollisionCircle() != null){
				doCollision(entity);
			}
			
			world.entityUpdate(entity);
			entity.update(delta);
			
		}
		
	}

	public Entity getNearestEntity(Entity entity, Class<? extends Entity> type, float maxDistance, boolean lookForEnemy){
		
		float distance = maxDistance;
		Entity closestEntity = null;
		for(int i = 0; i < entities.length; i++){
			if(entities[i] == null) continue;
			if(entities[i].getId() == entity.getId()) continue;
			if(lookForEnemy && entity.isAllieTo(entities[i])) continue;
			if(entities[i].getClass().equals(type) && entities[i].isAlive()){
				
			
				float curDistance = entity.getPosition().dst(entities[i].getPosition());

				if(curDistance < distance){
					closestEntity = entities[i];
					distance = curDistance;
				}
			}
		}

		return closestEntity;
	}
	
	public Entity getEntityById(int id){
		if(id == -1){ 
			Gdx.app.error("EntityManager.getEntityById()", "Tried to get an entity that was not assigned an ID");
			return null;
		}
		return entities[id];
	}
	
	public void removeEntityById(int id){
		if(id < 0 || id > entities.length) return;
		entities[id] = null;
	}
	
	public int addEntity(Entity entity){
		
		for(int i = 0; i < entities.length; i++){
			if(entities[i] == null){
				entity.setId(i);
				entity.setNetworkEnabled(true);
				entities[i] = entity;
				return i;
			}
		}
		return -1;
	}
	
	public void addEntity(Entity entity, int id){
		
		if(entities[id] != null){
			System.err.println("overlapped entity on id " + id);
		}
		
		entity.setNetworkEnabled(true);
		entity.setId(id);
		entities[id] = entity;
		
	}
	
	public int getAvailableEntityId(){
		for(int i = 0; i < entities.length; i++){
			if(entities[i] == null){
				return i;
			}
		}
		System.err.println("No available ids in entity pool");
		return -1;
	}
	
	public int addLocalEntity(Entity entity){
		for(int i = 0; i < localEntities.length; i++){
			if(localEntities[i] == null){
				entity.setId(i);
				entity.setNetworkEnabled(false);
				localEntities[i] = entity;
				return i;
			}
		}
		return -1;
	}
	
	public Entity getLocalEntityById(int id){
		return localEntities[id];
	}
	
	public void removeLocalEntityById(int id){
		if(id < 0 || id > localEntities.length) return;
		localEntities[id] = null;
	}
	
}
