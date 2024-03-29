package com.dmiranda.revert.client;

import com.badlogic.gdx.Gdx;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.network.Network.Connect;
import com.dmiranda.revert.network.Network.Disconnect;
import com.dmiranda.revert.network.Network.EntityDeath;
import com.dmiranda.revert.network.Network.EntitySpawnSelf;
import com.dmiranda.revert.network.Network.MapProperties;
import com.dmiranda.revert.network.Network.UnitUpdate;
import com.dmiranda.revert.network.properties.PAsteroid;
import com.dmiranda.revert.network.properties.PUnit;
import com.dmiranda.revert.shared.Asteroid;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.EntityFactory;
import com.dmiranda.revert.shared.GameWorld;
import com.dmiranda.revert.shared.Player;
import com.dmiranda.revert.shared.Ship;
import com.dmiranda.revert.shared.Unit;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

public class ClientIncoming extends Listener {
	
	private Revert game;
	
	public ClientIncoming(Revert revert){
		this.game = revert;
	}
	
	@Override
	public void received(Connection connection, Object object){
		super.received(connection, object);
		
		//Gdx.app.debug("[R:" + object.getClass().getSimpleName() + "]", object.toString());
		
		if(object instanceof UnitUpdate){
			UnitUpdate updater = (UnitUpdate)object;
			
			//Gdx.app.debug("[UnitUpdate]" + updater.properties.size(), "Length: " + updater.properties.size());
			
			for(int i = 0; i < updater.properties.size(); i++){
				
				PUnit pUnit = updater.properties.get(i);
				
				Entity entity = game.world.getEntityManager().getEntityById(pUnit.id);
				if(entity != null){
					
					if(entity instanceof Unit){

                        if(entity instanceof Ship){

                            Ship ship = (Ship)entity;
                            ship.moveUp(pUnit.w);
                            ship.moveLeft(pUnit.a);
                            ship.moveRight(pUnit.d);
                            ship.moveDown(pUnit.s);
                        }
						
						Unit unit = (Unit)entity;
						
						if(unit.getClientNetSim() != null){
							unit.getClientNetSim().input(game.getClient().getLatency(), pUnit.x, pUnit.y, pUnit.xv, pUnit.yv);
						}
						else{
							unit.setPosition(pUnit.x, pUnit.y);
							unit.setVelocity(pUnit.xv, pUnit.yv);
						}

						unit.setShooting(pUnit.shooting);
						unit.rotateTo(pUnit.rt);
						unit.setHealth(pUnit.health);
						
					}
					
				}
				else{
					
					Player player = game.world.getPlayers().get(pUnit.playerid);
					
					if(player != null){
						
						Entity newEntity = EntityFactory.client().createEntity(pUnit.type, player, pUnit.x, pUnit.y);
						if(newEntity != null){
							
							Gdx.app.debug("Network [UnitUpdate]", "Created entity " + pUnit.id + " for " + player);
							
							GameWorld.entityManager.addEntity(newEntity, pUnit.id);
						}
					}
				}
			}
		}
        else if(object instanceof FrameworkMessage.Ping){
            FrameworkMessage.Ping ping = (FrameworkMessage.Ping)object;

            if(ping.isReply){
                game.getClient().setLatency(connection.getReturnTripTime());
                game.getClient().getRawClient().updateReturnTripTime();
            }
        }
		else if(object instanceof MapProperties){
			MapProperties prop = (MapProperties)object;

			if(prop.asteroidProperties != null){
				
				for(int i = 0; i < prop.asteroidProperties.size(); i++){
					
					PAsteroid pAsteroid = prop.asteroidProperties.get(i);
					Asteroid asteroid = new Asteroid(pAsteroid.type, pAsteroid.x, pAsteroid.y, pAsteroid.r);
					game.world.getEntityManager().addEntity(asteroid, pAsteroid.id);
					
					Gdx.app.debug("Network [MapProperties]", "created asteroid of type " + pAsteroid.type);
				}
			}
		}
		else if(object instanceof EntityDeath){
			EntityDeath spawn = (EntityDeath)object;
			
			Entity dead = game.world.getEntityManager().getEntityById(spawn.id);
			Entity killer = game.world.getEntityManager().getEntityById(spawn.killerid);

            if(killer == null){
                Gdx.app.error("Network [Death]", "Received null for entity who did the killing" + spawn.killerid);
            }

            if(dead != null){
                Gdx.app.debug("Network [Death]", killer + " killed " + dead);
                dead.die(killer);
            } else {
                Gdx.app.error("Network [Death]", "Received null locally for dead entity with id " + spawn.id);
            }
			
		}
		else if(object instanceof EntitySpawnSelf){
			EntitySpawnSelf spawn = (EntitySpawnSelf)object;

			Ship unit = (Ship) EntityFactory.client().createEntity(spawn.type, game.world.getLocalPlayer(), spawn.x, spawn.y);
			
			unit.getClientNetSim().setOnlySync(true);
			game.getCamera().focusEntity(unit);
			game.getHud().getMiniMap().setCenterOnShip(unit);
			
			Gdx.app.debug("Network [SpawnSelf]", "self created unit(" + spawn.id + ")");
			
			GameWorld.entityManager.addEntity(unit, spawn.id);
			
		}
		else if(object instanceof Connect){
			Connect connect = (Connect)object;
			
			boolean localPlayer = false;
			
			if(connect.id == game.getClient().getSessionId()){
				game.getClient().setHandshakeStatus(true);
				localPlayer = true;
				GameWorld.serverSeed = connect.seed;
			}

			Player player = game.world.createPlayer(connect.id, connect.team, connect.username, localPlayer);
			Gdx.app.debug("Network [Connect]", "Player created -  " + player);
			
		}
		else if(object instanceof Disconnect){
			Disconnect disconnect = (Disconnect)object;
			
			Player player = game.world.getPlayers().get(disconnect.id);
			
			Gdx.app.debug("Network [Disconnect]", "player " + player + " disconnected. killed ship(" + player.ship+ ")");
            if(player.ship != null){
			    player.ship.die(null);
            }
			game.world.getPlayers().remove(disconnect.id);
			
		}
		
	}
	
	@Override
	public void connected(Connection connection){
		super.connected(connection);

        game.getClient().getRawClient().updateReturnTripTime();
		game.getClient().setSessionId(connection.getID());

	}
	
	@Override
	public void disconnected(Connection connection){
		super.disconnected(connection);
		
		game.getClient().setHandshakeStatus(false);
		
	}

}
