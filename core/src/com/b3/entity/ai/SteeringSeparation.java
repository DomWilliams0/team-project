package com.b3.entity.ai;

import com.b3.entity.component.PhysicsComponent;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class SteeringSeparation extends Steering {

	private static Vector2 tmpVector = new Vector2();
	private static final int ALIGNMENT_DISTANCE_SQRD = 5 * 5;

	private List<PhysicsComponent> entities;

	public SteeringSeparation(PhysicsComponent entity, List<PhysicsComponent> entities) {
		super(entity);
		this.entities = entities;

		// remove self
		entities.remove(entity);
	}

	@Override
	public void tick(Vector2 steeringOut) {
		steeringOut.setZero();
		int count = 0;

		for (PhysicsComponent e : entities) {
			tmpVector.set(e.getPosition());
			if (tmpVector.sub(entity.getPosition()).len2() < ALIGNMENT_DISTANCE_SQRD) {
				steeringOut.add(tmpVector.sub(entity.getPosition()));
				count += 1;
			}
		}

		if (count != 0) {
			steeringOut
					.scl(1f / count)
					.sub(entity.getPosition())
					.scl(-1)
					.nor();
		}

	}
}
