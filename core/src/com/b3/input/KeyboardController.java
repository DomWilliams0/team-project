package com.b3.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * An input listener that keeps state about movement keys,
 * zooming and exiting
 *
 * @author dxw405
 */
public class KeyboardController extends InputAdapter {

	private static final Set<Integer> CONTROL_KEYS;

	static {
		CONTROL_KEYS = new HashSet<>(Key.values().length);

		for (Key key : Key.values())
			CONTROL_KEYS.add(key.binding);
	}

	private Map<Integer, Boolean> keys;
	private int zoomDelta;
	private boolean exit;

	/**
	 * Creates a new instance of the keyboard controller
	 */
	public KeyboardController() {
		zoomDelta = 0;
		exit = false;
		keys = new TreeMap<>(); // for faster iterating
	}

	/**
	 * Decides what to do when the user presses a key on the keyboard
	 *
	 * @param keycode the integer representing the key pressed on the keyboard
	 * @return true if key which has binding was pressed; false otherwise.
	 */
	@Override
	public boolean keyDown(int keycode) {
		if (CONTROL_KEYS.contains(keycode)) {
			if (keycode == Key.EXIT.binding)
				exit = true;

			keys.put(keycode, true);
			return true;

		}
		return false;
	}

	/**
	 * Decides what to do when the user releases a key on the keyboard
	 *
	 * @param keycode the integer representing the key pressed on the keyboard
	 * @return false
	 */
	@Override
	public boolean keyUp(int keycode) {
		if (CONTROL_KEYS.contains(keycode))
			keys.put(keycode, false);
		return false;
	}

	/**
	 * Decides what to do when the user scrolls
	 *
	 * @param amount the amount of scrolling the user does
	 * @return false
	 */
	@Override
	public boolean scrolled(int amount) {
		zoomDelta = amount;
		return false;
	}

	/**
	 * Polls the movement keys for 2D delta movement
	 *
	 * @param delta Vector to populate
	 * @param d     Movement speed
	 */
	public void pollMovement(Vector2 delta, float d) {
		delta.setZero();
		keys.entrySet().stream().filter(Map.Entry::getValue).forEach(pressedKeys -> {
			Integer i = pressedKeys.getKey();
			if (i == Key.UP.binding)
				delta.y = d;
			else if (i == Key.LEFT.binding)
				delta.x = -d;
			else if (i == Key.DOWN.binding)
				delta.y = -d;
			else if (i == Key.RIGHT.binding)
				delta.x = d;
		});
	}

	/**
	 * Updates the zoom on the world, depending on how much the user has zoomed
	 *
	 * @return true is zoom has changed; false otherwise
	 */
	public int pollZoom() {
		int ret = zoomDelta; // mouse zoom

		// check keyboard zoom
		if (ret == 0) {
			Boolean kbIn = keys.get(Key.ZOOM_IN.binding);
			Boolean kbOut = keys.get(Key.ZOOM_OUT.binding);

			// only zoom if one is true
			if (kbIn == Boolean.TRUE || kbOut == Boolean.TRUE)
				ret = kbIn != null && kbIn ? -1 : 1;
		}

		zoomDelta = 0;
		return ret;
	}

	/**
	 * Checks if the user has pressed esc to exit the program
	 *
	 * @return true to exit; false otherwise
	 */
	public boolean shouldExit() {
		boolean ret = exit;
		exit = false;
		return ret;
	}

}
