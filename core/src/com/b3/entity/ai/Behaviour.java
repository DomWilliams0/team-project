package com.b3.entity.ai;


import com.b3.entity.Agent;
import com.badlogic.gdx.math.Vector2;

public abstract class Behaviour {
	protected Agent agent;
	protected final Steering steering;

	public Behaviour(Agent agent, Steering steering) {
		this.agent = agent;
		this.steering = steering;
	}

	public abstract BehaviourType getType();

	public void tick(Vector2 steeringOutput) {
		steering.tick(steeringOutput);
	}
}
