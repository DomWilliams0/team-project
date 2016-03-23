package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.b3.search.Node;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Utils;
import com.b3.world.World;
import com.badlogic.gdx.math.Vector2;

/**
 * Behaviour to move agent from one node another node, following a specific path.
 * When the {@link Agent} arrives, then another destination node is picked
 *
 * @author dxw405
 */
public class BehaviourMultiContinuousPathFind extends Behaviour implements BehaviourWithPathFind {

	private BehaviourPathFind pathFind;
	private WorldGraph graph;
	private SearchAlgorithm algorithm;

	/**
	 * Construct a new behaviour
	 *
	 * @param agent	the {@link Agent} to follow this behaviour
	 * @param searchAlgorithm the {@link SearchAlgorithm} to use for this search
	 * @param worldGraph the {@link WorldGraph} to use for this search
	 * @param world the world the {@link Agent} is on
	 */
	public BehaviourMultiContinuousPathFind(Agent agent, SearchAlgorithm searchAlgorithm, WorldGraph worldGraph, World world) {
		super(agent, null);
		graph = worldGraph;
		algorithm = searchAlgorithm;
		pathFind = new BehaviourPathFind(agent, agent.getPhysicsComponent().getPosition(), Utils.pointToVector2(graph.getRandomNode().getPoint()), searchAlgorithm, world);
	}

	/**
	 * Ticks the behaviour, moving the {@link Agent} to a new position
	 *
	 * @param steeringOutput the {@link Vector2} that represents the current movement for the {@link Agent}
	 */
	@Override
	public void tick(Vector2 steeringOutput) {
		pathFind.tick(steeringOutput);
		if (pathFind.hasArrivedForTheFirstTime()) {
			if (graph.getLearningModeNext() != null)
				algorithm = graph.getLearningModeNext();
			Node currentPos = pathFind.getNodeFromTile(graph, agent.getPhysicsComponent().getPosition());
			Vector2 nextDestination = graph.getNextDestination();

			Vector2 nextGoal = nextDestination == null ?
					Utils.pointToVector2(graph.getRandomNode(currentPos).getPoint()) : nextDestination;

			pathFind.reset(agent.getPhysicsComponent().getPosition(), nextGoal, algorithm, graph);
			graph.clearNextDestination();
		}
	}

	/**
	 * @return the {@link BehaviourType} of this behaviour
	 */
	@Override
	public BehaviourType getType() {
		return BehaviourType.FOLLOW_PATH;
	}

	/**
	 * @return true if the {@link Agent} has arrived at its destination
	 */
	public boolean hasArrived() {
		return pathFind.hasArrived();
	}

	/**
	 * @return true if the {@link Agent} has arrived at its destination for the first time
	 */
	@Override
	public boolean hasArrivedForTheFirstTime() {
		return pathFind.hasArrivedForTheFirstTime();
	}

	/**
	 * @return the {@link SearchTicker} that this behaviour is using
	 */
	@Override
	public SearchTicker getSearchTicker() {
		return pathFind.getSearchTicker();
	}

}
