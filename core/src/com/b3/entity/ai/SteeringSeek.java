package com.b3.entity.ai;

import com.b3.entity.component.PhysicsComponent;
import com.badlogic.gdx.math.Vector2;

public class SteeringSeek extends SteeringWithTarget {

	public SteeringSeek(PhysicsComponent entity, SteeringTarget target) {
		super(entity, target);
	}

	@Override
	public void tick(Vector2 steeringOut) {
		steeringOut.set(
				target.getPosition()
						.sub(entity.getPosition())
						.nor()
						.scl(entity.getMaxAcceleration())
						.limit(entity.getMaxSpeed())
		);
	}
}
