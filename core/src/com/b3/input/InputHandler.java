package com.b3.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;

/**
 * A global wrapper around libGDX's input multiplexer
 *
 * @author dxw405 oxw410
 */
public class InputHandler {

	private InputMultiplexer inputMultiplexer;
	private static InputHandler instance;

	/**
	 * Creates a new input handler
	 */
	private InputHandler() {
		inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);
	}

	/**
	 * @return an instance of the input handler
	 */
	public static InputHandler getInstance() {
		if (instance == null)
			instance = new InputHandler();
		return instance;
	}

	/**
	 * Adds a {@link InputProcessor} for the input handler to control
	 *
	 * @param inputProcessor the {@link InputProcessor} to
	 */
	public void addProcessor(InputProcessor inputProcessor) {
		inputMultiplexer.addProcessor(inputProcessor);
	}

	/**
	 * Adds a {@link InputProcessor} for the input handler to control
	 *
	 * @param inputProcessor
	 */
	public void removeProcessor(InputProcessor inputProcessor) {
		inputMultiplexer.removeProcessor(inputProcessor);
	}

	/**
	 * Clears the current input handler
	 */
	public void clear() {
		inputMultiplexer.clear();
	}

}
