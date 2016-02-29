package com.b3.input;

import com.b3.gui.RenderTester;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by Nishanth on 29/02/2016.
 */
public class PopUpSelectionHander implements InputProcessor {

    private RenderTester rt;

    public PopUpSelectionHander(RenderTester rt) {
        this.rt = rt;
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
        if (rt.getPopupShowing()) {
            System.out.println("CLICKED");
            return true;
        } else
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
