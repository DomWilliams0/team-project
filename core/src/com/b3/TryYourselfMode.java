package com.b3;

import com.b3.gui.SideBarIntensiveLearningMode;
import com.b3.gui.SideBarNodes;
import com.b3.gui.help.HelpBox;
import com.b3.input.*;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TryYourselfMode implements Screen {

    private World world;
    private WorldCamera camera;
    //private Stage sideBarStage;
    //private SideBarIntensiveLearningMode sideBar;
    //private SideBarNodes sideBarNodes;
    //private HelpBox helpBox;
    private KeyboardController keyboardController;

    public TryYourselfMode(MainGame game) {
        // set up the sounds
        String[] arrSoundsDir = game.getSoundsDirList();
        SoundController soundController = new SoundController(arrSoundsDir);

        // create world
        world = new World("core/assets/world/world_smaller_test.tmx", Mode.TRY_YOURSELF, game.inputHandler, soundController);

        // init gui
        //setupSidebar();

        // register input handlers
        initInputHandlers(game.inputHandler);

        // init camera
        Vector2 cameraPos = new Vector2(world.getTileSize().scl(0.5f));
        camera = new WorldCamera(1, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(cameraPos.x, cameraPos.y, Config.getFloat(ConfigKey.CAMERA_DISTANCE_DEFAULT));
        camera.near = 1f;
        camera.far = 300f;
        camera.lookAt(cameraPos.x, cameraPos.y, 10);
        camera.rotate((float) 26, 1, 0, 0);
        camera.update();

        camera.setWorld(world);
        world.initEngine(camera);

        world.getWorldGraph().getCurrentSearch().pause(1);
        world.getWorldGraph().getCurrentSearch().setUpdated(true);
    }

    /**
     * Initialise the keyboard and mouse listeners that listen for input and decide what to do.
     * @param inputHandler
     */
    private void initInputHandlers(InputHandler inputHandler) {
        // keyboard control has top priority
        keyboardController = new KeyboardController();
        inputHandler.addProcessor(keyboardController);

        // world clicking
        //inputHandler.addProcessor(sideBarStage);

        // world clicking
        inputHandler.addProcessor(new TYMWorldSelection(world));
    }

    /**
     * Sets up the sidebars (one with options on the left; one with nodes and step-by-step buttons on right; and help box on top)
     */
    private void setupSidebar() {

    }

    @Override
    public void show() {}

    /**
     * Renders the world, the three sidebars and updates the world's position and zoom depending on input from the user via the input listeners.
     * @param delta
     */
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
        camera.move(keyboardController);
        camera.update();

        // world rendering
        world.render();

        // sidebar rendering
        /*sideBarStage.act(Gdx.graphics.getDeltaTime());
        sideBarNodes.render();
        if(world.hasNewClick()) sideBarNodes.highlightNode(world.getCurrentClick(), true);
        if(sideBarNodes.hasNewClick()) world.setCurrentClick(sideBarNodes.getNewClick().getX(), sideBarNodes.getNewClick().getY());
        sideBar.render();
        sideBarStage.draw();*/

        if (keyboardController.shouldExit())
            Gdx.app.exit();
    }

    /**
     * Updates the position of the sidebars and world and scale when the window has been resized
     * Prevents stretching of elements
     * Allows app window to be multi-sized and also work for multiple resolutions
     * @param width the current width of the window
     * @param height the current height of the window
     */
    @Override
    public void resize(int width, int height) {
        /*sideBarStage.getViewport().update(width, height, true);
        sideBar.resize(width, height);
        sideBarNodes.resize(width, height);
        helpBox.resize(width, height);*/

        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        world.dispose();
    }
}
