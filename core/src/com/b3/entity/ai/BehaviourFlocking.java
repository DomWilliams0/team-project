package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.b3.entity.component.PhysicsComponent;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

public class BehaviourFlocking extends Behaviour {

	private static final Vector2 additionVector = new Vector2();

	private Family family;
	private Engine engine;

	private BehaviourWander wander;

	private SteeringAlignment alignment;
	private SteeringCohesion cohesion;
	private SteeringSeparation separation;

	/**
	 * Constructs a new behaviour with an agent and a type of movement behaviour
	 *
	 * @param agent the agent that this new behaviour will be applied to
	 */
	public BehaviourFlocking(Agent agent, Engine entityEngine) {
		super(agent, null);

		alignment = new SteeringAlignment(agent.getPhysicsComponent());
		cohesion = new SteeringCohesion(agent.getPhysicsComponent());
		separation = new SteeringSeparation(agent.getPhysicsComponent());

		engine = entityEngine;
		family = Family.all(PhysicsComponent.class).get();

		wander = new BehaviourWander(agent);
	}

	@Override
	public void tick(Vector2 steeringOutput) {
		steeringOutput.setZero();

		wander.tick(steeringOutput);
		steeringOutput.nor();

		ImmutableArray<Entity> entities = engine.getEntitiesFor(family);

		tickSteering(alignment, entities, additionVector, steeringOutput);
		tickSteering(cohesion, entities, additionVector, steeringOutput);
		tickSteering(separation, entities, additionVector, steeringOutput);
	}

	private void tickSteering(SteeringFlocking steering, ImmutableArray<Entity> entities, Vector2 additionVector, Vector2 steeringOutput) {
		steering.setEntities(entities);
		steering.tick(additionVector);
		steeringOutput.add(additionVector);
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.FLOCK;
	}
}
