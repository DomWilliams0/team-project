package com.b3.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

public class InputHandler {

	private InputMultiplexer inputMultiplexer;
	private static InputHandler instance;

	private InputHandler() {
		inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	public static InputHandler getInstance() {
		if (instance == null)
			instance = new InputHandler();
		return instance;
	}

	public void addProcessor(InputProcessor inputProcessor) {
		inputMultiplexer.addProcessor(inputProcessor);
	}

}