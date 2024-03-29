package com.dmiranda.revert.shared;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	
	protected Vector2 position, oldPosition;
	protected Vector2 velocity, oldVelocity;
	protected int width, height;
	protected float rotation, oldRotation;
    protected float rotationSpeed;
    protected float rotateTo;
	
	protected Entity ownerEntity;
	protected Player ownerPlayer;
	protected EntityActionState actionState;

	protected TextureRegion texture;

    private boolean hide;
	private CollisionCircle collisionCircle;
    private float collisionMass;
	private boolean net = true;
	private boolean alive = true;
	private int type = -1;
	private int id = -1;
    private Entity killer;
	
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
        oldRotation = rotation;
        oldVelocity = velocity;
		
		float vx = velocity.x * delta;
		float vy = velocity.y * delta;
		
		position.x += vx;
		position.y += vy;
		
		if(rotation > 360) rotation -= 360;
		else if(rotation < 0) rotation += 360;

        // Rotate based on the rotation speed
        float d = rotateTo - rotation;
        if(d > 180)
            d -= 360;
        else if(d < -180)
            d += 360;
        rotation += d * rotationSpeed * delta;
		
		if(collisionCircle != null){
			collisionCircle.update(getCenterPosition(), rotation);
		}
		
		actionState.update(delta);
	}
	
	public void render(SpriteBatch sb){
		if(texture != null && !hide){
			sb.draw(texture,
					getPosition().x,
					getPosition().y, 
					getWidth() / 2,
					getHeight() / 2,
					getWidth(),
					getHeight(),
					1, 1,
					getRotation());
		}
	}
	
	public void createCollisionCircle(){
		createCollisionCircle(width * 0.5f);
	}
	
	public void createCollisionCircle(float radius){
		createCollisionCircle(getCenterX(), getCenterY(), radius);
	}
	
	public void createCollisionCircle(float x, float y, float radius){
		collisionCircle = new CollisionCircle(x, y, radius);
	}

    public boolean hasChangedSinceLastTick(){
        return oldPosition.x != position.x && oldPosition.y != position.y && oldRotation != rotation &&
                oldVelocity.x != velocity.x && oldVelocity.y != velocity.y;
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
		return ownerEntity == null ? ownerPlayer : ownerEntity.getOwnerPlayer();
	}

    /**
     * Returns top-most owner of entity else returns this
     * @return
     */
    public Entity getOwnerEntity(){
        return ownerEntity == null ? this : ownerEntity.getOwnerEntity();
    }
	
	public void die(Entity killer){
        this.killer = killer;
		this.alive = false;
	}

	protected void onHit(Entity hitter){}
	protected void onDeath(){}

    public void setRotationSpeed(float rotationSpeed){ this.rotationSpeed = rotationSpeed; }
    public void rotateTo(float rotateTo){
        if(rotateTo > 360) rotateTo -= 360;
        else if(rotateTo < 0) rotateTo += 360;
        this.rotateTo = rotateTo;
    }
    public float getRotateTo(){ return rotateTo; }

    public void hide(){ this.hide = true; }
    public void show(){ this.hide = false; }
	public void setTexture(TextureRegion texture){ this.texture = texture; }
	public void setNetworkEnabled(boolean net){ this.net = net; }
	public void setId(int id){ this.id = id; }
	public void setOwnerEntity(Entity entity){ this.ownerEntity = entity; }
	public void setOwnerPlayer(Player player){ this.ownerPlayer = player; }
	public void setPosition(Vector2 position){ this.position.x = position.x; this.position.y = position.y; }
	public void setPosition(float x, float y){ this.position.x = x; this.position.y = y;}
	public void setVelocity(Vector2 velocity){ this.velocity.x = velocity.x; this.velocity.y = velocity.y; }
	public void setVelocity(float x, float y){ this.velocity.x = x; this.velocity.y = y; }
	public void setRotation(float r){ this.rotation = r; }
	public void setType(int type){ this.type = type; }
    public void setCollisionMass(float mass){ this.collisionMass = mass; }

    public boolean isHidden(){ return hide; }
    public TextureRegion getTexture(){ return texture; }
	public boolean isNetworkEnabled(){ return net; }
	public Vector2 getVelocity(){ return velocity; }
	public Vector2 getPosition(){ return position; }
	public Vector2 getCenterPosition(){ return new Vector2(getCenterX(), getCenterY()); }
	public float getCenterX(){ return position.x + width / 2; }
	public float getCenterY(){ return position.y + height / 2; }
	public float getRotation(){ return rotation; }
	public float getWidth(){ return width; }
	public float getHeight(){ return height; }
	public CollisionCircle getCollisionCircle(){ return collisionCircle; }
	public int getId(){ return id; }
	public int getType(){ return type; }
    public float getCollisionMass(){ return collisionMass; }
	
	public EntityActionState getEntityActionState(){ return actionState; }
	
	public boolean isAlive(){ return alive; }
	public boolean hasMoved(){ return oldPosition.x != position.x && oldPosition.y != position.y; }
	
	public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append(" Entity: { ");
        sb.append(this.getClass().getSimpleName());
        sb.append(" (");
        sb.append(id);
        sb.append(")]");
        sb.append(" Player: ");
        sb.append(getOwnerPlayer());
		return sb.toString();
	}

	
}
