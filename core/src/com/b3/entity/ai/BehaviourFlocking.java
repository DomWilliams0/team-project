package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.b3.entity.component.PhysicsComponent;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.List;

public class BehaviourFlocking extends Behaviour {

	private static final Vector2 additionVector = new Vector2();
	private static final double DISTANCE = 0.05f;

	private List<PhysicsComponent> neighbours;
	private World world;

	private BehaviourWander wander;

	private SteeringAlignment alignment;
	private SteeringCohesion cohesion;
	private SteeringSeparation separation;

	/**
	 * Constructs a new behaviour with an agent and a type of movement behaviour
	 *
	 * @param agent the agent that this new behaviour will be applied to
	 */
	public BehaviourFlocking(Agent agent, World physicsWorld) {
		super(agent, null);
		world = physicsWorld;
		neighbours = new ArrayList<>();

		alignment = new SteeringAlignment(agent.getPhysicsComponent());
		cohesion = new SteeringCohesion(agent.getPhysicsComponent());
		separation = new SteeringSeparation(agent.getPhysicsComponent());


		wander = new BehaviourWander(agent);
	}

	@Override
	public void tick(Vector2 steeringOutput) {
		steeringOutput.setZero();

		wander.tick(steeringOutput);
		steeringOutput.nor();

		findNearestEntities();
		tickSteering(alignment, neighbours, additionVector, steeringOutput);
		tickSteering(cohesion, neighbours, additionVector, steeringOutput);
		tickSteering(separation, neighbours, additionVector, steeringOutput);
	}

	private void tickSteering(SteeringFlocking steering, List<PhysicsComponent> neighbours, Vector2 additionVector, Vector2 steeringOutput) {
		steering.setNeighbours(neighbours);
		steering.tick(additionVector);
		steeringOutput.add(additionVector);
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.FLOCK;
	}

	private void findNearestEntities() {

		neighbours.clear();
		QueryCallback callback = fixture -> {
			Object userData = fixture.getBody().getUserData();
			if (userData == null || !(userData instanceof Agent))
				return true;

			neighbours.add(((Agent) userData).getPhysicsComponent());
			return true;
		};
		Vector2 position = agent.getPhysicsComponent().getPosition();
		world.QueryAABB(
				callback,
				(float) (position.x - DISTANCE / 2),
				(float) (position.y - DISTANCE / 2),
				(float) (position.x + DISTANCE / 2),
				(float) (position.y + DISTANCE / 2)
		);
	}
}
