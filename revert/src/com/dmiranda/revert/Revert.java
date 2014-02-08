package com.dmiranda.revert;

import java.util.HashMap;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.dmiranda.revert.client.InputReconciliation;
import com.dmiranda.revert.client.RevertClient;
import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.shared.*;
import com.dmiranda.revert.ui.Hud;
import com.dmiranda.revert.ui.MainMenu;
import com.dmiranda.revert.ui.MiniMap;

public class Revert implements ApplicationListener {

	public static AssetManager assets = new AssetManager();
	public static HashMap<String, Animation> animations = new HashMap<String, Animation>();
    public static BitmapFont lFont, sFont, tFont;

    public GameWorldClient world;

    private MainMenu menuScreen;

    private Camera gameCamera;
	private OrthographicCamera uiCamera;
	private SpriteBatch sb;
    private Hud hud;
	private ShapeRenderer debugRenderer;
	private RevertClient client;
    private ToggleHandler toggleHandler;

	private GAME_STATES currentGameState = null;
	
	public enum GAME_STATES {
		NETWORK,
		LOAD_ASSETS,
		LOAD,
		PLAY,
		PAUSE,
		MENU
	}
	
	private String tempHostName;
	
	@Override
	public void create() {
		
		Network.clientSide = true;
		
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

        toggleHandler = new ToggleHandler(this);

		initLoad();
		
		setGameState(GAME_STATES.MENU);

		uiCamera = new OrthographicCamera();
		uiCamera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiCamera.position.x = Gdx.graphics.getWidth() / 2;
		uiCamera.position.y = Gdx.graphics.getHeight() / 2;
		
		sb = new SpriteBatch();
		gameCamera = new Camera();
		world = new GameWorldClient(this);
        hud = new Hud(this);

		debugRenderer = new ShapeRenderer();
		
		menuScreen = new MainMenu(this);
		
		try{tempHostName = java.net.InetAddress.getLocalHost().getHostName();}catch(Exception e){}
		
		
	}
	
	private void initLoad(){
		
		lFont = new BitmapFont(Gdx.files.internal("./assets/data/fonts/mlarge.fnt"), Gdx.files.internal("./assets/data/fonts/mlarge.png"), true);
		sFont = new BitmapFont(Gdx.files.internal("./assets/data/fonts/msmall.fnt"), Gdx.files.internal("./assets/data/fonts/msmall.png"), true);
		tFont = new BitmapFont(Gdx.files.internal("./assets/data/fonts/mtiny.fnt"), Gdx.files.internal("./assets/data/fonts/mtiny.png"), true);
		
		loadAssets();
		
	}

	@Override
	public void dispose() {
		assets.dispose();
        world.dispose();
	}
	

	@Override
	public void render() {

        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		gameCamera.update();
		uiCamera.update();

		sb.setProjectionMatrix(gameCamera.combined);
		
		if(currentGameState == GAME_STATES.MENU){
			
			menuScreen.render();
			
		}
		else if(currentGameState == GAME_STATES.LOAD_ASSETS){
			
			menuScreen.dispose();
			
			if(assets.update()){
				Gdx.app.log("Assets", "loaded " + assets.getLoadedAssets() + " assets");
				setGameState(GAME_STATES.LOAD);
			}
			
			sb.begin();
			sFont.draw(sb, "Loading " + Math.round(assets.getProgress()*100) + "%", Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() * 0.75f);
			sb.end();
			
		}
		else if(currentGameState == GAME_STATES.LOAD){
			
			animations.put("fighter-engine", new Animation(64,Revert.getLoadedTexture("fighter_engine.png").split(16, 5)[0]));
			animations.put("fighter-engine-light", new Animation(32, Revert.getLoadedTexture("light.png").split(30, 30)[0]));
			
			world.create();
            hud.load();
				
            client = new RevertClient(this);

            sb.begin();
            sFont.draw(sb, "Connecting...", Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() * 0.75f);
            sb.end();

            setGameState(GAME_STATES.NETWORK);
		}
		else if(currentGameState == GAME_STATES.NETWORK){
			
			sb.begin();
			sFont.draw(sb, client.getStatus(), Gdx.graphics.getWidth() / 2 - 100, Gdx.graphics.getHeight() * 0.75f);
			sb.end();

            if(!client.isConnecting()){
                client.connect(Network.DEFAULT_HOST, Network.PORT_TCP, Network.PORT_UDP, tempHostName);
            }
				
			if(client.isHandshakeComplete()){
                Gdx.input.setInputProcessor(toggleHandler);
				setGameState(GAME_STATES.PLAY);
			}
			
		}
		else if(currentGameState == GAME_STATES.PLAY){
			
			doInputs();

            // Update and render world
            world.update(Gdx.graphics.getDeltaTime());
            world.render(sb, gameCamera);

			hud.render(sb);

			// Render debug info
			Entity[] entities = GameWorld.entityManager.getEntities();
			debugRenderer.setProjectionMatrix(gameCamera.combined);
			debugRenderer.setColor(Color.WHITE);
			debugRenderer.begin(ShapeRenderer.ShapeType.Line);
			{
				for(int i = 0; i < entities.length; i++){
					if(entities[i] == null) continue;
					
					if(entities[i] instanceof Unit){
						Unit unit = (Unit)entities[i];
                        if(unit.getClientNetSim() == null) continue;
						
						debugRenderer.circle(unit.getClientNetSim().getRawServerPosition().x + unit.getWidth() / 2,
								unit.getClientNetSim().getRawServerPosition().y + unit.getHeight() / 2,
								entities[i].getCollisionCircle().getShape().radius);
						
					}
										
				}
			}
			debugRenderer.end();
			
			
		}
		
	}


