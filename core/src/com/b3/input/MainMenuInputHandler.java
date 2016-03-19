package com.b3.input;

import com.b3.gui.PopupDescription;
import com.b3.mode.MainMenuScreen;
import com.b3.mode.TutorialMode;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.WorldGraph;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 *
 * @author nbg481
 */
public class MainMenuInputHandler extends InputAdapter {

	private final MainMenuScreen mainMenuScreen;

	public MainMenuInputHandler(MainMenuScreen mainMenuScreen) {
		this.mainMenuScreen = mainMenuScreen;
	}

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
