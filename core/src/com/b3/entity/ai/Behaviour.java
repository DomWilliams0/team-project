package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.badlogic.gdx.math.Vector2;

/**
 * A base for behaviours that contain an Agent and a corresponding Steering behaviour
 *
 * @author dxw405
 */
public abstract class Behaviour {
	protected Steering steering;
	protected Agent agent;

	/**
	 * Constructs a new behaviour with an agent and a type of movement behaviour
	 *
	 * @param agent    the agent that this new behaviour will be applied to
	 * @param steering the physics that the agent will use
	 */
	public Behaviour(Agent agent, Steering steering) {
		this.agent = agent;
		this.steering = steering;
	}

	/**
	 * @return the BehaviourType of the current behaviour
	 */
	public abstract BehaviourType getType();

	/**
	 * Ticks the movement of the agent. Updates the position of the agent following the current steering input
	 */
	public void tick(Vector2 steeringOutput) {
		steering.tick(steeringOutput);
	}

}
