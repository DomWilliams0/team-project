package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.b3.entity.component.PhysicsComponent;
import com.badlogic.gdx.ai.steer.behaviors.Wander;
import com.badlogic.gdx.math.Vector2;

public class WanderBehaviour implements Behaviour {

	@Override
	public void begin(Agent agent) {
		PhysicsComponent physics = agent.getPhysicsComponent();

		Wander<Vector2> wander = new Wander<>(physics);
		wander.setWanderRate(1f);
		wander.setWanderRadius(0.8f);
		physics.setSteeringBehavior(wander);
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.WANDER;
	}
}
