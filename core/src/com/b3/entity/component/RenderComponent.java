package com.b3.entity.component;

import com.b3.world.ModelController;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

/**
 * An agent's physical manifestation on the screen, through the power of magnets
 */
public class RenderComponent implements Component {

	public final ModelController controller;
	public Color dotColour;
	public boolean visible;

	public RenderComponent(ModelController controller) {
		this.controller = controller;
		this.dotColour = Color.WHITE;
		this.visible = true;
	}

}
