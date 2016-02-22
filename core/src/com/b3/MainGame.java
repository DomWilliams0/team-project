package com.b3;

import com.b3.gui.SideBar;
import com.b3.gui.SideBarNodes;
import com.b3.input.InputHandler;
import com.b3.input.KeyboardController;
import com.b3.input.WorldSelectionHandler;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


public class MainGame extends ApplicationAdapter {

	private World world;
	private WorldCamera camera;
	private Stage sideBarStage;
	private SideBar sideBar;
	private SideBarNodes sideBarNodes;
	private KeyboardController keyboardController;

	@Override
	public void create() {
		// load config
		Config.loadConfig("core/assets/reference.yml", "core/assets/userconfig.yml");

		// create world
		world = new World("core/assets/world/world.tmx");

		// init gui
		setupSidebar();

		// register input handlers
		initInputHandlers();

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

	private void initInputHandlers() {
		InputHandler inputHandler = InputHandler.getInstance();

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
	}

	@Override
	public void resize(int width, int height) {
		sideBarStage.getViewport().update(width, height, true);
		sideBarNodes.resize(width, height);

		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}

	@Override
	public void render() {
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
		sideBar.act();
		sideBar.render();

		sideBarNodes.act();
		sideBarNodes.render();

		if (keyboardController.shouldExit())
			Gdx.app.exit();
	}


	@Override
	public void dispose() {
		world.dispose();
	}
}
