package com.b3.entity.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

public class RenderComponent implements Component {
	public Color colour;
	public float radius;

	public RenderComponent(Color colour, float radius) {
		this.colour = colour;
		this.radius = radius;
	}
}
