package com.dmiranda.revert.shared;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Entity {
	
	protected Vector2 position;
	protected Vector2 velocity; 
	protected int width, height;
	protected float rotation;
	
	protected Entity ownerEntity;
	protected Player ownerPlayer;
	
	protected EntityActionState actionState;
	
	private Texture texture;
	
	private CollisionCircle collisionCircle;
	private boolean net = true;
	private boolean alive = true;
	private int id = -1;
	
	private Vector2 oldPosition;
	
	public Entity(float x, float y, int width, int height){
		velocity = new Vector2(0, 0);
		position = new Vector2(x, y);
		oldPosition = new Vector2(x, y);
		this.width = width;
		this.height = height;

		actionState = new EntityActionState();
	}
	
	public void update(float delta){
		
		oldPosition.x = position.x;
		oldPosition.y = position.y;
		
		float vx = velocity.x * delta;
		float vy = velocity.y * delta;
		
		position.x += vx;
		position.y += vy;
		
		if(rotation > 360) rotation -= 360;
		else if(rotation < 0) rotation += 360;
		
		if(collisionCircle != null){
			
			Vector2 futurePosition = new Vector2(position.x, position.y);
			futurePosition.x += vx + width / 2;
			futurePosition.y += vy + height / 2;
			
			collisionCircle.update(futurePosition, rotation);
		}
		
		actionState.update(delta);
	}
	
	public void render(SpriteBatch sb){
		if(texture != null){
			sb.draw(texture,
					getPosition().x,
					getPosition().y, 
					getWidth() / 2,
					getHeight() / 2,
					getWidth(),
					getHeight(),
					1, 1,
					getRotation(),
					0, 0,
					(int)getWidth(),
					(int)getHeight(),
					false, false);
		}
	}
	
	public void createCollisionCircle(){
		createCollisionCircle(width / 2);
	}
	
	public void createCollisionCircle(float radius){
		collisionCircle = new CollisionCircle(getCenterX(), getCenterY(), radius);
	}
	
	public boolean isAllieTo(Entity otherEntity){
		return !isEnemyTo(otherEntity);
	}
	
	public boolean isEnemyTo(Entity otherEntity){
		
		Player otherPlayer = otherEntity.getOwnerPlayer();
		Player thisPlayer = getOwnerPlayer();
		
		if(thisPlayer == null || otherPlayer == null) return true;
		
		return thisPlayer.isEnemies(otherPlayer);
	}
	
	public Player getOwnerPlayer(){
		return (ownerEntity == null) ? ownerPlayer : ownerEntity.getOwnerPlayer();
	}
	
	public void kill(Entity killer){
		onDeath(killer);
		alive = false;
	}
	
	protected void onHit(Entity hitter){}
	protected void onDeath(Entity killer){}

	public void setTexture(Texture texture){ this.texture = texture; }
	public void setNetworkEnabled(boolean net){ this.net = net; }
	public void setId(int id){ this.id = id; }
	public void setOwnerEntity(Entity entity){ this.ownerEntity = entity; }
	public void setOwnerPlayer(Player player){ this.ownerPlayer = player; }
	public void setPosition(Vector2 position){ this.position = new Vector2(position); }
	public void setPosition(float x, float y){ this.position = new Vector2(x, y); }
	public void setVelocity(Vector2 velocity){ this.velocity = new Vector2(velocity); }
	public void setVelocity(float x, float y){ this.velocity = new Vector2(x, y); }
	public void setRotation(float r){ this.rotation = r; }
	
	public boolean isNetworkEnabled(){ return net; }
	public Vector2 getVelocity(){ return velocity; }
	public Vector2 getPosition(){ return position; }
	public Vector2 getCenterPosition(){ return new Vector2(getCenterX(), getCenterY()); }
	public float getCenterX(){ return position.x + width / 2; }
	public float getCenterY(){ return position.y + height / 2; }
	public float getRotation(){ return rotation; }
	public float getWidth(){ return width; }
	public float getHeight(){ return height; }
	public Entity getOwnerEntity(){ return ownerEntity; }
	public CollisionCircle getCollisionCircle(){ return collisionCircle; }
	public int getId(){ return id; }
	
	public EntityActionState getEntityActionState(){ return actionState; }
	
	public boolean isAlive(){ return alive; }
	public boolean hasMoved(){ return oldPosition.x != position.x && oldPosition.y != position.y; }
	
	public String toString(){
		return "{Entity " + this.getClass().getSimpleName() + " (" + id + ")}";
	}

	
}
