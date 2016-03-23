package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.badlogic.gdx.math.Vector2;

/**
 * A behaviour that does nothing
 *
 * @author dxw405
 */
public class BehaviourNop extends Behaviour {

	/**
	 * Creates a new behaviour that does nothing
	 *
	 * @param agent the {@link Agent} to do nothing
	 */
	public BehaviourNop(Agent agent) {
		super(agent, null);
	}

	/**
	 * @return the type of behaviour as a {@link BehaviourType}
	 */
	@Override
	public BehaviourType getType() {
		return BehaviourType.NOTHING;
	}

	/**
	 * Ticks the behaviour, doing nothing
	 *
	 * @param steeringOutput the {@link Vector2} that represents the current movement for the {@link Agent}
	 */
	@Override
	public void tick(Vector2 steeringOutput) {
		// do nothing...yep!
	}
}
