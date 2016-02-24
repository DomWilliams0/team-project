package com.b3;


import com.b3.input.InputHandler;
import com.b3.util.Config;
import com.badlogic.gdx.Game;

public class MainGame extends Game {
	public InputHandler inputHandler;

	@Override
	public void create() {
		// load config
		Config.loadConfig("core/assets/reference.yml", "core/assets/userconfig.yml");

		inputHandler = InputHandler.getInstance();
		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render() {
		super.render();
	}
}
