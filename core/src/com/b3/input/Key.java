package com.b3.input;

import com.badlogic.gdx.Input;

public enum Key {
	UP(Input.Keys.UP),
	DOWN(Input.Keys.DOWN),
	LEFT(Input.Keys.LEFT),
	RIGHT(Input.Keys.RIGHT),
	ZOOM_IN(Input.Keys.PLUS),
	ZOOM_OUT(Input.Keys.MINUS),
	EXIT(Input.Keys.ESCAPE);


	public final int binding;

	Key(int key) {

		this.binding = key;
	}
}
