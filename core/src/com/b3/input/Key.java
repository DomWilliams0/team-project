package com.b3.input;

import com.badlogic.gdx.Input;

/**
 * Represents the keyboard key mapping to functions that is used in this program.
 *
 * @author dxw405
 */
public enum Key {

	UP(Input.Keys.UP),
	DOWN(Input.Keys.DOWN),
	LEFT(Input.Keys.LEFT),
	RIGHT(Input.Keys.RIGHT),
	ZOOM_IN(Input.Keys.PLUS),
	ZOOM_OUT(Input.Keys.MINUS),
	EXIT(Input.Keys.ESCAPE);

	/**
	 * The key binding value.
	 */
	public final int binding;

	/**
	 * @param key The key binding value.
	 */
	Key(int key) {
		this.binding = key;
	}
	
}
