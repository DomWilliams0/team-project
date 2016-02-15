package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

/**
 * A behaviour that makes an agent follow the list of tile positions given as a path
 */
public class BehaviourPathFollow extends Behaviour {

	/**
	 * @param agent The agent who owns this behaviour
	 * @param path  A list of tile positions to follow as a path
	 */
	public BehaviourPathFollow(Agent agent, List<Vector2> path) {
		super(agent, new SteeringPathFollow(agent.getPhysicsComponent(), path));
	}

	/**
	 * @return True if the agent has reached the end of its path, otherwise false
	 */
	public boolean hasArrived() {
		return ((SteeringPathFollow) steering).hasArrived();
	}

	/**
	 * @return The tile position of the current node in the path that the agent is travelling to
	 */
	public Vector2 getCurrentGoal() {
		return ((SteeringPathFollow) steering).getCurrentGoal();
	}

	/**
	 * @return An immutable version of the agent's full path
	 */
	public List<Vector2> getFullPath() {
		return ((SteeringPathFollow) steering).getFullPath();
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.FOLLOW_PATH;
	}
}
