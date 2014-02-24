package com.dmiranda.revert.client;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.network.Network;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;

public class RevertClient {
	
	private Client client;
	private Revert game;
	private boolean handShakeStatus;
    private int session;

    //private final int ROLLING_LATENCY_BUFFER_SIZE = 15;
    //private ArrayList<Integer> rollingLatency = new ArrayList<Integer>();
	private int latency;

	private String status = "";
	
	public RevertClient(Revert game){

		this.game = game;
		
		client = new Client(2*1024, 2*1024);
		client.start();
		Network.register(client);
		
		ThreadedListener listener = new ThreadedListener(new ClientIncoming(game));
		client.addListener(listener);
		
	}
	
	public String getStatus(){
		return status;
	}
	
	public void connect(final String host, final int portTcp, final int portUdp){
		
		status = "Connecting to " + Network.DEFAULT_HOST + " : " + Network.PORT_TCP;

        new Thread(){
            public void run(){
                try {
                    client.connect(5000, host, portTcp, portTcp);
                } catch (IOException e) {
                    status = e.getMessage();
                }
            }
        }.start();
		
	}

    public void sendHandShake(String name, int team){

        if(!isConnected()){
            Gdx.app.log("Client", "Tried to handshake without being connected");
            return;
        }

        Network.Connect connect = new Network.Connect();
        connect.username = name;
        connect.team = 0;
        client.sendTCP(connect);
    }
	
	public void setSessionId(int session){
		this.session = session;
		Gdx.app.log("Network", "Session #" + session);
	}
	
	public boolean isConnected(){
		return client.isConnected(); 
	}
	
	public void setHandshakeStatus(boolean handShakeStatus){
		this.handShakeStatus = handShakeStatus;
	}
	
	public boolean isHandshakeComplete(){ return handShakeStatus; }
	public int getSessionId(){ return session; }
	public Client getRawClient(){ return client; }
	
	public int getLatency(){ return latency; }
	public void setLatency(int latency){

        this.latency = latency;

        /*
        rollingLatency.add(latency);
        if(rollingLatency.size() > ROLLING_LATENCY_BUFFER_SIZE){
            rollingLatency.remove(rollingLatency.size() - 1);
        }

        int average = 0;
        for(Integer l : rollingLatency){
            average += l.intValue();
            System.out.println(l.intValue());
        }
        average /= rollingLatency.size();

        this.latency = average;
        */
    }

}
