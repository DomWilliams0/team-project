package com.b3.entity.ai;

import com.b3.entity.component.PhysicsComponent;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * A base flocking {@link Steering} that uses its neighbours' positions to flock
 *
 * @author dxw405
 */
abstract class SteeringFlocking extends Steering {

	private List<PhysicsComponent> neighbours;

	/**
	 * @param entity The entity who owns this {@link Steering}
	 */
	public SteeringFlocking(PhysicsComponent entity) {
		super(entity);
		this.neighbours = new ArrayList<>();
	}

	/**
	 * Called once per game update
	 *
	 * @param steeringOut The desired agent steering to apply
	 */
	@Override
	public void tick(Vector2 steeringOut) {
		steeringOut.setZero();

		for (PhysicsComponent neighbour : neighbours)
			processNeighbour(steeringOut, neighbour.getPosition(), neighbour);

		if (!neighbours.isEmpty())
			finaliseOutput(steeringOut.scl(1f / neighbours.size()));
	}


	/**
	 * Called on each of the neighbouring entities
	 *
	 * @param steeringOut       The output {@link Vector2} for this {@link Steering}
	 * @param neighbourPosition The position of this neighbour, for efficiency's sake
	 * @param neighbour         The neighbour in question
	 */
	protected abstract void processNeighbour(Vector2 steeringOut, Vector2 neighbourPosition, PhysicsComponent neighbour);

	/**
	 * Called on the cumulative steering output from calling
	 * {@link SteeringFlocking#processNeighbour(Vector2, Vector2, PhysicsComponent)}
	 * on all neighbours
	 *
	 * @param steeringOut The cumulative output to be modified in-place
	 */
	protected abstract void finaliseOutput(Vector2 steeringOut);

	/**
	 * Updates the internal neighbours list for this frame. It is shared among all
	 * {@link SteeringFlocking} to avoid excessive calculation
	 *
	 * @param neighbours The new list of neighbours
	 */
	public void setNeighbours(List<PhysicsComponent> neighbours) {
		this.neighbours = neighbours;
	}
}
