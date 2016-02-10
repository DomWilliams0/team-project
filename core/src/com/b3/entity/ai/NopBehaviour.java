package com.b3.entity.ai;

import com.b3.entity.Agent;

public class NopBehaviour implements Behaviour {
	@Override
	public void begin(Agent agent) {
		// do nothing...check!
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.NOTHING;
	}
}
