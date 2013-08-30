package com.dmiranda.revert.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dmiranda.revert.Revert;
import com.dmiranda.revert.shared.Entity;
import com.dmiranda.revert.shared.GameWorld;

public class Hud {

    private Revert game;
    private MiniMap minimap;

    public Hud(Revert game){
        this.game = game;
        minimap = new MiniMap(game);
    }

    public void load(){
        minimap.load();
    }

    public void render(SpriteBatch sb){

        sb.setProjectionMatrix(game.getUICamera().combined);
        sb.begin();

        minimap.renderUI(sb);

        Entity[] entities = GameWorld.entityManager.getEntities();
        for(int i = 0; i < entities.length; i++){
            if(entities[i] == null) continue;
            minimap.render(sb, entities[i]);
        }

        // Debug text
        Revert.tFont.draw(sb, "Latency: " + game.getClient().getLatency(), 10, Revert.sFont.getXHeight());
        Revert.tFont.draw(sb, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, Revert.sFont.getXHeight() * 2);

        if(game.world.getLocalPlayerFast() != null && game.world.getLocalPlayerFast().ship != null){
            Revert.tFont.draw(sb, "Team: " + (game.world.getLocalPlayerFast().team == 0 ? "Red" : "Blue"), 10, Revert.sFont.getXHeight() *4);
            Revert.tFont.draw(sb, "Position: " + Math.round(game.world.getLocalPlayerFast().ship.getPosition().x) + " : "
                    + Math.round(game.world.getLocalPlayerFast().ship.getPosition().y), 10, Revert.sFont.getXHeight() * 6);
        }

        sb.end();
    }

    public MiniMap getMiniMap(){ return minimap; }

    /*
        healthTexture = Revert.getLoadedTexture("health.png");

        float barLength = getWidth() * (getHealth() / getMaxHealth());

        sb.draw(healthTexture, position.x, position.y + getHeight() + 3, barLength, 2);
     */

}
