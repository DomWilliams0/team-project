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

	public SteeringArrive(PhysicsComponent owner, SteeringTarget target,
						  double arrivalThreshold, double decelerationDistance) {
		super(owner, target);
		this.arrivalThreshold = arrivalThreshold * arrivalThreshold;
		this.deaccelerationDistance = decelerationDistance * decelerationDistance;
		this.seek = new SteeringSeek(owner, target);
	}

	public SteeringArrive(PhysicsComponent owner, SteeringTarget target) {
		this(owner, target, 0.25f, 1f);
	}


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
