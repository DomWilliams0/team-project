package com.b3;

import com.b3.gui.components.ImageButtonComponent;
import com.b3.mode.CompareMode;
import com.b3.mode.LearningMode;
import com.b3.mode.PracticeMode;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * Shows the main menu, allowing the user to choose learning mode or comparison mode.
 */
public class MainMenuScreen implements Screen {

    private final Table wrapper;

    private final OrthographicCamera camera;
    private final Stage mainMenuStage;
    private final SpriteBatch spriteBatch;
    private final Sprite sprite;
    private final Sprite spriteTwoText;

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
        ImageButtonComponent compareModeBtn = new ImageButtonComponent("compare_mode_primary.png", "compare_mode_mouseover.png", "compare_mode_mouseover.png");
        compareModeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new CompareMode(game));
                dispose();
            }
        });

        // Practice mode button
        // --------------------
        ImageButtonComponent practiceModeBtn = new ImageButtonComponent("practice_mode_primary.png", "practice_mode_mouseover.png", "practice_mode_mouseover.png");
        practiceModeBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PracticeMode(game));
                dispose();
            }
        });

        // Learning mode button
        // --------------------
        ImageButtonComponent learningModeBtn = new ImageButtonComponent("learning_mode_primary.png", "learning_mode_mouseover.png", "learning_mode_mouseover.png");
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

        //Load texture for ICON
        spriteBatch = new SpriteBatch();
        sprite = new Sprite(new Texture("core/assets/icon.png"));

        spriteTwoText= new Sprite(new Texture("core/assets/icon_final.png"));
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

        spriteBatch.begin();
        float size = (float) (Gdx.graphics.getHeight() / 5);
        spriteBatch.draw(sprite, Gdx.graphics.getWidth() / 2 - size / 2, Gdx.graphics.getHeight() - size, size, size);
        spriteBatch.draw(spriteTwoText, Gdx.graphics.getWidth() / 2 - spriteTwoText.getWidth()/2, (float) (Gdx.graphics.getHeight() - size*1.25) - 10);
        spriteBatch.end();

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
        spriteBatch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
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
