package com.b3.entity.component;

import com.b3.entity.ai.Behaviour;
import com.badlogic.ashley.core.Component;

/**
 * An agent's brain through the power of friendship
 *
 * @author dxw405
 */
public class AIComponent implements Component {
	
	/**
	 * The path following {@link Behaviour} of the agent.
	 */
	public Behaviour behaviour;
	
	/**
	 * @param behaviour The path following {@link Behaviour} of the agent.
	 */
	public AIComponent(Behaviour behaviour) {
		this.behaviour = behaviour;
	}

}
