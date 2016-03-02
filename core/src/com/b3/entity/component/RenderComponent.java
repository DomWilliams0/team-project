package com.b3.entity.component;

import com.b3.world.ModelController;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

/**
 * An agent's physical manifestation on the screen, through the power of magnets
 */
public class RenderComponent implements Component {

	public final ModelController controller;
	public final Color dotColour;

	public RenderComponent(ModelController controller, Color dotColour) {
		this.controller = controller;
		this.dotColour = dotColour;
	}

}
