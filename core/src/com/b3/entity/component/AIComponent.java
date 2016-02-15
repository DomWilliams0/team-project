package com.b3.entity.component;

import com.b3.entity.ai.Behaviour;
import com.badlogic.ashley.core.Component;

/**
 * An agent's brain
 */
public class AIComponent implements Component {

	public Behaviour behaviour;

	public AIComponent(Behaviour behaviour) {
		this.behaviour = behaviour;
	}

}
