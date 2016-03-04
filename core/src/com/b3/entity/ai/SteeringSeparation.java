package com.b3.entity.ai;

import com.b3.entity.component.PhysicsComponent;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Vector2;

public class SteeringSeparation extends SteeringFlocking {


	public SteeringSeparation(PhysicsComponent entity, ImmutableArray<Entity> entities) {
		super(entity, entities);
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
