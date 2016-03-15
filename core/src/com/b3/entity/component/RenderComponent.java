package com.b3.entity.component;

import com.b3.entity.Agent;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.world.ModelController;
import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Color;

/**
 * An agent's physical manifestation on the screen, through the power of magnets
 *
 * @author dxw405
 */
public class RenderComponent implements Component {
	
	/**
	 * The render model of the agent.
	 * If rendering of models is enabled.
	 */
	public final ModelController controller;
	
	/**
	 * If the model is not rendered, the colour the dot representative will be.
	 */
	public Color dotColour;
	
	/**
	 * If the model is not rendered, the radius the dot representative will be.
	 */
	public float radius;
	
	/**
	 * @param controller The model that the agent will have.
	 *                   (If rendering of models is enabled)
	 */
	public RenderComponent(ModelController controller) {
		this.controller = controller;
		this.dotColour = Agent.FLOCKING_COLOUR;
		this.radius = Config.getFloat(ConfigKey.ENTITY_DIAMETER) / 2f;
	}
}
