package com.dmiranda.revert;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.Unit;


public class ParticleSystem {
	
	private HashMap<Entity, ParticleEffectFollower> effectsFollower = new HashMap<Entity, ParticleEffectFollower>();
	private HashMap<String, ParticleEffect> effects = new HashMap<String, ParticleEffect>();
	private HashMap<String, ParticleEffect> cache = new HashMap<String, ParticleEffect>();
	
	public ParticleSystem(){
		
		ParticleEffect pe;

        FileHandle dirHandle = Gdx.files.internal("./assets/data/particles");
        for(FileHandle handle : dirHandle.list()){
            pe = new ParticleEffect();
            pe.load(handle, Gdx.files.internal("./assets/textures"));
            cache.put(handle.name(), pe);
        }
		
		
	}
	
	public void render(SpriteBatch sb, float delta){
	
		Iterator<Entry<String, ParticleEffect>> it = effects.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, ParticleEffect> pairs = it.next();
			String key = pairs.getKey();
			ParticleEffect effect = pairs.getValue();
			
			if(effect.isComplete()){
				it.remove();
				continue;
			}
			
			effect.draw(sb, delta);
		}
		
		Iterator<Entry<Entity, ParticleEffectFollower>> itF = effectsFollower.entrySet().iterator();
		while(itF.hasNext()){
			Map.Entry<Entity, ParticleEffectFollower> pairs = itF.next();
			Entity key = pairs.getKey();
			ParticleEffectFollower effectFollower = pairs.getValue();
			
			if(!key.isAlive()){
				itF.remove();
				continue;
			}
			
			effectFollower.update();
			effectFollower.effect.draw(sb, delta);
			
		}
		
	}
	
	public ParticleEffect getEffectByFollower(Entity entity){
		for(int i = 0; i < effectsFollower.size(); i++){
			ParticleEffectFollower pef = effectsFollower.get(i);
			if(pef.follower == entity){
				return pef.effect;
			}
		}
		return null;
	}
	
	public void addNewEffectFollower(String name, Entity entity){
		addNewEffectFollower(name, entity, new Vector2(), false);
	}
	
	public void addNewEffectFollower(String name, Entity entity, Vector2 offset, boolean matchRotation){
		
		ParticleEffect effect = new ParticleEffect(cache.get(name));
		effectsFollower.put(entity, new ParticleEffectFollower(effect, entity, offset, matchRotation));
		
	}
	
	public ParticleEffect getCachedEffect(String name){
		return new ParticleEffect(cache.get(name));
	}
	
	public void addNewEffect(ParticleEffect effect, String name, int uid){
		effect.start();
		effects.put(name + uid, effect);
	}
	
	public void addNewEffect(String name, int uid, float x, float y){

		ParticleEffect effect = new ParticleEffect(cache.get(name));

		effect.setPosition(x, y);
		effect.start();
		effects.put(name + uid, effect);
	}
	
	public HashMap<String, ParticleEffect> getEffects(){ return effects; }
	public HashMap<Entity, ParticleEffectFollower> getEffectsFollow(){ return effectsFollower; }
	
	public class ParticleEffectFollower {
		
		private ParticleEffect effect;
		private Entity follower;
		private Vector2 offset;
		private boolean matchRotation;
		
		public ParticleEffectFollower(ParticleEffect effect, Entity follower, Vector2 offset, boolean matchRotation){
			this.effect = effect;
			this.follower = follower;
			this.offset = offset;
			this.matchRotation = matchRotation;
		}
		
		public void update(){
			effect.setPosition(follower.getCenterX() + offset.x, follower.getCenterY() + offset.y);
			if(matchRotation){
				for(int i = 0; i < effect.getEmitters().size; i++){
					ParticleEmitter emitter = effect.getEmitters().get(i);
					emitter.getAngle().setHigh(follower.getRotation() + 4);
					emitter.getAngle().setLow(follower.getRotation() - 4);
				}
			}
		}
		
		public ParticleEffect getEffect(){ return effect; }
		
	}

}
