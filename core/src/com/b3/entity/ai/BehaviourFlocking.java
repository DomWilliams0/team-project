package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.b3.entity.component.PhysicsComponent;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Flocking behaviour to move all agents in a flocking-like behaviour
 *
 * @author dxw405
 */
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

	/**
	 * Ticks the movement of the {@link Agent}, and stores the resulting steering
	 * {@link Vector2} in <code>steeringOutput</code>
	 */
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

	/**
	 * Ticks the given flocking steering with the list of neighbours, to avoid recalculating 3x for every entity
	 *
	 * @param steering       The steering behaviour to tick
	 * @param neighbours     The neighbours in range of the entity
	 * @param additionVector The output behaviour for this specific steering, to be added to the grand total
	 * @param steeringOutput The grand total cumulative steering output
	 */
	private void tickSteering(SteeringFlocking steering, List<PhysicsComponent> neighbours, Vector2 additionVector, Vector2 steeringOutput) {
		steering.setNeighbours(neighbours);
		steering.tick(additionVector);
		steeringOutput.add(additionVector);
	}

	/**
	 * @return The {@link BehaviourType} of the current behaviour
	 */
	@Override
	public BehaviourType getType() {
		return BehaviourType.FLOCK;
	}

	/**
	 * Updates <code>neighbours</code> with the agents in the range of <code>DISTANCE</code> of this entity
	 */
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
