package com.b3.entity.ai;

import com.b3.entity.component.PhysicsComponent;
import com.badlogic.gdx.math.Vector2;

/**
 * A steering behaviour that travels directly towards the target without stopping
 *
 * @author dxw405
 */
public class SteeringSeek extends SteeringWithTarget {

	public SteeringSeek(PhysicsComponent entity, SteeringTarget target) {
		super(entity, target);
	}

	/**
	 * Called once per game update
	 *
	 * @param steeringOut The desired agent steering to apply
	 */
	@Override
	public void tick(Vector2 steeringOut) {
		steeringOut.set(
				target.getPosition()
						.sub(entity.getPosition())
						.nor()
						.scl(entity.maxAcceleration)
						.limit(entity.maxSpeed)
		);
	}
}
