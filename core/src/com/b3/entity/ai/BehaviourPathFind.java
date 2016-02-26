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
public class BehaviourPathFind extends Behaviour implements BehaviourWithPathFind {

	private SearchTicker ticker;
	protected boolean wasArrivedLastFrame, hasArrivedThisFrame;

	public BehaviourPathFind(Agent agent, Vector2 startTile, Vector2 endTile, SearchAlgorithm algorithm, WorldGraph worldGraph) {
		super(agent, null);
		ticker = new SearchTicker();
		wasArrivedLastFrame = false;

		Node startNode = getNodeFromTile(worldGraph, startTile);
		Node endNode = getNodeFromTile(worldGraph, endTile);
		validateNotNull(startNode, startTile, endNode, endTile);

		ticker.reset(algorithm, startNode, endNode);
	}

	public Node getNodeFromTile(WorldGraph worldGraph, Vector2 tile) {
		return worldGraph.getNode(new Point((int) tile.x, (int) tile.y));
	}

	private void validateNotNull(Node start, Vector2 startTile, Node end, Vector2 endTile) {
		if (start == null)
			throw new IllegalArgumentException("Invalid start node: " + startTile);
		if (end == null)
			throw new IllegalArgumentException("Invalid end node: " + endTile);
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

	@Override
	public boolean hasArrivedForTheFirstTime() {
		return hasArrivedThisFrame && !wasArrivedLastFrame;
	}

	public SearchTicker getTicker() {
		return ticker;
	}

	/**
	 * Resets this behaviour with the given parameters
	 *
	 * @param startTile  The tile to start path finding from
	 * @param goalTile   The tile to path find to
	 * @param algorithm  The algorithm to use
	 * @param worldGraph The world graph
	 */
	protected void reset(Vector2 startTile, Vector2 goalTile, SearchAlgorithm algorithm, WorldGraph worldGraph) {
		wasArrivedLastFrame = hasArrivedThisFrame = false;
		Node startNode = getNodeFromTile(worldGraph, startTile);
		Node endNode = getNodeFromTile(worldGraph, goalTile);
		validateNotNull(startNode, startTile, endNode, goalTile);

		ticker.reset(algorithm, startNode, endNode);
		steering = null;
	}
}
