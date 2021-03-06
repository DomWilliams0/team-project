package com.b3.mode;

import com.b3.MainGame;
import com.b3.entity.Agent;
import com.b3.entity.ai.BehaviourMultiContinuousPathFind;
import com.b3.gui.sidebars.SideBar;
import com.b3.gui.sidebars.SideBarNodes;
import com.b3.input.InputHandler;
import com.b3.input.WorldSelectionHandler;
import com.b3.search.SearchPauser;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Config;
import com.b3.util.ConfigKey;

/**
 * A small scale world with step by step views and pop-ups to allow for users with limited knowledge
 * Sets up small world, camera, input handler and launches the world paused (forcing / implying step-by-step)
 *
 * @author dxw405 oxe410 nbg481
 */
public class LearningMode extends Mode {

	private SideBar sideBar;

	/**
	 * Constructs the world, sets up the camera, loads to worldmap and launches the world paused.
	 *
	 * @param game used to set up the world, contains directories to config files
	 */
	public LearningMode(MainGame game) {
		super(game,
				"world/world_smaller_test.tmx", 45f, 30f, null, null);

		SearchTicker currentSearch = world.getWorldGraph().getCurrentSearch();
		currentSearch.pause(SearchPauser.PLAY_PAUSE_BUTTON);
		currentSearch.setUpdated(true);
	}

	/**
	 * Adds the special {@link WorldSelectionHandler} which listens for mouse inputs and responds accordingly, taking
	 * into account the fact that this is learning mode
	 *
	 * @param inputHandler the current input handler
	 */
	@Override
	protected void registerFurtherInputProcessors(InputHandler inputHandler) {
		// world clicking
		inputHandler.addProcessor(new WorldSelectionHandler(world));
	}

	/**
	 * Sets up the sidebars (one with options on the left; one with nodes and step-by-step buttons on right; and help box on top)
	 */
	@Override
	protected void initSidebar() {
		super.initSidebar();
		sideBarNodes = new SideBarNodes(sideBarStage, world);

		sideBarNodes.setStepthrough(true);
		sideBarStage.addActor(sideBarNodes);

		sideBar = SideBar.createModeSidebar(ModeType.LEARNING, world, sideBarStage);
		sideBarStage.addActor(sideBar);
	}

	/**
	 * Renders the world, the three sidebars and updates the world's position and zoom depending on input from the user via the input listeners.
	 */
	@Override
	public void tick() {
		sideBar.render();
	}

	/**
	 * Instantiation that takes place _in_ the Mode constructor, after the world
	 * has been loaded
	 */
	@Override
	public void initialise() {
		//get the default starting algorithm from the config file
		SearchAlgorithm defaultAlg = Config.getAlgorithm(ConfigKey.DEFAULT_SEARCH_ALGORITHM, SearchAlgorithm.DEPTH_FIRST);

		WorldGraph worldGraph = world.getWorldGraph();
		worldGraph.setLearningModeNext(defaultAlg);
		Agent agent = world.spawnAgent(world.getTileSize().scl(0.5f));
		BehaviourMultiContinuousPathFind behaviour = new BehaviourMultiContinuousPathFind(
				agent, defaultAlg, worldGraph, world);
		agent.setBehaviour(behaviour);

		worldGraph.setCurrentSearch(agent, behaviour.getSearchTicker());
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
