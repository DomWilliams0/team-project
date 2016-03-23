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
 *
 * @author dxw405 (small additions by nbg481 oxe410)
 */
public class MainGame extends Game {

	private static ModeType currentMode;

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
				"sounds/sad_failure.wav",
				"sounds/search_complete.mp3",
				"sounds/error_buildings.mp3",
				"sounds/ping.mp3"
		});

		// load config
		Config.loadConfig("reference.yml", "userconfig.yml");

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
	 * Dispose of this object correctly, also cleanly closing the sound controller
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
		inputHandler.clear();
		super.setScreen(new MainMenuScreen(this));
		currentMode = ModeType.MENU;
	}

	/**
	 * Switches to the given mode
	 *
	 * @param modeType The mode type to switch to
	 */
	public void goToMode(ModeType modeType) {
		Config.clearConfig();
		ModeType modeBackup = currentMode;
		currentMode = modeType;

		Mode m;
		switch (modeType) {
			case MENU:
				goToMainMenu();
				return;
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
				currentMode = modeBackup;
				throw new IllegalArgumentException("Cannot switch to unknown mode '" + modeType + "'");
		}

		m.finishInitialisation();
		super.setScreen(m);
	}

	/**
	 * @return the current mode this game is in
	 */
	public static ModeType getCurrentMode() {
		return currentMode;
	}
}
