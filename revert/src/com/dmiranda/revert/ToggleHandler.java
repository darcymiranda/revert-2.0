package com.dmiranda.revert;

import com.badlogic.gdx.InputAdapter;

public class ToggleHandler extends InputAdapter {

    private Revert game;

    public ToggleHandler(Revert game){
        this.game = game;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        game.getCamera().zoomAdjust(amount * 0.05f);
        return true;
    }
}
