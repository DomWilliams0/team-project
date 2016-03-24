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
	 * @return The singleton {@link InputHandler}
	 */
	public static InputHandler getInstance() {
		if (instance == null)
			instance = new InputHandler();
		return instance;
	}

	/**
	 * Adds a {@link InputProcessor} for the input handler to control
	 *
	 * @param inputProcessor the {@link InputProcessor} to add
	 */
	public void addProcessor(InputProcessor inputProcessor) {
		inputMultiplexer.addProcessor(inputProcessor);
	}

	/**
	 * Removes a {@link InputProcessor} from the registered listeners
	 *
	 * @param inputProcessor The {@link InputProcessor} to remove
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
