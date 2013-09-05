package com.dmiranda.revert;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * User: dmiranda
 * Date: 9/4/13
 * Time: 7:20 PM
 */
public class ToggleHandler implements InputProcessor {

    private Revert game;

    public ToggleHandler(Revert game){
        this.game = game;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
