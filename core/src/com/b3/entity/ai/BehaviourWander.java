package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * A behaviour that makes an agent wander aimlessly around
 *
 * @author dxw405
 */
public class BehaviourWander extends Behaviour {

	private float angle;
	private float angleRate;

	/**
	 * Creates a new wandering behaviour
	 *
	 * @param agent the {@link Agent} that this behaviour is linked to
	 */
	public BehaviourWander(Agent agent) {
		super(agent, null);
		angle = MathUtils.random(360);
		angleRate = MathUtils.random(2f, 20f);
	}

	/**
	 * @return the type of this behaviour
	 */
	@Override
	public BehaviourType getType() {
		return BehaviourType.WANDER;
	}

	/**
	 * Moves the {@link Agent} linked to this behaviour.
	 *
	 * @param steeringOutput the current position of the {@link Agent} linked to this behaviour
	 */
	@Override
	public void tick(Vector2 steeringOutput) {
		angle += MathUtils.randomTriangular() * angleRate;

		steeringOutput.set(0, -1);
		steeringOutput.setAngle(angle);
	}
}
