package com.b3.entity.component;

import com.badlogic.ashley.core.Component;

public class PositionComponent implements Component {
	public float x = 0f;
	public float y = 0f;

	public PositionComponent(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public String toString() {
		return "PositionComponent{" +
				"x=" + x +
				", y=" + y +
				'}';
	}
}


