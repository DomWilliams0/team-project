package com.b3;

import com.b3.input.InputHandler;
import com.b3.input.SoundController;
import com.b3.mode.CompareMode;
import com.b3.mode.MainMenuScreen;
import com.b3.util.Config;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;

/**
 * Loads the current configuration file and sets up the input handler for the program (onClicks, onMouseOver etc.)
 */
public class MainGame extends Game {

	private InputHandler inputHandler;
	private SoundController sc;

	/**
	 * On first launch of program
	 * Load config
	 * Set up input handler (mouse clicks, keyboard presses etc.)
	 */
	@Override
	public void create() {
		sc = new SoundController(new String[]{
				"core/assets/sounds/sad_failure.wav",
				"core/assets/sounds/search_complete.mp3",
				"core/assets/sounds/error_buildings.mp3",
				"core/assets/sounds/ping.mp3"
		});

		// load config
		Config.loadConfig("core/assets/reference.yml", "core/assets/userconfig.yml");

		// render the models properly
		DefaultShader.defaultCullFace = 0;

		inputHandler = InputHandler.getInstance();
		setScreen(new MainMenuScreen(this));
	}

	public InputHandler getInputHandler() {
		return inputHandler;
	}

	@Override
	public void dispose() {
		super.dispose();
		sc.dispose();
	}
}
