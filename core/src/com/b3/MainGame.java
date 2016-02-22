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
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;


public class MainGame extends Game {

	public World world;
	public WorldCamera camera;
	public Stage sideBarStage;
	public Stage mainMenuStage;
	public SideBar sideBar;
	public SideBarNodes sideBarNodes;
	public KeyboardController keyboardController;

	@Override
	public void create() {
		// load config
		Config.loadConfig("core/assets/reference.yml", "core/assets/userconfig.yml");

		// create world
		world = new World("core/assets/world/world.tmx");

		// init main menu
		setupMainMenu();

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

		setScreen(new MainMenuScreen(this));
	}

	private void initInputHandlers() {
		InputHandler inputHandler = InputHandler.getInstance();

		// keyboard control has top priority
		keyboardController = new KeyboardController();
		inputHandler.addProcessor(keyboardController);

		// main menu screen clicking
		inputHandler.addProcessor(mainMenuStage);

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

	private void setupMainMenu() {
		mainMenuStage = new Stage(new ScreenViewport());
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		world.dispose();
	}
}
