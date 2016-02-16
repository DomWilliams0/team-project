package com.b3;

import com.b3.gui.SideBar;
import com.b3.gui.SideBarNodes;
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
	private InputHandler inputHandler;
	private Stage sideBarStage;
	private SideBar sideBar;
	private SideBarNodes sideBarNodes;

	@Override
	public void create() {
		Config.loadConfig("core/assets/reference.yml", "core/assets/userconfig.yml");

		world = new World("core/assets/world/world.tmx");

		// Setup sidebar
		sideBarStage = new Stage(new ScreenViewport());
		sideBar = new SideBar(sideBarStage);
		sideBar.setWorld(world);
		sideBarStage.addActor(sideBar);

		sideBarNodes = new SideBarNodes(sideBarStage);
		sideBarNodes.setWorld(world);
		sideBarStage.addActor(sideBarNodes);

		// Setup input handlers
		inputHandler = InputHandler.getInstance();
		inputHandler.addProcessor(sideBarStage);
		inputHandler.initInputProcessor();

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
		// TODO - Multiply it by the speed slider.
		Utils.deltaTime = Gdx.graphics.getRawDeltaTime();

		// clear screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// camera movement
		camera.move(inputHandler);
		camera.update();

		// world rendering
		world.render();

		// Side bar rendering
		sideBar.act();
		sideBar.render();

		sideBarNodes.act();
		sideBarNodes.render();

		if (inputHandler.shouldExit())
			Gdx.app.exit();

	}


	@Override
	public void dispose() {
		world.dispose();
	}
}
