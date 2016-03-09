package com.b3.mode;

import com.b3.MainGame;
import com.b3.gui.sidebars.SideBarNodes;
import com.b3.gui.sidebars.tabs.PracticeModeSettingsTab;
import com.b3.input.InputHandler;
import com.b3.input.KeyboardController;
import com.b3.input.PracticeModeWorldSelectionHandler;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Mainly edited (ordered by no. of lines) firstly and significantly by oxe410; secondly nbg481.
 * Commits:
 * 3  nbg481
 * 1  oxe410
 */
public class PracticeMode implements Screen {

    private World world;
    private WorldCamera camera;
    private KeyboardController keyboardController;
    private Stage popupStage;
    private Stage sideBarStage;
    private SideBarNodes sideBarNodes;
    private MainGame game;

    public PracticeMode(MainGame game) {
        // create world
        world = new World("core/assets/world/world_smaller_test_tiym.tmx", Mode.TRY_YOURSELF, game.inputHandler);

        this.game = game;

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

        world.getWorldGraph().setLearningModeNext(SearchAlgorithm.DEPTH_FIRST);
        world.getWorldGraph().getCurrentSearch().pause(1);
        world.getWorldGraph().getCurrentSearch().setUpdated(true);

        // init gui
        setupSidebar();

        // register input handlers
        initInputHandlers(game.inputHandler);

        // Display first popup
//        MessageBoxComponent descriptionPopup = new MessageBoxComponent(popupStage,
//                "Welcome to the 'Try it yourself' mode.\n" +
//                        "Here you can practice what you have learned in the 'Learning mode'.\n" +
//                        "Currently you can interact using DFS.\n" +
//                        "Now please click on the node to be expanded next.",
//                "OK");
//        descriptionPopup.show();
    }

    /**
     * Initialise the keyboard and mouse listeners that listen for input and decide what to do.
     * @param inputHandler
     */
    private void initInputHandlers(InputHandler inputHandler) {
        // Keyboard control has top priority
        keyboardController = new KeyboardController();
        inputHandler.addProcessor(keyboardController);

        // Sidebar clicking
        inputHandler.addProcessor(sideBarStage);

        // Popup clicking
        inputHandler.addProcessor(popupStage);

        // World clicking
        inputHandler.addProcessor(new PracticeModeWorldSelectionHandler(world, popupStage));
    }

    /**
     * Sets up the sidebars (one with options on the left; one with nodes and step-by-step buttons on right; and help box on top)
     */
    private void setupSidebar() {
        popupStage = new Stage(new ScreenViewport());
        sideBarStage = new Stage(new ScreenViewport());

        // INITIALISE SKIN AND FONT
        // ------------------------
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(Config.getString(ConfigKey.TEXTURE_ATLAS)));
        Skin skin = new Skin(atlas);
        BitmapFont font = Utils.getFont(Config.getString(ConfigKey.FONT_FILE), 16);

        // SIDEBAR NODES
        // -------------
        sideBarNodes = new SideBarNodes(sideBarStage, world, new ArrayList<>());

        // Get world and controller (for settings tab)
        Map<String, Object> data = new HashMap<String, Object>() {{
            put("world", world);
            put("controller", game);
        }};

        // Add settings tab
        PracticeModeSettingsTab settingsTab = new PracticeModeSettingsTab(skin, font, sideBarNodes.getPreferredWidth(), data);
        settingsTab.setName("Settings");

        sideBarNodes.addTab(settingsTab.getTab());
        sideBarNodes.setStepthrough(true);
        sideBarStage.addActor(sideBarNodes);
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

        popupStage.act(Gdx.graphics.getDeltaTime());
        popupStage.draw();

        // sidebar rendering
        sideBarStage.act(Gdx.graphics.getDeltaTime());
        sideBarNodes.render();
        if(world.hasNewClick()) sideBarNodes.highlightNode(world.getCurrentClick(), true);
        if(sideBarNodes.hasNewClick()) world.setCurrentClick(sideBarNodes.getNewClick().getX(), sideBarNodes.getNewClick().getY());
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
        popupStage.getViewport().update(width, height, true);
        sideBarStage.getViewport().update(width, height, true);
        sideBarNodes.resize(width, height);
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
