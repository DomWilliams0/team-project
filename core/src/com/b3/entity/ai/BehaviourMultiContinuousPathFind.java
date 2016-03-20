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
 * @author dxw405
 */
public class BehaviourMultiContinuousPathFind extends Behaviour implements BehaviourWithPathFind {

	private BehaviourPathFind pathFind;
	private WorldGraph graph;
	private SearchAlgorithm algorithm;

	public BehaviourMultiContinuousPathFind(Agent agent, SearchAlgorithm searchAlgorithm, WorldGraph worldGraph, World world) {
		super(agent, null);
		graph = worldGraph;
		algorithm = searchAlgorithm;
		pathFind = new BehaviourPathFind(agent, agent.getPhysicsComponent().getPosition(), Utils.pointToVector2(graph.getRandomNode().getPoint()), searchAlgorithm, world);
	}

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


	@Override
	public BehaviourType getType() {
		return BehaviourType.FOLLOW_PATH;
	}

	public boolean hasArrived() {
		return pathFind.hasArrived();
	}

	@Override
	public boolean hasArrivedForTheFirstTime() {
		return pathFind.hasArrivedForTheFirstTime();
	}

	@Override
	public SearchTicker getSearchTicker() {
		return pathFind.getSearchTicker();
	}

}
