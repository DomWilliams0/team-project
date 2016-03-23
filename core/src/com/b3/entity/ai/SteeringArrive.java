package com.b3.entity.ai;

import com.b3.entity.component.PhysicsComponent;
import com.badlogic.gdx.math.Vector2;

/**
 * A steering behaviour that seeks to the target, and slows down in an attempt to stop on the target
 *
 * @author dxw405
 */
public class SteeringArrive extends SteeringWithTarget {
	private double arrivalThreshold;
	private double deaccelerationDistance;

	private SteeringSeek seek;

	/**
	 * @param owner                  The entity who owns this {@link Steering}
	 * @param target                 The target to seek to
	 * @param arrivalThreshold       The maximum acceptable distance from the goal
	 * @param deaccelerationDistance The distance from the goal at which to start slowing down
	 */
	public SteeringArrive(PhysicsComponent owner, SteeringTarget target,
	                      double arrivalThreshold, double deaccelerationDistance) {
		super(owner, target);
		this.arrivalThreshold = arrivalThreshold * arrivalThreshold;
		this.deaccelerationDistance = deaccelerationDistance * deaccelerationDistance;
		this.seek = new SteeringSeek(owner, target);
	}

	/**
	 * Creates a new {@link Steering} with default threshold values
	 *
	 * @param owner  The entity who owns this {@link Steering}
	 * @param target The target to seek to
	 */
	public SteeringArrive(PhysicsComponent owner, SteeringTarget target) {
		this(owner, target, 0.25f, 1f);
	}


	/**
	 * Called once per game update
	 *
	 * @param steeringOut The desired agent steering to apply
	 */
	@Override
	public void tick(Vector2 steeringOut) {
		// arrived
		double distance = getDistanceSqrd();
		if (distance <= arrivalThreshold) {
			steeringOut.setZero();
			return;
		}

		seek.tick(steeringOut);

		// slow
		if (distance <= deaccelerationDistance) {
			float scale = (float) (distance / deaccelerationDistance);
			steeringOut.scl(scale);
		}

	}
}
