package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Utils;
import com.badlogic.gdx.math.Vector2;

public class BehaviourMultiContinuousPathFind extends Behaviour implements BehaviourWithPathFind {

	private BehaviourPathFind pathFind;
	private WorldGraph graph;
	private SearchAlgorithm algorithm;
	private boolean otherArrived;

	public BehaviourMultiContinuousPathFind(Agent agent, SearchAlgorithm searchAlgorithm, WorldGraph worldGraph) {
		super(agent, null);
		otherArrived = false;
		graph = worldGraph;
		algorithm = searchAlgorithm;
		pathFind = new BehaviourPathFind(agent, agent.getPhysicsComponent().getPosition(), generateRandomTile(), searchAlgorithm, worldGraph);
	}

	public void setOtherArrived(Boolean otherArrived) {
		this.otherArrived = otherArrived;
	}

	@Override
	public void tick(Vector2 steeringOutput) {
		pathFind.tick(steeringOutput);
		if (pathFind.hasArrivedForTheFirstTime() && otherArrived)
			pathFind.reset(agent.getPhysicsComponent().getPosition(),
					generateRandomTile(), algorithm, graph);
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
