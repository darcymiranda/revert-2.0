package com.dmiranda.revert.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.Revert.GAME_STATES;

public class MainMenu {
	
	private Stage stage;
	
	private Skin playButtonSkin, quitButtonSkin;
	private ImageButton playButton, quitButton;
	private TextureAtlas atlas;
	
	public MainMenu(final Revert game){
		
		stage = new Stage();
		
		atlas = new TextureAtlas(Gdx.files.internal("./assets/data/skins/gui-assets.txt"));
		
		playButtonSkin = new Skin(Gdx.files.internal("./assets/data/skins/button-play.json"), atlas);
		quitButtonSkin = new Skin(Gdx.files.internal("./assets/data/skins/button-quit.json"), atlas);
		
		playButton = new ImageButton(playButtonSkin);
		playButton.setPosition(Gdx.graphics.getWidth() * 0.5f - (110 * 0.5f), Gdx.graphics.getHeight() * 0.75f);
		playButton.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent e, float x, float y){
				
				game.setGameState(Revert.GAME_STATES.LOAD_ASSETS);
				stage.clear();
				stage.dispose();
				
			}
			
		});
		stage.addActor(playButton);
		
		quitButton = new ImageButton(quitButtonSkin);
		quitButton.setPosition(Gdx.graphics.getWidth() * 0.5f - (110 * 0.5f), Gdx.graphics.getHeight() * 0.75f - ((42 * 2) * 0.5f));
		quitButton.addListener(new ClickListener(){
			
			@Override
			public void clicked(InputEvent e, float x, float y){
				
				Gdx.app.exit();
				
			}
			
		});
		stage.addActor(quitButton);
		
		Gdx.input.setInputProcessor(stage);
		
		
	}

	public void render() {
		
		stage.act();
		stage.draw();
		
	}
	
	public void dispose(){
		playButtonSkin.dispose();
		quitButtonSkin.dispose();
		atlas.dispose();
	}

}
