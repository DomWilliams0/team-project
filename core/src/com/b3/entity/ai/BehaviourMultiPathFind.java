package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayDeque;

/**
 * A behaviour that maintains a queue of goals, which will be travelled to sequentially
 *
 * @author dxw405
 */
public class BehaviourMultiPathFind extends Behaviour implements BehaviourWithPathFind {

	private WorldGraph graph;
	private ArrayDeque<Search> goals;
	private BehaviourPathFind pathFind;
	private final SearchAlgorithm originalAlgorithm;

	public BehaviourMultiPathFind(Agent agent, Vector2 startTile, Vector2 endTile, SearchAlgorithm algorithm, WorldGraph worldGraph, WorldCamera worldCamera, World world) {
		super(agent, null);
		originalAlgorithm = algorithm;
		pathFind = new BehaviourPathFind(agent, startTile, endTile, algorithm, world);
		goals = new ArrayDeque<>();
		graph = worldGraph;
	}

	@Override
	public void tick(Vector2 steeringOutput) {
		pathFind.tick(steeringOutput);
		if (pathFind.hasArrived() && !goals.isEmpty()) {
			Search nextSearch = goals.poll();
			pathFind.reset(agent.getPhysicsComponent().getPosition(), nextSearch.goal, nextSearch.algorithm, graph);
		}
	}

	public boolean hasArrived() {
		return pathFind.hasArrived();
	}

	/**
	 * Adds a goal to the end of the goal queue.
	 * The original search algorithm (defined in the constructor) will be used
	 *
	 * @param goal The tile to path find to
	 */
	public void addNextGoal(Vector2 goal) {
		addNextGoal(goal, originalAlgorithm);
	}

	/**
	 * Adds a goal to the end of the goal queue, with the given algorithm
	 *
	 * @param goal      The tile to path find to
	 * @param algorithm The algorithm to use
	 */
	public void addNextGoal(Vector2 goal, SearchAlgorithm algorithm) {
		goals.add(new Search(algorithm, goal));
	}

	@Override
	public BehaviourType getType() {
		return pathFind.getType();
	}

	@Override
	public boolean hasArrivedForTheFirstTime() {
		return pathFind.hasArrivedForTheFirstTime();
	}


	/**
	 * A helper class to contain a goal and a search algorithm
	 */
	private class Search {
		public final SearchAlgorithm algorithm;
		public final Vector2 goal;

		private Search(SearchAlgorithm algorithm, Vector2 goal) {
			this.algorithm = algorithm;
			this.goal = goal;
		}
	}

	@Override
	public SearchTicker getSearchTicker() {
		return pathFind.getSearchTicker();
	}
}
