package com.dmiranda.revert.network;

import java.util.ArrayList;

import com.dmiranda.revert.network.properties.PAsteroid;
import com.dmiranda.revert.network.properties.PUnit;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {

	public static final int CLIENT_SEND_INTERVAL = 6;
	public static final String DEFAULT_HOST = "50.72.157.82";
	public static final int PORT_TCP = 19555;
	public static final boolean RUN_WITH_SERVER = true;
    public static final int SIM_LAG_MIN = 300, SIM_LAG_MAX = 500;
	
	public static boolean clientSide;
	
	public static void register(EndPoint endPoint) {
		
		Kryo kyro = endPoint.getKryo();
		kyro.register(ArrayList.class);
		kyro.register(PAsteroid.class);
		kyro.register(PUnit.class);
		kyro.register(MapProperties.class);
		kyro.register(UnitUpdate.class);
		kyro.register(UnitShoot.class);
		kyro.register(SingleUnitUpdate.class);
		kyro.register(EntityDeath.class);
		kyro.register(EntitySpawnSelf.class);
		kyro.register(Connect.class);
		kyro.register(Disconnect.class);
		
	}
	
	public static class PingTest {
		public boolean reply;
	}
	
	public static class UnitShoot {
		public int id;
		public boolean shooting;
	}
	
	public static class MapProperties {
		public ArrayList<PAsteroid> asteroidProperties;
	}
	
	public static class UnitUpdate {
		public ArrayList<PUnit> properties;
		public UnitUpdate(){ properties = new ArrayList<PUnit>(); }
	}
	
	public static class SingleUnitUpdate {
		public int id, playerid, type;
        public int latency;
		public float x,y,xv,yv,r,rt;
		public float health;
		public boolean w,s,d,a;
		public boolean shooting;
	}
	
	public static class EntitySpawnSelf {
		public int id, type;
		public float x, y;
	}
	
	public static class EntityDeath {
		public int id, killerid, playerid;
	}
	
	public static class Connect {
		public int id, team;
		public long seed;
		public String username;
	}
	
	public static class Disconnect {
		public int id;
	}

	
}
