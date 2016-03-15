package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.badlogic.gdx.math.Vector2;

/**
 * A behaviour that does nothing
 *
 * @author dxw405
 */
public class BehaviourNop extends Behaviour {

	public BehaviourNop(Agent agent) {
		super(agent, null);
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.NOTHING;
	}

	@Override
	public void tick(Vector2 steeringOutput) {
		// do nothing...yep!
	}
}
