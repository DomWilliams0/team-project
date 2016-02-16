package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.stream.Collectors;


/**
 * A behaviour that makes an agent find a path, then follow it
 */
public class BehaviourPathFind extends Behaviour {

	private SearchTicker ticker;
	private boolean wasArrivedLastFrame, hasArrivedThisFrame;

	public BehaviourPathFind(Agent agent, Vector2 startTile, Vector2 endTile, SearchAlgorithm algorithm, WorldGraph worldGraph) {
		super(agent, null);
		ticker = new SearchTicker();
		wasArrivedLastFrame = false;

		Node startNode = worldGraph.getNode(new Point((int) startTile.x, (int) startTile.y));
		Node endNode = worldGraph.getNode(new Point((int) endTile.x, (int) endTile.y));
		if (startNode == null || endNode == null)
			throw new IllegalArgumentException("Invalid start/end node: " + startTile + " or " + endTile + " is invalid");

		ticker.reset(algorithm, startNode, endNode);
	}


	public boolean hasArrived() {
		return ticker.isPathComplete() && steering != null && ((SteeringPathFollow) steering).hasArrived();
	}

	@Override
	public BehaviourType getType() {
		return BehaviourType.FOLLOW_PATH;
	}

	@Override
	public void tick(Vector2 steeringOutput) {
		if (ticker.isPathComplete()) {
			if (steering == null)
				updatePathFromTicker();
			steering.tick(steeringOutput);
		} else
			ticker.tick();

		wasArrivedLastFrame = hasArrivedThisFrame;
		hasArrivedThisFrame = hasArrived();
	}

	private void updatePathFromTicker() {
		List<Vector2> path =
				ticker.getPath()
						.stream()
						.map(p -> new Vector2(p.getPoint().x, p.getPoint().y))
						.collect(Collectors.toList());

		steering = new SteeringPathFollow(agent.getPhysicsComponent(), path);
	}

	public boolean hasArrivedForTheFirstTime() {
		return hasArrivedThisFrame && !wasArrivedLastFrame;
	}

	public SearchTicker getTicker() {
		return ticker;
	}
}
