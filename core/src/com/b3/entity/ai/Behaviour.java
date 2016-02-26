package com.b3.entity.ai;


import com.b3.entity.Agent;
import com.b3.search.Node;
import com.b3.search.Point;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

/**
 * A base for behaviours that contain an Agent and a corresponding Steering behaviour
 */
public abstract class Behaviour {
	protected Steering steering;
	protected Agent agent;

	public Behaviour(Agent agent, Steering steering) {
		this.agent = agent;
		this.steering = steering;
	}

	public abstract BehaviourType getType();

	public void tick(Vector2 steeringOutput) {
		steering.tick(steeringOutput);
	}

}
