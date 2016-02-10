package com.b3.entity.ai;


import com.b3.entity.Agent;

public interface Behaviour {
	void begin(Agent agent);

	BehaviourType getType();

}
