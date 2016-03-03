package com.b3;

import com.b3.input.InputHandler;
import com.b3.util.Config;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;

/**
 * Loads the current configuration file and sets up the input handler for the program (onClicks, onMouseOver etc.)
 */

public class MainGame extends Game {

	public InputHandler inputHandler;
	private String[] soundsDirList;

	/**
	 * On first launch of program
	 * Load config
	 * Set up input handler (mouse clicks, keyboard presses etc.)
	 */
	@Override
	public void create() {
		soundsDirList = new String[2];
		soundsDirList[0] = "core/assets/sounds/sad_failure.mp3";
		soundsDirList[1] = "core/assets/sounds/search_complete.mp3";

		// load config
		Config.loadConfig("core/assets/reference.yml", "core/assets/userconfig.yml");

		// render the models properly
		DefaultShader.defaultCullFace = 0;

		inputHandler = InputHandler.getInstance();
		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render() {
		super.render();
	}

	public String[] getSoundsDirList() {
		return soundsDirList;
	}
}
