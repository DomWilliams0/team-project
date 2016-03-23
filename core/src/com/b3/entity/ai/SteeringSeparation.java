package com.b3.entity.ai;

import com.b3.entity.component.PhysicsComponent;
import com.badlogic.gdx.math.Vector2;

/**
 * @author dxw405
 */
public class SteeringSeparation extends SteeringFlocking {


	public SteeringSeparation(PhysicsComponent entity) {
		super(entity);
	}

	/**
	 * Called on each of the neighbouring entities
	 *
	 * @param steeringOut       The output {@link Vector2} for this {@link Steering}
	 * @param neighbourPosition The position of this neighbour, for efficiency's sake
	 * @param neighbour         The neighbour in question
	 */
	@Override
	protected void processNeighbour(Vector2 steeringOut, Vector2 neighbourPosition, PhysicsComponent neighbour) {
		steeringOut.add(neighbourPosition.sub(entity.getPosition()));
	}

	/**
	 * Called on the cumulative steering output from calling
	 * {@link SteeringFlocking#processNeighbour(Vector2, Vector2, PhysicsComponent)}
	 * on all neighbours
	 *
	 * @param steeringOut The cumulative output to be modified in-place
	 */
	@Override
	protected void finaliseOutput(Vector2 steeringOut) {
		steeringOut
				.sub(entity.getPosition())
				.scl(-1)
				.nor();
	}
}
