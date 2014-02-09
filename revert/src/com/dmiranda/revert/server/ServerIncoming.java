package com.dmiranda.revert.server;

import java.util.ArrayList;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.dmiranda.revert.network.Network.Connect;
import com.dmiranda.revert.network.Network.Disconnect;
import com.dmiranda.revert.network.Network.EntitySpawnSelf;
import com.dmiranda.revert.network.Network.MapProperties;
import com.dmiranda.revert.network.Network.PingTest;
import com.dmiranda.revert.network.Network.SingleUnitUpdate;
import com.dmiranda.revert.network.properties.PAsteroid;
import com.dmiranda.revert.server.RevertServer.UserConnection;
import com.dmiranda.revert.shared.Asteroid;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.EntityFactory;
import com.dmiranda.revert.shared.GameWorld;
import com.dmiranda.revert.shared.Player;
import com.dmiranda.revert.shared.Unit;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;

public class ServerIncoming extends Listener {
	
	private RevertServer game;
	
	public ServerIncoming(RevertServer server){
		this.game = server;
	}
	
	@Override
	public void received(Connection currentConnection, Object object){
		super.received(currentConnection, object);
		
		Player currentPlayer = game.world.getPlayers().get(currentConnection.getID());
		if(currentPlayer == null){
			
			if(object instanceof Connect){
				
				System.out.println("Connected: Session #" + currentConnection.getID());
				
				currentPlayer = game.world.createPlayer(currentConnection.getID(), currentConnection.getID() % 2, ((Connect)object).username, false);
				
				if(currentPlayer.team == 0)
					currentPlayer.setSpawnPoint(372, 372);
				else if(currentPlayer.team == 1)
					currentPlayer.setSpawnPoint(2188, 2188);
				
				Connect connect = new Connect();
				connect.id = currentPlayer.id;
				connect.username = currentPlayer.username;
				connect.team = currentPlayer.team;
				connect.seed = GameWorld.serverSeed;
				game.server.sendToAllTCP(connect);
				
				float x = MathUtils.random(currentPlayer.getSpawnPoint().x + 256, currentPlayer.getSpawnPoint().x + 256 + 100);
				float y = MathUtils.random(currentPlayer.getSpawnPoint().y + 256, currentPlayer.getSpawnPoint().y + 256 + 100);
				
				Entity spawnedEntity = EntityFactory.server().createEntity(Unit.UT_FIGHTER, currentPlayer, x, y);
				GameWorld.entityManager.addEntity(spawnedEntity);
				
				EntitySpawnSelf spawn = new EntitySpawnSelf();
				spawn.id = spawnedEntity.getId();
				spawn.x = x;		
				spawn.y = y;
				spawn.type = Unit.UT_FIGHTER;
				game.server.sendToTCP(currentConnection.getID(), spawn);
				
				MapProperties props = new MapProperties();
				props.asteroidProperties = new ArrayList<PAsteroid>();
				
				for(int i = 0; i < game.world.getEntityManager().getEntities().length; i++){
					Entity entity = game.world.getEntityManager().getEntities()[i];
					if(entity == null) continue;
					if(entity instanceof Asteroid){
						Asteroid asteroid = (Asteroid)entity;
						PAsteroid pAsteroid = new PAsteroid();
						pAsteroid.id = asteroid.getId();
						pAsteroid.x = asteroid.getPosition().x;
						pAsteroid.y = asteroid.getPosition().y;
						pAsteroid.r = asteroid.getRotation();
						pAsteroid.type = asteroid.getAsteroidType();
						props.asteroidProperties.add(pAsteroid);
					}
				}
				
				game.server.sendToTCP(currentConnection.getID(), props);
				
				// Send data of all the other clients to sync the new one
				Connection[] connections = game.server.getConnections();
				for(int i = 0; i < connections.length; i ++){
					if(connections[i].getID() == currentConnection.getID()) continue;
					
					Player player = game.world.getPlayers().get(connections[i].getID());
					if(player == null){
						continue;
					}
					
					Connect connectAll = new Connect();
					connectAll.id = player.id;
					connectAll.username = player.username;
					connectAll.team = player.team;
					game.server.sendToTCP(currentPlayer.id, connectAll);
					
				}
			}
		}
		else if(object instanceof SingleUnitUpdate){
			SingleUnitUpdate updater = (SingleUnitUpdate)object;
			
			if(currentPlayer.ship != null){

                float diff = updater.latency * 0.001f;

				currentPlayer.ship.setShooting(updater.shooting);
				currentPlayer.ship.rotateTo(updater.rt);
				currentPlayer.ship.moveUp(updater.w);
				currentPlayer.ship.moveLeft(updater.a);
				currentPlayer.ship.moveRight(updater.d);
				currentPlayer.ship.moveDown(updater.s);

                Vector2 newPosition = new Vector2(updater.x + (updater.xv * diff), updater.y + (updater.yv * diff));

                float distance = currentPlayer.ship.getPosition().dst2(newPosition.x, newPosition.y);
                if(distance > 15000 + (updater.latency * 100) || distance < -15000 - (updater.latency * 100) ){

                    System.err.println(currentPlayer + "  " + currentPlayer.ship + " moved too quickly of " + distance + " units.");

                }

                currentPlayer.ship.setPosition(updater.x + (updater.xv * diff), updater.y + (updater.yv * diff));
                currentPlayer.ship.setVelocity(updater.xv, updater.yv);

			}
		}
	}
	
	@Override
	public void connected(Connection connection){
		super.connected(connection);
	}
	
	@Override
	public void disconnected(Connection connection){
		super.disconnected(connection);
		
		if(game.world.getPlayers().containsKey(connection.getID())){
			
			Player player = game.world.getPlayers().get(connection.getID());
			
			game.world.getPlayers().remove(connection.getID());
			
			if(player.ship != null){
				game.world.getEntityManager().removeEntityById(player.ship.getId());
			}
			// TODO: Remove all entities related to the player.
			
			
			System.out.println("Disconnected: Session #" + connection.getID());
		}
		
		Disconnect disconnect = new Disconnect();
		disconnect.id = connection.getID();
		game.server.sendToAllTCP(disconnect);
	}

}
