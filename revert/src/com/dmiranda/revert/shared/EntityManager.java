package com.dmiranda.revert.shared;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.effects.Effect;
import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.shared.bullet.Bullet;


public class EntityManager {
	
	private Entity[] entities = new Entity[1024*4];
	private Entity[] localEntities = new Entity[1024*4];
	public final Entity[] getEntities() { return entities; }
	public final Entity[] getLocalEntities(){ return localEntities; }
	
	private GameWorld world;
	
	public EntityManager(GameWorld world){
		this.world = world;
	}
	
	private void doCollision(Entity entity, float delta){
	
		for(int i = 0; i < entities.length; i++){
			if(entities[i] == null) continue;
			if(entities[i] == entity) continue;
			if(entities[i].isAllieTo(entity)) continue;
			if(entities[i].getCollisionCircle() == null) continue;
			
			Entity otherEntity = entities[i];
			
			if(entity.getCollisionCircle().intersects(otherEntity.getCollisionCircle())){
				
				world.entityCollision(entity, otherEntity);
                entity.onHit(otherEntity);
                otherEntity.onHit(entity);

                CollisionCircle cc1 = entity.getCollisionCircle();
                CollisionCircle cc2 = otherEntity.getCollisionCircle();
                Circle c1 = cc1.getShape();
                Circle c2 = cc2.getShape();

                /*
                Vector2 collision = new Vector2(
                        c1.x - c2.x,
                        c1.y - c2.y
                );

                float distance = collision.len2();
                if(distance == 0){
                    collision = new Vector2(1, 0);
                    distance = 1;
                }
                if(distance > 1){
                    continue;
                }

                collision = collision.div(distance);
                System.out.println(collision);
                float aci = entity.getVelocity().dot(collision);
                float bci = otherEntity.getVelocity().dot(collision);

                float acf = bci;
                float bcf = aci;

                entity.velocity.x += (acf - aci) * collision.x;
                entity.velocity.y += (bcf - bci) * collision.y;
                */

                float xDist = c1.x - c2.x;
                float yDist = c1.y - c2.y;
                float distSquared = xDist * xDist + yDist * yDist;

                float vx = otherEntity.velocity.x - entity.velocity.x;
                float vy = otherEntity.velocity.y - entity.velocity.y;
                float dp = xDist * vx + yDist * vy;

                if(dp > 0){

                    float cScale = dp / distSquared;
                    float cx = xDist * cScale;
                    float cy = yDist * cScale;

                    float combinedMass = entity.getCollisionMass() + otherEntity.getCollisionMass();
                    float c1Weight = 2 * otherEntity.getCollisionMass() / combinedMass;
                    float c2Weight = 2 * entity.getCollisionMass() / combinedMass;

                    entity.velocity.x = c1Weight * cx * 0.25f;
                    entity.velocity.y = c1Weight * cy * 0.25f;
                    otherEntity.velocity.x = -(c2Weight * cx * 0.25f);
                    otherEntity.velocity.y = -(c2Weight * cy * 0.25f);

                    entity.position.x += entity.velocity.x * delta;
                    entity.position.y += entity.velocity.y * delta;
                    otherEntity.position.x -= otherEntity.velocity.x * delta;
                    otherEntity.position.y -= otherEntity.velocity.y * delta;
                }
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
                entity.onDeath();
				localEntities[i] = null;

				continue;
				
			}

			entity.update(delta);
            world.entityUpdate(entity);
			
		}

        // Now check collisions
        for(int i = 0; i < localEntities.length; i++){
            if(localEntities[i] == null) continue;

            Entity entity = localEntities[i];

            if(entity instanceof Bullet){

                Bullet bullet = (Bullet) entity;

                if(GameWorld.map.collideEndMapBullet(bullet)){
                    entity.onDeath();
                    localEntities[i] = null;
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
                    doCollision(entity, delta);
                }

            }
        }
		
		
		/**
		 * Entities
		 */
		for(int i = 0; i < entities.length; i++){
			if(entities[i] == null) continue;
			
			Entity entity = entities[i];
		
			if(!entity.isAlive()){
				
				world.entityDeath(entity);
                entity.onDeath();
				entities[i] = null;
				
				continue;
				
			}

			entity.update(delta);
            world.entityUpdate(entity);
			
		}

        // Now check collisions
        for(int i = 0; i < entities.length; i++){
            if(entities[i] == null) continue;

            Entity entity = entities[i];

            GameWorld.map.collideEndMapEntity(entity);

            if(entity.getCollisionCircle() != null){
                doCollision(entity, delta);
            }
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
