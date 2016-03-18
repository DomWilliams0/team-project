package com.b3.mode;

import com.b3.MainGame;
import com.b3.entity.Agent;
import com.b3.entity.ai.BehaviourPathFind;
import com.b3.gui.sidebars.SideBarCompareMode;
import com.b3.input.InputHandler;
import com.b3.input.WorldSelectionHandler;
import com.b3.search.Point;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * A large scale world with comparison views but no pop-ups or data structue views to allow for users to see the differences
 * between the algorithms
 * Sets up large world, camera, input handler and launches the world paused (forcing / implying step-by-step)
 */
public class CompareMode extends Mode {

	private SideBarCompareMode sideBar;
	private final List<Agent> agents;

	/**
	 * Constructs the world, sets up the camera, loads to worldmap and launches the world with the search already running.
	 *
	 * @param game used to set up the world, contains directories to config files
	 */
	public CompareMode(MainGame game) {
		super(ModeType.COMPARE, game, "world/world-compare.tmx", 67, 25.f, null, 10f);
		agents = new ArrayList<>(3);
	}

	/**
	 * Sets up the sidebars (one with options on the left and help box on top)
	 */
	@Override
	protected void initSidebar() {
		super.initSidebar();
		sideBar = new SideBarCompareMode(sideBarStage, world);
		sideBar.setController(game);
		sideBarStage.addActor(sideBar);
	}

	/**
	 * Adds the special {@link WorldSelectionHandler} which listens for mouse inputs and responds accordingly, taking
	 * into account the fact that this is compare mode - pop-ups aren't needed as this mode is not for learning, it's
	 * for comparing.
	 *
	 * @param inputHandler the current input handler
	 */

	@Override
	protected void registerFurtherInputProcessors(InputHandler inputHandler) {
		// world clicking
		inputHandler.addProcessor(new WorldSelectionHandler(world));
	}

	/**
	 * Ticks the current renderer, does the following:
	 * Checks if all agents have arrived successfully
	 * If so it clears the searches and starts another search to another point
	 */
	@Override
	protected void tick() {
		WorldGraph graph = world.getWorldGraph();

		boolean allArrived = graph.getAllSearchAgents()
				.stream()
				.allMatch(a -> {
					BehaviourPathFind behaviour = (BehaviourPathFind) a.getBehaviour();
					return behaviour.hasArrived();
				});


		if (!allArrived)
			return;

		// todo user selects with mouse
		// todo correspond to search agent's segment

		graph.clearAllSearches();
		for (Agent agent : agents) {
			BehaviourPathFind oldBehaviour = (BehaviourPathFind) agent.getBehaviour();
			Point oldEnd = oldBehaviour.getSearchTicker().getEnd().getPoint();
			Vector2 oldEndVector = new Vector2(oldEnd.x, oldEnd.y);
			Point oldStart = oldBehaviour.getSearchTicker().getStart().getPoint();
			Vector2 oldStartVector = new Vector2(oldStart.x, oldStart.y);

			BehaviourPathFind newBehaviour = new BehaviourPathFind(agent,
					oldEndVector,
					oldStartVector,
					oldBehaviour.getSearchTicker().getAlgorithm(),
					world);
			agent.setBehaviour(newBehaviour);

			graph.setCurrentSearch(agent, newBehaviour.getSearchTicker());
		}

	}

	@Override
	protected void initialise() {
	}

	/**
	 * Spaws three agents on the screen, one following A*, one folllowing DFS and one following BFS and starts them moving
	 * and searching for the end position
	 */
	@Override
	public void finishInitialisation() {

		SearchAlgorithm[] algorithms = {SearchAlgorithm.A_STAR, SearchAlgorithm.DEPTH_FIRST, SearchAlgorithm.BREADTH_FIRST};
		for (int i = 0; i < 3; i++) {
			int xOffset = i * 18;
			spawnAgent(
					new Vector2(12 + xOffset, 1),
					new Vector2(2 + xOffset, 35),
					algorithms[i]
			);
		}
	}

	/**
	 * Spawns an agent following a specific algorithm on the world, with a specific start and end position
	 * @param startPos the starting position of the agent
	 * @param goalPos the ending position of the agent
	 * @param algorithm the {@link SearchAlgorithm} that the agent should use to get from the startPos to the endPos
     */
	private void spawnAgent(Vector2 startPos, Vector2 goalPos, SearchAlgorithm algorithm) {
		Agent agent = world.spawnAgent(startPos);
		BehaviourPathFind behaviour = new BehaviourPathFind(
				agent, startPos, goalPos, algorithm,
				world);
		agent.setBehaviour(behaviour);

		SearchTicker ticker = behaviour.getSearchTicker();
		world.getWorldGraph().setCurrentSearch(agent, ticker);
		agents.add(agent);
	}

	/**
	 * Updates the position of the sidebars and world and scale when the window has been resized
	 * Prevents stretching of elements
	 * Allows app window to be multi-sized and also work for multiple resolutions
	 *
	 * @param width  the current width of the window
	 * @param height the current height of the window
	 */
	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		sideBar.resize(width, height);
	}
}
