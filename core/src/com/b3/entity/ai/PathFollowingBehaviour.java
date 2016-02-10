package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.b3.entity.component.PhysicsComponent;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PathFollowingBehaviour implements Behaviour {

	// progress?
	// reason?

	private Array<Vector2> path;
	private boolean arrive;

	public PathFollowingBehaviour(Array<Vector2> path, boolean arrive) {
		this.path = path;
		this.arrive = arrive;
	}

	public Array<Vector2> getPath() {
		return path;
	}

	@Override
	public void begin(Agent agent) {
		if (path.size < 2)
			throw new IllegalArgumentException("Given path is too short in PathFollowingBehaviour");

		// move to centres of tiles
		for (int i = 0; i < path.size; i++)
			path.get(i).add(0.5f, -0.5f);

		LinePath<Vector2> linePath = new LinePath<>(path, true);
		PhysicsComponent phys = agent.getPhysicsComponent();

		FollowPath<Vector2, LinePath.LinePathParam> behaviour =
				new FollowPath<>(phys, linePath);

		behaviour.setArriveEnabled(arrive);
		behaviour.setPathOffset(1f);
		behaviour.setPredictionTime(1f);
		behaviour.setArrivalTolerance(1f);
		behaviour.setDecelerationRadius(0f);

		phys.setSteeringBehavior(behaviour);
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.FOLLOW_PATH;
	}
}
