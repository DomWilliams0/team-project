package com.b3.mode;

import com.b3.MainGame;
import com.b3.gui.sidebars.SideBarIntensiveLearningMode;
import com.b3.gui.sidebars.SideBarNodes;
import com.b3.gui.help.HelpBox;
import com.b3.input.InputHandler;
import com.b3.input.KeyboardController;
import com.b3.input.WorldSelectionHandler;
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

/**
 * Mainly edited (ordered by no. of lines) by firstly, oxe410 and secondly, nbg481
 * Commits:
 * 15  nbg481
 * 3  oxe410
 * 2  lxd417
 *
 * A small scale world with step by step views and pop-ups to allow for uneducated 2nd year CS university students to learn about algorithms they should've learnt in year 1.
 * Sets up small world, camera, input handler and launches the world paused (forcing / implying step-by-step)
 */

public class LearningMode implements Screen {

    private World world;
    private WorldCamera camera;
    private Stage sideBarStage;
    private SideBarIntensiveLearningMode sideBar;
    private SideBarNodes sideBarNodes;
    private HelpBox helpBox;
    private KeyboardController keyboardController;
    private MainGame game;

    /**
     * Constructs the world, sets up the camera, loads to worldmap and launches the world paused.
     * @param game used to set up the world, contains directories to config files
     */
    public LearningMode(MainGame game) {
        // init database
        //Database.init();

        this.game = game;

        // create world
        world = new World("core/assets/world/world_smaller_test.tmx", Mode.LEARNING, game.inputHandler);

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

        // init gui
        setupSidebar();

        // register input handlers
        initInputHandlers(game.inputHandler);
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
        inputHandler.addProcessor(sideBarStage);

        // world clicking
        inputHandler.addProcessor(new WorldSelectionHandler(world));

    }

    /**
     * Sets up the sidebars (one with options on the left; one with nodes and step-by-step buttons on right; and help box on top)
     */
    private void setupSidebar() {
        sideBarStage = new Stage(new ScreenViewport());

        sideBar = new SideBarIntensiveLearningMode(sideBarStage, world);
        sideBar.setController(game);
        sideBarStage.addActor(sideBar);

        sideBarNodes = new SideBarNodes(sideBarStage, world);
        sideBarNodes.setStepthrough(true);
        sideBarStage.addActor(sideBarNodes);

        helpBox = new HelpBox(sideBarStage, world);
        sideBarStage.addActor(helpBox);
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
        sideBarStage.act(Gdx.graphics.getDeltaTime());
        sideBarNodes.render();
        if (!world.getPseudoCode()) {sideBarNodes.resetPseudoCode(); world.setPseudoCode(true);}
        if(world.hasNewClick()) sideBarNodes.highlightNode(world.getCurrentClick(), true);
        if(sideBarNodes.hasNewClick()) world.setCurrentClick(sideBarNodes.getNewClick().getX(), sideBarNodes.getNewClick().getY());
        sideBar.render();
        sideBarStage.draw();

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
        sideBarStage.getViewport().update(width, height, true);
        sideBar.resize(width, height);
        sideBarNodes.resize(width, height);
        helpBox.resize(width, height);
        world.getCoordinatePopup().resize();

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
