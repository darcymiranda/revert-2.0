package com.dmiranda.revert.server;

import java.io.IOException;
import java.util.Iterator;

import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.shared.Player;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

public class RevertServer extends Thread {

    public GameWorldServer world;
    public Server server;

    private final int TARGET_FPS = 60;
    private final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;
    private long lastUpdateTime = System.nanoTime();

    public RevertServer() throws IOException {

        server = new Server(2*1024, 2*1024);

        Network.register(server);

        world = new GameWorldServer(this);

        Listener serverListener = new ServerIncoming(this);
        server.addListener(serverListener);

        server.bind(Network.PORT_TCP, Network.PORT_TCP);
        server.start();

        this.start();

        System.out.println("Server started on port " + Network.PORT_TCP);

    }

    public void sendTcpToAllValidatedPlayers(Object message){
        for(int i = 0; i < server.getConnections().length; i++){
            if(world.getPlayers().containsKey(server.getConnections())){
                server.getConnections()[i].sendTCP(message);
            }
        }
    }

    public void sendUdpToAllValidatedPlayers(Object message){
        for(int i = 0; i < server.getConnections().length; i++){
            if(world.getPlayers().containsKey(server.getConnections())){
                server.getConnections()[i].sendUDP(message);
            }
        }
    }

    public void run(){

		while(true){
			
			long now = System.nanoTime();
			long updateLength = now - lastUpdateTime;
			lastUpdateTime = now;
			
			float delta = updateLength / (float)OPTIMAL_TIME / 60;
			
			world.update(delta);

			try { Thread.sleep( ((lastUpdateTime - System.nanoTime() + OPTIMAL_TIME) / 1000000) );}catch(Exception ex){};
			
		}
    }

    public static void main(String args[]) throws IOException{
        Log.set(Log.LEVEL_TRACE);
        new RevertServer();

    }

}
