package com.dmiranda.revert;

import java.util.HashMap;

import com.badlogic.gdx.Application;
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
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.dmiranda.revert.client.RevertClient;
import com.dmiranda.revert.effects.Effect;
import com.dmiranda.revert.network.Network;
import com.dmiranda.revert.shared.*;
import com.dmiranda.revert.ui.Hud;
import com.dmiranda.revert.ui.MainMenu;

public class Revert implements ApplicationListener {

	public static AssetManager assets = new AssetManager();
	public static HashMap<String, Animation> animations = new HashMap<String, Animation>();
    public static BitmapFont lFont, sFont, tFont, titleFont;

    public GameWorldClient world;

    private MainMenu menuScreen;

    private Camera gameCamera;
	private OrthographicCamera uiCamera;
	private SpriteBatch sb;
    private Hud hud;
	private ShapeRenderer debugRenderer;
    private boolean debugMode;
	private RevertClient client;
    private ToggleHandler toggleHandler;

    private StateMachine stateMachine;
	
	private String tempHostName;
	
	@Override
	public void create() {
		
		Network.clientSide = true;
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

        toggleHandler = new ToggleHandler(this);

		initLoad();

		uiCamera = new OrthographicCamera();
		uiCamera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		uiCamera.position.x = Gdx.graphics.getWidth() * 0.5f;
		uiCamera.position.y = Gdx.graphics.getHeight() * 0.5f;
		
		sb = new SpriteBatch();
		gameCamera = new Camera();
		world = new GameWorldClient(this);
        hud = new Hud(this);

		debugRenderer = new ShapeRenderer();

		menuScreen = new MainMenu(this);
		
		try{tempHostName = java.net.InetAddress.getLocalHost().getHostName();}catch(Exception e){}


        /*
        Setup states
         */
        stateMachine = new StateMachine(this);
        stateMachine.addState("menu", new StateMachine.State(){
            @Override
            public void begin() {
            }

            @Override
            public void render() {
                menuScreen.render();
            }

            @Override
            public void end() {
            }
        });
        stateMachine.addState("load", new StateMachine.State(){
            @Override
            public void begin() {
            }

            @Override
            public void render() {

                menuScreen.render();

                if(assets.update()){
                    Gdx.app.log("Assets", "loaded " + assets.getLoadedAssets() + " assets");
                    stateMachine.setCurrentState("network");
                }

                sb.begin();
                String loadText = "Loading " + Math.round(assets.getProgress()*100) + "%";
                sFont.draw(sb, loadText, Gdx.graphics.getWidth() * 0.5f - loadText.length() * 4, Gdx.graphics.getHeight() * 0.75f);
                sb.end();
            }

            @Override
            public void end() {
                animations.put("fighter-engine", new Animation(64,Revert.getLoadedTexture("fighter_engine.png").split(16, 5)[0]));

                world.create();

            }
        });
        stateMachine.addState("network", new StateMachine.State() {
            @Override
            public void begin() {
                client = new RevertClient(stateMachine.getGame());
                client.connect(Network.DEFAULT_HOST, Network.PORT_TCP, Network.PORT_TCP);
            }

            @Override
            public void render() {

                menuScreen.render();

                sb.begin();
                sFont.draw(sb, client.getStatus(), Gdx.graphics.getWidth() * 0.5f - client.getStatus().length() * 4, Gdx.graphics.getHeight() * 0.75f);
                sb.end();

                if(client.isConnected()){
                    stateMachine.setCurrentState("play");
                }

            }

            @Override
            public void end() {
                menuScreen.dispose();
            }
        });
        stateMachine.addState("play", new StateMachine.State() {
            @Override
            public void begin() {
                hud.load();
                Gdx.input.setInputProcessor(toggleHandler);
                client.sendHandShake(tempHostName, Team.BLUE);
            }

            @Override
            public void render() {

                doInputs();

                world.update(Gdx.graphics.getDeltaTime());
                world.render(sb, gameCamera);

                if(debugMode) {
                    Entity[] entities = GameWorld.entityManager.getEntities();
                    Entity[] localEntities = GameWorld.entityManager.getLocalEntities();
                    debugRenderer.setProjectionMatrix(gameCamera.combined);
                    debugRenderer.setColor(Color.WHITE);
                    debugRenderer.begin(ShapeRenderer.ShapeType.Line);
                    {
                        for (int i = 0; i < entities.length; i++) {
                            if (entities[i] == null) continue;

                            debugRenderer.circle(entities[i].getCollisionCircle().getShape().x,
                                    entities[i].getCollisionCircle().getShape().y,
                                    entities[i].getCollisionCircle().getShape().radius);

                        }
                    }
                    debugRenderer.end();
                }

                hud.render(sb);
            }

            @Override
            public void end() {
            }
        });
        stateMachine.setCurrentState("menu");
	}
	
