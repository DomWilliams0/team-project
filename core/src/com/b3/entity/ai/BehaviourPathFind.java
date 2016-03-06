package com.b3.entity.ai;

import com.b3.entity.Agent;
import com.b3.gui.ErrorPopups;
import com.b3.input.SoundController;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.List;
import java.util.stream.Collectors;


/**
 * A behaviour that makes an agent find a path, then follow it
 */
public class BehaviourPathFind extends Behaviour implements BehaviourWithPathFind {

	private World world;
	private ErrorPopups errorPopups;

	private Node startNode;
	private Node endNode;
	private SearchAlgorithm algorithm;
	private SearchTicker ticker;
	protected boolean wasArrivedLastFrame, hasArrivedThisFrame;

	public BehaviourPathFind(Agent agent, Vector2 startTile, Vector2 endTile, SearchAlgorithm algorithm, WorldGraph worldGraph, WorldCamera worldCamera, World world) {
		super(agent, null);
		ticker = new SearchTicker(worldGraph);
		wasArrivedLastFrame = false;

		Texture tempTexture = new Texture("core/assets/world/popups/errorSearch.png");
		tempTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		errorPopups = new ErrorPopups(worldCamera, new Sprite(tempTexture));

		startNode = getNodeFromTile(worldGraph, startTile);
		endNode = getNodeFromTile(worldGraph, endTile);
		this.algorithm = algorithm;
		this.world = world;

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
		int shouldPlayFail = 0;
		if (ticker.isPathComplete()) {
			if (steering == null) {
				shouldPlayFail = 1;
				updatePathFromTicker();
			}
			if (steering != null) {
				steering.tick(steeringOutput);
			} else {
				if (getPath().size() == 0) {
					//Path not completed properly, so show error and start again
					errorPopups.showPopup(400);
					shouldPlayFail = -1;
					SearchAlgorithm algo = world.getWorldGraph().getLearningModeNext();
					ticker.reset(algo, startNode, endNode);
				} else {
					if (!(getPath().get(getPath().size() - 1).x == endNode.getPoint().x && getPath().get(getPath().size() - 1).y == endNode.getPoint().y))
						ticker.reset(algorithm, startNode, endNode);
				}
			}
		} else
			ticker.tick();

		if (shouldPlayFail == 1 || shouldPlayFail == -1) {
			world.setPseudoCode(false);
		}
		if (shouldPlayFail == 1) {
			SoundController.playSounds(1);
		}
		if (shouldPlayFail == -1) {
			SoundController.playSounds(0);
		}

		wasArrivedLastFrame = hasArrivedThisFrame;
		hasArrivedThisFrame = hasArrived();
	}

	public ErrorPopups getErrorPopups() {
		return errorPopups;
	}

	private List<Vector2> getPath () {
		List<Vector2> path =
				ticker.getPath()
						.stream()
						.map(p -> new Vector2(p.getPoint().x, p.getPoint().y))
						.collect(Collectors.toList());

		return path;
	}

	private void updatePathFromTicker() {
		List<Vector2> path =
				ticker.getPath()
						.stream()
						.map(p -> new Vector2(p.getPoint().x, p.getPoint().y))
						.collect(Collectors.toList());

		if (path.size() > 0)
			if (path.get(path.size()-1).x == endNode.getPoint().x && path.get(path.size()-1).y == endNode.getPoint().y) {
				System.out.println("Valid path");
				steering = new SteeringPathFollow(agent.getPhysicsComponent(), path);
			}
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
		this.startNode = getNodeFromTile(worldGraph, startTile);
		this.endNode = getNodeFromTile(worldGraph, goalTile);
		this.algorithm = algorithm;

		validateNotNull(startNode, startTile, endNode, goalTile);

		ticker.reset(algorithm, startNode, endNode);
		steering = null;
	}
}
