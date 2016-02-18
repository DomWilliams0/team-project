package com.b3.entity.component;

import com.b3.world.ModelController;
import com.badlogic.ashley.core.Component;

/**
 * An agent's physical manifestation on the screen, through the power of magnets
 */
public class ModelComponent implements Component {

	private final ModelController controller;

	public ModelComponent(ModelController controller) {
		this.controller = controller;
	}

	public void render(float x, float y, float degrees) {
		controller.setPosition(x, y, 0).setRotation(degrees);
	}

}
