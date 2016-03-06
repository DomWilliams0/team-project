package com.b3;

import com.b3.gui.components.ImageButtonComponent;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Mainly edited (ordered by no. of lines) by equally oxe410 & nbg481.
 * Commits:
 *  10  oxe410
 *  4  nbg481
 *
 * Shows the main menu, allowing the user to choose learning mode or comparison mode.
 */
public class MainMenuScreen implements Screen {

    private Table wrapper;
    private ImageButtonComponent learningModeBtn;
    private ImageButtonComponent compareModeBtn;
    private ImageButtonComponent practiceModeBtn;

    private OrthographicCamera camera;
    private Stage mainMenuStage;

    /**
     * Constructs the (static / final) main menu camera and the two buttons, and sets up events for each respective button.
     * @param game used to set up the world, contains directories to config files
     */
    public MainMenuScreen(MainGame game) {
        this.camera = new OrthographicCamera();
        this.camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        this.mainMenuStage = new Stage(new ScreenViewport());

        game.inputHandler.addProcessor(mainMenuStage);

        // Compare mode button
        // --------------------
        compareModeBtn = new ImageButtonComponent("compare_mode_primary.png", "compare_mode_mouseover.png", "compare_mode_mouseover.png");
        compareModeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new CompareMode(game));
                dispose();
            }
        });

        // Practice mode button
        // --------------------
        practiceModeBtn = new ImageButtonComponent("practice_mode_primary.png", "practice_mode_mouseover.png", "practice_mode_mouseover.png");
        practiceModeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PracticeMode(game));
                dispose();
            }
        });

        // Learning mode button
        // --------------------
        learningModeBtn = new ImageButtonComponent("learning_mode_primary.png", "learning_mode_mouseover.png", "learning_mode_mouseover.png");
        learningModeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new LearningMode(game));
                dispose();
            }
        });

        wrapper = new Table();
        wrapper.setWidth(Gdx.graphics.getWidth());
        wrapper.setHeight(Gdx.graphics.getHeight());

        wrapper.add(learningModeBtn.getComponent());
        wrapper.row().padTop(30);
        wrapper.add(practiceModeBtn.getComponent());
        wrapper.row().padTop(30);
        wrapper.add(compareModeBtn.getComponent());

        mainMenuStage.addActor(wrapper);
    }

    @Override
    public void show() {}

    /**
     * Render the buttons on the screen, and update the viewpoint with the new (if any) change to the width and height of the window (allows scaling and positioning of buttons properly)
     * @param delta not used
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.925490196f, 0.941176471f, 0.941176471f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.viewportWidth = Gdx.graphics.getWidth();
        camera.viewportHeight = Gdx.graphics.getHeight();

        mainMenuStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        camera.update();
        mainMenuStage.act();
        mainMenuStage.draw();

    }

    /**
     * Called whenever the window is resized
     * Keeps the two buttons centred by updating the table to fill the screen (and so the center of the table = the center of the screeN-
     * @param width the current width of the window
     * @param height the current height of the window
     */
    @Override
    public void resize(int width, int height) {
        wrapper.setWidth(width);
        wrapper.setHeight(height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    /**
     * Cleans up stages when window is closed to allow for clean exit from program
     */
    @Override
    public void dispose() {
        mainMenuStage.dispose();
    }
}