	public void setGameState(GAME_STATES state){
		Gdx.app.log("Game State Change", currentGameState + " to " + state);
		currentGameState = state;
	}

    private InputReconciliation inputReconciliation = new InputReconciliation();
	
	private void doInputs(){
		
		Player localPlayer = world.getLocalPlayerFast();
		
		if(localPlayer != null){
			
			Ship localShip = localPlayer.ship;
			if(localShip != null){
				
				Vector3 mouse = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
				getCamera().unproject(mouse);
				
				float direction = (float) -(Math.atan2(mouse.x - localShip.getCenterX(), 
													  mouse.y - localShip.getCenterY()) * (180 / Math.PI));
                localShip.rotateTo(direction);

                inputReconciliation.addInput(
                        inputReconciliation.new Input(
                                Gdx.input.isKeyPressed(Input.Keys.W),
                                Gdx.input.isKeyPressed(Input.Keys.A),
                                Gdx.input.isKeyPressed(Input.Keys.S),
                                Gdx.input.isKeyPressed(Input.Keys.D),
                                Gdx.input.isButtonPressed(Buttons.LEFT)
                        )
                );

                // temp client side prediction
                localShip.moveUp(Gdx.input.isKeyPressed(Input.Keys.W));
                localShip.moveLeft(Gdx.input.isKeyPressed(Input.Keys.A));
                localShip.moveDown(Gdx.input.isKeyPressed(Input.Keys.S));
                localShip.moveRight(Gdx.input.isKeyPressed(Input.Keys.D));
                localShip.enableBooster(Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT));
                boolean shoot = Gdx.input.isButtonPressed(Buttons.LEFT);

                if(localShip.isShooting() != shoot){
                    world.forceNextNetSend();
                }

                localShip.setShooting(Gdx.input.isButtonPressed(Buttons.LEFT));
            }
		}
	}
	
	private void loadAssets(){
		
		FileHandle dirHandle;
		if(Gdx.app.getType() == ApplicationType.Android){
			dirHandle = Gdx.files.internal("/");
		}
		else{
			dirHandle = Gdx.files.internal("./assets/textures");
		}
		
		for(FileHandle handle : dirHandle.list()){
			assets.load(handle.path(), Texture.class);
		}
		
	}
	
	public static TextureRegion getLoadedTexture(String name){
		final String prefix = "./assets/textures/";
		
		// Default texture to a default texture to prevent crashes
		if(!assets.isLoaded(prefix + name, Texture.class)){
			Gdx.app.error("Assets", "NOT FOUND - " + prefix + name);
			return assets.get(prefix + "default.png");
		}
		
		return new TextureRegion(assets.get(prefix + name, Texture.class));
	}


	@Override
	public void resize(int width, int height) {
		gameCamera.zoom(1);
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
	}

	public Camera getCamera(){ return gameCamera; }
    public OrthographicCamera getUICamera(){ return uiCamera; }
	public RevertClient getClient(){ return client; }
    public Hud getHud(){ return hud; }
	
}
