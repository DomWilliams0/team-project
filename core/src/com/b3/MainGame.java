package com.b3;

import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;


public class MainGame extends ApplicationAdapter {

	private World world;
	private WorldCamera camera;
	private InputHandler inputHandler;

	@Override
	public void create() {
		Config.loadConfig("core/assets/reference.yml", "core/assets/userconfig.yml");
		world = new World("core/assets/world/world.tmx");
		inputHandler = new InputHandler();

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
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}

	@Override
	public void render() {
		// clear screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		// camera movement
		camera.move(inputHandler);
		camera.update();

		// world rendering
		world.render();

		if (inputHandler.shouldExit())
			Gdx.app.exit();

	}


	@Override
	public void dispose() {
		world.dispose();
	}
}
