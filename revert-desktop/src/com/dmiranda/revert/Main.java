package com.dmiranda.revert;

import java.io.IOException;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.dmiranda.revert.server.RevertServer;

public class Main {
	
	public static void main(String[] args) throws IOException {
		
		if(args.length > 0){
			if(args[0].equals("-server")){
				new RevertServer();
				return;
			}
		}
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Revert";
		cfg.useGL20 = true;
		cfg.width = 1280;
		cfg.height = 800;
		cfg.resizable = false;
		
		LwjglApplicationConfiguration.disableAudio = true;
		
		new LwjglApplication(new Revert(), cfg);
			
		
	}
}
