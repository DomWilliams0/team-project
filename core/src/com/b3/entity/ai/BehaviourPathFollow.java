package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.badlogic.gdx.math.Vector2;

import java.util.List;

public class BehaviourPathFollow extends Behaviour {

	public BehaviourPathFollow(Agent agent, List<Vector2> path) {
		super(agent, new SteeringPathFollow(agent.getPhysicsComponent(), path));
	}

	public boolean hasArrived() {
		return ((SteeringPathFollow) steering).hasArrived();
	}

	public Vector2 getCurrentGoal() {
		return ((SteeringPathFollow) steering).getCurrentGoal();
	}

	public List<Vector2> getFullPath() {
		return ((SteeringPathFollow) steering).getFullPath();
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.FOLLOW_PATH;
	}
}
