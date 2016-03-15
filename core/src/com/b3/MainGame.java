package com.b3;

import com.b3.input.InputHandler;
import com.b3.input.SoundController;
import com.b3.mode.*;
import com.b3.util.Config;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
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
		goToMainMenu();
	}

	/**
	 * @return the current input handler for this world
	 */
	public InputHandler getInputHandler() {
		return inputHandler;
	}

	/**
	 * dispose of this object correclty, also cleanly closing the sound controller
	 */
	@Override
	public void dispose() {
		super.dispose();
		sc.dispose();
	}

	@Override
	public void setScreen(Screen screen) {
		throw new UnsupportedOperationException("Use MainGame#goToMode or MainGame#goToMainMenu instead");
	}

	/**
	 * Destroys the current state and switches to the main menu
	 */
	public void goToMainMenu() {
		super.setScreen(new MainMenuScreen(this));
	}

	/**
	 * Switches to the given mode
	 *
	 * @param modeType The mode type to switch to
	 */
	public void goToMode(ModeType modeType) {
		Mode m;
		switch (modeType) {
			case LEARNING:
				m = new LearningMode(this);
				break;
			case PRACTICE:
				m = new PracticeMode(this);
				break;
			case COMPARE:
				m = new CompareMode(this);
				break;
			case TUTORIAL:
				m = new TutorialMode(this);
				break;
			default:
				throw new IllegalArgumentException("Cannot switch to unknown mode '" + modeType + "'");
		}

		m.finishInitialisation();
		super.setScreen(m);
	}
}
