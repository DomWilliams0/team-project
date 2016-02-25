package com.b3;

import com.b3.gui.components.ButtonComponent;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen implements Screen {

    private OrthographicCamera camera;
    private Stage mainMenuStage;

    public MainMenuScreen(MainGame game) {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.mainMenuStage = new Stage(new ScreenViewport());

        game.inputHandler.addProcessor(mainMenuStage);

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(Config.getString(ConfigKey.TEXTURE_ATLAS)));
        Skin skin = new Skin(atlas);
        BitmapFont font = new BitmapFont(
                Gdx.files.internal(Config.getString(ConfigKey.FONT_FILE)),
                Gdx.files.internal(Config.getString(ConfigKey.FONT_IMAGE_FILE)),
                false
        );

        // Learning mode button
        // --------------------
        ButtonComponent learningModeBtn = new ButtonComponent(skin, font, "Learning mode");
        learningModeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new LearningModeScreen(game));
                dispose();
            }
        });

        // Compare mode button
        // -------------------
        ButtonComponent compareModeBtn = new ButtonComponent(skin, font, "Compare mode");
        compareModeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new CompareModeScreen(game));
                dispose();
            }
        });

        Table wrapper = new Table();
        wrapper.setWidth(Gdx.graphics.getWidth());
        wrapper.setHeight(Gdx.graphics.getHeight());

        wrapper.add(learningModeBtn.getComponent());
        wrapper.row().padTop(30);
        wrapper.add(compareModeBtn.getComponent());

        mainMenuStage.addActor(wrapper);
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        mainMenuStage.act();
        mainMenuStage.draw();

        /*if (Gdx.input.isTouched()) {
            game.setScreen(new LearningModeScreen(game));
            dispose();
        }*/
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}
