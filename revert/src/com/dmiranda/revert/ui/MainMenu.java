package com.dmiranda.revert.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.dmiranda.revert.Camera;
import com.dmiranda.revert.Revert;

public class MainMenu {

	private Stage stage;
    private Camera camera;

	private Skin playButtonSkin, quitButtonSkin;
	private ImageButton playButton, quitButton;
	private TextureAtlas atlas;
    private Image background;
    private Sprite fadeOut;
    private final String TITLE = "REVERT";

    private boolean scrollDirection = false;

	public MainMenu(final Revert game){

        fadeOut = new Sprite();
        fadeOut.setTexture(new Texture(Gdx.files.internal("assets/textures/health.png")));
        fadeOut.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        fadeOut.setColor(Color.BLACK);

        camera = new Camera();
        camera.translate(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
        camera.zoom = 1;

        TextureRegion tr = new TextureRegion(new Texture(Gdx.files.internal("assets/textures/bg-stars.png")));
        background = new Image();
		background.setDrawable(new TextureRegionDrawable(tr));
        background.setBounds(0, 0, tr.getRegionWidth(), tr.getRegionHeight());

        stage = new Stage();

		atlas = new TextureAtlas(Gdx.files.internal("assets/data/skins/gui-assets.txt"));

		playButtonSkin = new Skin(Gdx.files.internal("assets/data/skins/button-play.json"), atlas);
		quitButtonSkin = new Skin(Gdx.files.internal("assets/data/skins/button-quit.json"), atlas);

		playButton = new ImageButton(playButtonSkin);
		playButton.setPosition(Gdx.graphics.getWidth() * 0.15f, Gdx.graphics.getHeight() * 0.3f);
		playButton.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent e, float x, float y){

                playButton.addAction(Actions.moveTo(-400, playButton.getY(), 1f, Interpolation.pow4));
                quitButton.addAction(Actions.moveTo(-400, quitButton.getY(), 1f, Interpolation.pow4));

                game.getStateMachine().setCurrentState("load");
                //game.setGameState(Revert.GAME_STATES.LOAD_ASSETS);

            }

		});
		stage.addActor(playButton);

		quitButton = new ImageButton(quitButtonSkin);
		quitButton.setPosition(Gdx.graphics.getWidth() * 0.15f, Gdx.graphics.getHeight() * 0.3f - ((42 * 2) * 1f));
		quitButton.addListener(new ClickListener(){

			@Override
			public void clicked(InputEvent e, float x, float y){
				Gdx.app.exit();
			}

		});
		stage.addActor(quitButton);

		Gdx.input.setInputProcessor(stage);

	}
    private void cameraBounce(){

        if(camera.getModifiedPosition().x > Gdx.graphics.getWidth()){
            scrollDirection = false;
            camera.position.x -= 1;
        }
        else if(camera.position.x < 0){
            camera.position.x += 1;
            scrollDirection = true;
        }

        if(scrollDirection)
            camera.position.x+=1;
        else
            camera.position.x-=1;

    }

	public void render(){

        cameraBounce();

        SpriteBatch sb = stage.getSpriteBatch();

        sb.setProjectionMatrix(camera.calculateParallaxMatrix(0.2f, 0.2f));
        sb.disableBlending();
        sb.begin();

        background.setPosition(-camera.viewportWidth * 0.5f, -camera.viewportHeight * 0.5f);
        background.draw(sb, 1f);
        sb.end();
        sb.enableBlending();


        sb.setProjectionMatrix(new Camera().combined);
        sb.begin();
        Revert.titleFont.draw(sb, TITLE, Gdx.graphics.getWidth() * 0.41f, Gdx.graphics.getHeight() * 0.1f);
        sb.end();

		stage.act();
		stage.draw();

        //sb.begin();
        //fadeOut.draw(sb);
        //sb.end();
	}

	public void dispose(){
		atlas.dispose();
        stage.dispose();
	}

}
