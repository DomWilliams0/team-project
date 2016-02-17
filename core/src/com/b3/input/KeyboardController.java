package com.b3.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

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

	public KeyboardController() {
		zoomDelta = 0;
		exit = false;
		keys = new TreeMap<>(); // for faster iterating
	}


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

	@Override
	public boolean keyUp(int keycode) {
		if (CONTROL_KEYS.contains(keycode))
			keys.put(keycode, false);
		return false;
	}

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

	public boolean shouldExit() {
		boolean ret = exit;
		exit = false;
		return ret;
	}

}