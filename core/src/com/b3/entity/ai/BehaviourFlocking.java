package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.b3.entity.component.PhysicsComponent;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class BehaviourFlocking extends Behaviour {

	private static final Vector2 additionVector = new Vector2();


	private SteeringAlignment alignment;
	private SteeringCohesion cohesion;
	private SteeringSeparation separation;


	/**
	 * Constructs a new behaviour with an agent and a type of movement behaviour
	 *
	 * @param agent    the agent that this new behaviour will be applied to
	 */
	public BehaviourFlocking(Agent agent, List<PhysicsComponent> entities) {
		super(agent, null);

		alignment = new SteeringAlignment(agent.getPhysicsComponent(), entities);
		cohesion = new SteeringCohesion(agent.getPhysicsComponent(), entities);
		separation = new SteeringSeparation(agent.getPhysicsComponent(), entities);
	}

	@Override
	public void tick(Vector2 steeringOutput) {
		steeringOutput.setZero();

		alignment.tick(additionVector);
		steeringOutput.add(additionVector);

		cohesion.tick(additionVector);
		steeringOutput.add(additionVector);

		separation.tick(additionVector);
		steeringOutput.add(additionVector);

	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.FLOCK;
	}
}
