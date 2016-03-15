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

	@Override
	protected void processNeighbour(Vector2 steeringOut, Vector2 neighbourPosition, PhysicsComponent neighbour) {
		steeringOut.add(neighbourPosition.sub(entity.getPosition()));
	}

	@Override
	protected void finaliseOutput(Vector2 steeringOut) {
		steeringOut
				.sub(entity.getPosition())
				.scl(-1)
				.nor();
	}
}
