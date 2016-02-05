package com.b3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class InputHandler {

	private static final Set<Integer> CONTROL_KEYS;

	static {
		CONTROL_KEYS = new HashSet<>(4);
		CONTROL_KEYS.add(Input.Keys.W);
		CONTROL_KEYS.add(Input.Keys.A);
		CONTROL_KEYS.add(Input.Keys.S);
		CONTROL_KEYS.add(Input.Keys.D);
	}

	private Map<Integer, Boolean> keys;
	private int zoomDelta;


	public InputHandler() {
		zoomDelta = 0;
		keys = new TreeMap<>(); // for faster iterating
		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if (CONTROL_KEYS.contains(keycode))
					keys.put(keycode, true);
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
		});
	}

	/**
	 * Polls the movement keys for 2D delta movement
	 *
	 * @param delta Vector to populate
	 * @param d     Movement speed
	 */
	public void pollMovement(Vector2 delta, float d) {
		delta.setZero();
		for (Map.Entry<Integer, Boolean> pressedKeys : keys.entrySet()) {
			if (pressedKeys.getValue()) {
				switch (pressedKeys.getKey()) {
					case Input.Keys.W:
						delta.y = d;
						break;
					case Input.Keys.A:
						delta.x = -d;
						break;
					case Input.Keys.S:
						delta.y = -d;
						break;
					case Input.Keys.D:
						delta.x = d;
						break;
					default:
						throw new IllegalArgumentException("Invalid input key: " + pressedKeys.getKey());
				}

			}
		}
	}

	public int pollZoom() {
		int ret = zoomDelta;
		zoomDelta = 0;
		return ret;
	}



}
