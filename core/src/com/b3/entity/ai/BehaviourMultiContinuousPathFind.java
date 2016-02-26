package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.b3.search.Node;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Utils;
import com.badlogic.gdx.math.Vector2;

public class BehaviourMultiContinuousPathFind extends Behaviour implements BehaviourWithPathFind {

	private BehaviourPathFind pathFind;
	private WorldGraph graph;
	private SearchAlgorithm algorithm;

	public BehaviourMultiContinuousPathFind(Agent agent, SearchAlgorithm searchAlgorithm, WorldGraph worldGraph) {
		super(agent, null);
		graph = worldGraph;
		algorithm = searchAlgorithm;
		pathFind = new BehaviourPathFind(agent, agent.getPhysicsComponent().getPosition(), generateRandomTile(), searchAlgorithm, worldGraph);
	}

	@Override
	public void tick(Vector2 steeringOutput) {
		pathFind.tick(steeringOutput);
		if (pathFind.hasArrivedForTheFirstTime()) {
			if (graph.getLearningModeNext() != null)
				algorithm = graph.getLearningModeNext();
			System.out.println("Next tick is using this algorithm " + algorithm);
			Node currentPos = pathFind.getNodeFromTile(graph, agent.getPhysicsComponent().getPosition());
			if (currentPos.equals(new Node(graph.getNextDestination())))
				pathFind.reset(agent.getPhysicsComponent().getPosition(), generateRandomTile(), algorithm, graph);
			else
				pathFind.reset(agent.getPhysicsComponent().getPosition(), new Vector2(graph.getNextDestination().getX(),graph.getNextDestination().getY()), algorithm, graph);
		}
	}

	private Vector2 generateRandomTile() {
		return new Vector2(
				Utils.RANDOM.nextInt(graph.getMaxXValue()),
				Utils.RANDOM.nextInt(graph.getMaxYValue())
		);
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

	public SearchTicker getTicker() {
		return pathFind.getTicker();
	}
}
