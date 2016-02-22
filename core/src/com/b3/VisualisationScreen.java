package com.b3;

import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

public class VisualisationScreen implements Screen {

    private MainGame game;

    public VisualisationScreen(MainGame game) {
        this.game = game;
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        // delta time
        float rawDeltaTime = Gdx.graphics.getRawDeltaTime();
        Utils.TRUE_DELTA_TIME = rawDeltaTime;
        Utils.DELTA_TIME = rawDeltaTime * Config.getFloat(ConfigKey.GAME_SPEED);

        // clear screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        // camera movement
        game.camera.move(game.keyboardController);
        game.camera.update();

        // world rendering
        game.world.render();

        // sidebar rendering
        game.sideBar.act();
        game.sideBar.render();

        game.sideBarNodes.act();
        game.sideBarNodes.render();

        if (game.keyboardController.shouldExit())
            Gdx.app.exit();
    }

    @Override
    public void resize(int width, int height) {
        game.sideBarStage.getViewport().update(width, height, true);
        game.sideBarNodes.resize(width, height);

        game.camera.viewportWidth = width;
        game.camera.viewportHeight = height;
        game.camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}