	private void initLoad(){
		
		lFont = new BitmapFont(Gdx.files.internal("assets/data/fonts/mlarge.fnt"), Gdx.files.internal("assets/data/fonts/mlarge.png"), true);
		sFont = new BitmapFont(Gdx.files.internal("assets/data/fonts/msmall.fnt"), Gdx.files.internal("assets/data/fonts/msmall.png"), true);
		tFont = new BitmapFont(Gdx.files.internal("assets/data/fonts/mtiny.fnt"), Gdx.files.internal("assets/data/fonts/mtiny.png"), true);
        titleFont = new BitmapFont(Gdx.files.internal("assets/data/fonts/title.fnt"), Gdx.files.internal("assets/data/fonts/title.png"), true);

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

        stateMachine.render();
		
	}
	
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

                localShip.moveUp(Gdx.input.isKeyPressed(Input.Keys.W));
                localShip.moveLeft(Gdx.input.isKeyPressed(Input.Keys.A));
                localShip.moveDown(Gdx.input.isKeyPressed(Input.Keys.S));
                localShip.moveRight(Gdx.input.isKeyPressed(Input.Keys.D));
                boolean shoot = Gdx.input.isButtonPressed(Buttons.LEFT);

                if(localShip.isShooting() != shoot){
                    world.forceNextNetSend();
                }

                localShip.setShooting(Gdx.input.isButtonPressed(Buttons.LEFT));

                //debug
                if(Gdx.input.isKeyPressed(Input.Keys.GRAVE)){
                    debugMode = !debugMode;
                }
                if(Gdx.input.isKeyPressed(Input.Keys.M)){
                    if(localShip != null){
                        localShip.die(null);
                    }
                }
                if(Gdx.input.isKeyPressed(Input.Keys.T)){
                    Effect effect = new Effect(mouse.x, mouse.y, 16, 16, true);
                    effect.addParticleEffect(Revert.getLoadedParticleEffect("expo1"), false);
                    effect.getParticleEffect().allowCompletion();
                }
            }
		}
	}
	
	private void loadAssets(){
		
		FileHandle imageDirHandle = Gdx.files.internal("./assets/textures/");
        FileHandle particleDirHandle = Gdx.files.internal("./assets/data/particles/");
		
		for(FileHandle handle : imageDirHandle.list())
            assets.load(handle.path(), Texture.class);

        for(FileHandle handle : particleDirHandle.list()){
            if(handle.file().getName().toCharArray()[0] == 112) continue; // ignore files that startEffect with p
            assets.load(handle.path(), ParticleEffect.class);
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

    public static ParticleEffect getLoadedParticleEffect(String name){
        final String prefix = "./assets/data/particles/";

        if(!assets.isLoaded(prefix + name, ParticleEffect.class)){
            Gdx.app.error("Assets", "NOT FOUND - " + prefix + name);
            return null;
        }

        return new ParticleEffect(assets.get(prefix + name, ParticleEffect.class));
    }


	@Override
	public void resize(int width, int height) {
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
    public StateMachine getStateMachine(){ return stateMachine; }
    public Hud getHud(){ return hud; }
    public boolean getDebugMode(){ return debugMode; }
	
}
