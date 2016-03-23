package com.b3.input;

import com.b3.mode.MainMenuScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;

/**
 * The input handler that handles input for the {@link MainMenuScreen}
 *
 * @author nbg481
 */
public class MainMenuInputHandler extends InputAdapter {

	private final MainMenuScreen mainMenuScreen;

	/**
	 * Creates a new input handler for the {@link MainMenuScreen}
	 *
	 * @param mainMenuScreen the {@link MainMenuScreen} that this class is linked to
	 */
	public MainMenuInputHandler(MainMenuScreen mainMenuScreen) {
		this.mainMenuScreen = mainMenuScreen;
	}

	/**
	 * Called whenever the mouse is moved
	 * Updates the parallax effect on the {@link MainMenuScreen}
	 *
	 * @param screenX the x position of the mouse
	 * @param screenY the y position of the mouse
	 * @return true if moved; false if not.
	 */
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		int xMax = Gdx.graphics.getWidth();
		int yMax = Gdx.graphics.getHeight();

		int xMiddle = xMax / 2;
		int yMiddle = yMax / 2;

		int xOffset = xMiddle - screenX;
		int yOffset = yMiddle - screenY;

		mainMenuScreen.setOffset(xOffset, yOffset);

		return super.mouseMoved(screenX, screenY);
	}

}
