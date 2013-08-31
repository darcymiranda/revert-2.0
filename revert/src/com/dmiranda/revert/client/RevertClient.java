package com.dmiranda.revert.client;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.network.Network;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener.LagListener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;

public class RevertClient {
	
	private Client client;
	private Revert game;
	private boolean handShakeStatus;
	private boolean connecting;
    private int session;
	private long latency;

	private String status = "";
	
	public RevertClient(Revert game){
		
		this.game = game;
		
		client = new Client(2*1024, 2*1024);
		client.start();
		Network.register(client);
		
		ThreadedListener listener = new ThreadedListener(new ClientIncoming(game));
		client.addListener(listener);
		//LagListener lagListener = new LagListener(30, 300, listener);
		//client.addListener(lagListener);
		
	}
	
	public String getStatus(){
		return status;
	}
	
	public void connect(String host, int portTcp, int portUdp, String username){
		
		status = "Connecting to " + Network.DEFAULT_HOST + " : " + Network.PORT_TCP;
		
		try {

            connecting = true;
			client.connect(5000, host, portTcp, portUdp);
			
		} catch (IOException e) {
			
			status = e.getMessage();
            connecting = false;
		}
		
		Network.Connect connect = new Network.Connect();
		connect.username = username;
		connect.team = 0;
		client.sendTCP(connect);
		
	}
	
	public void setSessionId(int session){
		this.session = session;
		Gdx.app.log("Network", "Session #" + session);
	}
	
	public boolean isConnect(){ 
		return client.isConnected(); 
	}

    public boolean isConnecting(){
        return connecting;
    }
	
	public void setHandshakeStatus(boolean handShakeStatus){
		this.handShakeStatus = handShakeStatus;
	}
	
	public boolean isHandshakeComplete(){ return handShakeStatus; }
	public int getSessionId(){ return session; }
	public Client getRawClient(){ return client; }
	
	public long getLatency(){ return latency; }
	public void setLatency(long latency){ this.latency = latency; }

}
