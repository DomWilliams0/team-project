package com.b3;

import com.b3.db.Database;
import com.b3.gui.SideBar;
import com.b3.gui.SideBarNodes;
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

public class LearningMode implements Screen {

    private World world;
    private WorldCamera camera;
    private Stage sideBarStage;
    private SideBar sideBar;
    private SideBarNodes sideBarNodes;
    private HelpBox helpBox;
    private KeyboardController keyboardController;

    public LearningMode(MainGame game) {
        // init database
        //Database.init();

        // create world
        world = new World("core/assets/world/world_smaller_test.tmx", false);

        // init gui
        setupSidebar();

        // register input handlers
        initInputHandlers(game.inputHandler);

        // init camera
        Vector2 cameraPos = new Vector2(world.getTileSize().scl(0.5f));
        camera = new WorldCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(cameraPos.x, cameraPos.y, Config.getFloat(ConfigKey.CAMERA_DISTANCE_DEFAULT));
        camera.near = 1f;
        camera.far = 300f;
        camera.lookAt(cameraPos.x, cameraPos.y, 0);
        camera.update();

        camera.setWorld(world);
        world.initEngine(camera);
        //world.initEventGenerator();
    }

    private void initInputHandlers(InputHandler inputHandler) {
        // keyboard control has top priority
        keyboardController = new KeyboardController();
        inputHandler.addProcessor(keyboardController);

        // world clicking
        inputHandler.addProcessor(sideBarStage);

        // world clicking
        inputHandler.addProcessor(new WorldSelectionHandler(world));
    }

    private void setupSidebar() {
        sideBarStage = new Stage(new ScreenViewport());

        sideBar = new SideBar(sideBarStage, world);
        sideBarStage.addActor(sideBar);

        sideBarNodes = new SideBarNodes(sideBarStage, world);
        sideBarNodes.setStepthrough(true);
        sideBarStage.addActor(sideBarNodes);

        helpBox = new HelpBox(sideBarStage, world);
        sideBarStage.addActor(helpBox);
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
        camera.move(keyboardController);
        camera.update();

        // world rendering
        world.render();

        // sidebar rendering
        sideBarStage.act(Gdx.graphics.getDeltaTime());
        sideBarNodes.render();
        sideBarStage.draw();

        if (keyboardController.shouldExit())
            Gdx.app.exit();
    }

    @Override
    public void resize(int width, int height) {
        sideBarStage.getViewport().update(width, height, true);
        sideBar.resize(width, height);
        sideBarNodes.resize(width, height);
        helpBox.resize(width, height);

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
