package com.b3.entity.ai;

import com.b3.MainGame;
import com.b3.entity.Agent;
import com.b3.input.SoundController;
import com.b3.search.*;
import com.b3.search.util.SearchAlgorithm;
import com.b3.world.World;
import com.badlogic.gdx.math.Vector2;

import java.util.List;
import java.util.stream.Collectors;


/**
 * A behaviour that makes an agent find a path, then follow it
 *
 * @author dxw405
 */
public class BehaviourPathFind extends Behaviour implements BehaviourWithPathFind {

	private World world;

	private Node startNode;
	private Node endNode;
	private SearchAlgorithm algorithm;
	private SearchTicker ticker;
	private boolean wasArrivedLastFrame, hasArrivedThisFrame;

	/**
	 * Construct a new behaviour
	 *
	 * @param agent     the {@link Agent} to follow this behaviour
	 * @param startTile the {@link Vector2} that the {@link Agent} will start from
	 * @param endTile   the {@link Vector2} that represents the {@link Agent}'s destination
	 * @param algorithm the {@link SearchAlgorithm} that this behaviour uses
	 * @param world     the world the {@link Agent} is on
	 */
	public BehaviourPathFind(Agent agent, Vector2 startTile, Vector2 endTile, SearchAlgorithm algorithm, World world) {
		super(agent, null);
		WorldGraph worldGraph = world.getWorldGraph();
		ticker = new SearchTicker(worldGraph, MainGame.getCurrentMode());
		wasArrivedLastFrame = false;

		startNode = getNodeFromTile(worldGraph, startTile);
		endNode = getNodeFromTile(worldGraph, endTile);
		this.algorithm = algorithm;
		this.world = world;

		validateNotNull(startNode, startTile, endNode, endTile);

		ticker.reset(algorithm, startNode, endNode);
	}

	/**
	 * @param worldGraph the {@link WorldGraph} to get the node from
	 * @param tile       the {@link Vector2} position to approximate to a {@link Node} on the {@link WorldGraph}
	 * @return the {@link Node} that the tile is linked to on the worldGraph
	 */
	public Node getNodeFromTile(WorldGraph worldGraph, Vector2 tile) {
		return worldGraph.getNode(new Point((int) tile.x, (int) tile.y));
	}

	/**
	 * Check that the start and end {@link Node} on {@link WorldGraph} are not null
	 *
	 * @param start     the start {@link Node}
	 * @param startTile the start {@link Vector2} tile that relates to {@code start}
	 * @param end       the end {@link Node}
	 * @param endTile   the end {@link Vector2} tile that relates to {@code end}
	 */
	private void validateNotNull(Node start, Vector2 startTile, Node end, Vector2 endTile) {
		if (start == null)
			throw new IllegalArgumentException("Invalid start node: " + startTile);
		if (end == null)
			throw new IllegalArgumentException("Invalid end node: " + endTile);
	}

	/**
	 * @return True if has arrived, otherwise false
	 */
	public boolean hasArrived() {
		return ticker.isPathComplete() && steering != null && ((SteeringPathFollow) steering).hasArrived();
	}

	/**
	 * @return The {@link BehaviourType} of the current behaviour
	 */
	@Override
	public BehaviourType getType() {
		return BehaviourType.FOLLOW_PATH;
	}

	/**
	 * Ticks the movement of the {@link Agent}, and stores the resulting steering
	 * {@link Vector2} in <code>steeringOutput</code>
	 */
	@Override
	public void tick(Vector2 steeringOutput) {
		int shouldPlayFail = 0;
		if (ticker.isPathComplete()) {
			SoundController.stopSound(3);
			if (steering == null) {
				shouldPlayFail = 1;
				updatePathFromTicker();
			}
			if (steering != null) {
				steering.tick(steeringOutput);
			} else {
				List<Vector2> path = getPath();
				if (path.size() == 0) {
					// Path not completed properly, so show error and start again
					world.getWorldGUI().getPopupManager().showBehaviourError();
					shouldPlayFail = -1;
					SearchAlgorithm algo = world.getWorldGraph().getLearningModeNext();
					ticker.reset(algo, startNode, endNode);
					ticker.pause(SearchPauser.PLAY_PAUSE_BUTTON);
				} else {
					if (!(path.get(path.size() - 1).x == endNode.getPoint().x &&
							path.get(path.size() - 1).y == endNode.getPoint().y))
						ticker.reset(algorithm, startNode, endNode);
				}
			}
		} else
			ticker.tick();

		if (shouldPlayFail == 1) {
			SoundController.playSounds(1);
		}
		if (shouldPlayFail == -1) {
			SoundController.playSounds(0);
		}

		wasArrivedLastFrame = hasArrivedThisFrame;
		hasArrivedThisFrame = hasArrived();
	}

	/**
	 * @return The {@link List} of tiles that form <code>ticker</code>'s path,
	 * if it has been found yet
	 */
	private List<Vector2> getPath() {
		return ticker.getPath()
				.stream()
				.map(p -> new Vector2(p.getPoint().x, p.getPoint().y))
				.collect(Collectors.toList());
	}

	/**
	 * Updates the steering behaviour to follow the newly found path, if it has
	 * indeed been found
	 */
	private void updatePathFromTicker() {
		List<Vector2> path = getPath();

		if (path.size() > 0)
			if (path.get(path.size() - 1).x == endNode.getPoint().x && path.get(path.size() - 1).y == endNode.getPoint().y) {
				steering = new SteeringPathFollow(agent.getPhysicsComponent(), path);
			}
	}

	/**
	 * @return True if the {@link Agent} has arrived since the last tick
	 */
	@Override
	public boolean hasArrivedForTheFirstTime() {
		return hasArrivedThisFrame && !wasArrivedLastFrame;
	}

	/**
	 * @return The {@link SearchTicker} that this behaviour is using
	 */
	@Override
	public SearchTicker getSearchTicker() {
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
