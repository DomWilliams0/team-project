package com.b3.entity.ai;

import com.b3.entity.component.PhysicsComponent;
import com.badlogic.gdx.math.Vector2;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A steering behaviour that follows the given path, and arrives at the last node
 */
public class SteeringPathFollow extends Steering {
	private static final float DEFAULT_ARRIVAL_THRESHOLD = 0.25f;

	private final List<Vector2> originalPath;
	private Queue<Vector2> path;
	private boolean arrived;
	private double nodeArrivalThreshold;

	private SteeringSeek seekSteering;
	private SteeringArrive arriveSteering;
	private TileSteeringTarget steeringTarget;

	public SteeringPathFollow(PhysicsComponent entity, double nodeArrivalThreshold, List<Vector2> path) {
		super(entity);

		// invalid path
		if (path.size() < 2)
			throw new IllegalArgumentException("Path must be at least 2 nodes long in SteeringPathFollow");

		this.arrived = false;
		this.nodeArrivalThreshold = nodeArrivalThreshold * nodeArrivalThreshold;
		this.path = new ArrayDeque<>(path.stream().map((p) -> p.add(0.5f, 0.5f)).collect(Collectors.toList()));
		this.originalPath = Collections.unmodifiableList(path);
		this.steeringTarget = new TileSteeringTarget(Vector2.Zero);
		this.arriveSteering = new SteeringArrive(entity, steeringTarget);
		this.seekSteering = new SteeringSeek(entity, steeringTarget);

		updateSeek();
	}

	public SteeringPathFollow(PhysicsComponent entity, double nodeArrivalThreshold, Vector2... path) {
		this(entity, nodeArrivalThreshold, Arrays.asList(path));
	}

	public SteeringPathFollow(PhysicsComponent entity, List<Vector2> path) {
		this(entity, DEFAULT_ARRIVAL_THRESHOLD, path);
	}

	public SteeringPathFollow(PhysicsComponent entity, Vector2... path) {
		this(entity, DEFAULT_ARRIVAL_THRESHOLD, path);
	}


	@Override
	public void tick(Vector2 steeringOut) {
		// finished
		if (arrived) {
			arriveSteering.tick(steeringOut);
			return;
		}

		// arrived at next node
		if (seekSteering.getDistanceSqrd() <= nodeArrivalThreshold)
			updateSeek();

		boolean lastNode = path.isEmpty();
		Steering steering = lastNode ? arriveSteering : seekSteering;

		steering.tick(steeringOut);
	}

	public boolean hasArrived() {
		return arrived;
	}

	public Vector2 getCurrentGoal() {
		return steeringTarget.getPosition();
	}

	public List<Vector2> getFullPath() {
		return originalPath;
	}

	private void updateSeek() {
		if (path.isEmpty()) {
			arrived = true;
		} else {
			Vector2 nextPos = path.poll();
//			nextPos.add(0.5f, -0.5f);
			steeringTarget.setPosition(nextPos);
		}
	}
}
