package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.b3.gui.popup.Popup;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Utils;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.math.Vector2;

public class BehaviourMultiContinuousPathFind extends Behaviour implements BehaviourWithPathFind {

	private BehaviourPathFind pathFind;
	private WorldGraph graph;
	private SearchAlgorithm algorithm;
	private Popup errorPopup;

	public BehaviourMultiContinuousPathFind(Agent agent, SearchAlgorithm searchAlgorithm, WorldGraph worldGraph, WorldCamera worldCamera, World world) {
		super(agent, null);
		graph = worldGraph;
		algorithm = searchAlgorithm;
		pathFind = new BehaviourPathFind(agent, agent.getPhysicsComponent().getPosition(), generateRandomTile(), searchAlgorithm, worldGraph, worldCamera, world);
	}

	@Override
	public void tick(Vector2 steeringOutput) {
		pathFind.tick(steeringOutput);
		if (pathFind.hasArrivedForTheFirstTime()) {
			if (graph.getLearningModeNext() != null)
				algorithm = graph.getLearningModeNext();
			Node currentPos = pathFind.getNodeFromTile(graph, agent.getPhysicsComponent().getPosition());

			if (currentPos.equals(new Node(graph.getNextDestination())) || graph.getNextDestination().getY() == 0 && graph.getNextDestination().getX() == 0 || graph.getNextDestination().getX() == -5)
				pathFind.reset(agent.getPhysicsComponent().getPosition(), generateRandomTile(currentPos), algorithm, graph);
			else
				pathFind.reset(agent.getPhysicsComponent().getPosition(), new Vector2(graph.getNextDestination().getX(),graph.getNextDestination().getY()), algorithm, graph);

			graph.setNextDestination(-5,-5);
		}
	}

	private Vector2 generateRandomTile() {
		int x, y;
		do {
			x = Utils.RANDOM.nextInt(graph.getMaxXValue());
			y = Utils.RANDOM.nextInt(graph.getMaxYValue());
		} while (!graph.hasNode(new Point(x, y)));
		return new Vector2(x, y);
	}


	private Vector2 generateRandomTile(Node currentPos) {
		int x, y;
		do {
			x = Utils.RANDOM.nextInt(graph.getMaxXValue());
			y = Utils.RANDOM.nextInt(graph.getMaxYValue());
		} while (!graph.hasNode(new Point(x, y)) && !new Point(x,y).equals(currentPos.getPoint()));
		return new Vector2(x, y);
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
