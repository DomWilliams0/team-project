package com.b3.mode;

import com.b3.MainGame;
import com.b3.entity.Agent;
import com.b3.entity.ai.BehaviourMultiContinuousPathFind;
import com.b3.gui.popup.TutorialPopups;
import com.b3.gui.sidebars.SideBar;
import com.b3.gui.sidebars.SideBarNodes;
import com.b3.input.InputHandler;
import com.b3.input.TutorialModeSelectionHandler;
import com.b3.search.Point;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.badlogic.gdx.math.Vector2;

/**
 * A mode to introduce the user to common controls and functions of the application
 *
 * @author nbg481
 */
public class TutorialMode extends Mode {

	private SideBar sideBar;
	private final TutorialPopups tutorialPopups;

	/**
	 * Constructs the world, sets up the camera, loads to worldmap and launches the world paused.
	 *
	 * @param game used to set up the world, contains directories to config files
	 */
	public TutorialMode(MainGame game) {
		// create world
		super(game, "world/world_smaller_test_tiym.tmx", 45f, 25f, null, null);

		world.getWorldGraph().setLearningModeNext(SearchAlgorithm.A_STAR);
		world.getWorldGraph().getCurrentSearch().pause(1);
		world.getWorldGraph().getCurrentSearch().setUpdated(true);

		tutorialPopups = new TutorialPopups();
	}

	/**
	 * Adds the special {@link TutorialModeSelectionHandler} which allows specfic clicks disabled, to focus
	 * user to ONLY do as instructed.
	 *
	 * @param inputHandler the current input handler
	 */
	@Override
	protected void registerFurtherInputProcessors(InputHandler inputHandler) {
		// world clicking
		inputHandler.addProcessor(new TutorialModeSelectionHandler(world, this));
	}

	/**
	 * Sets up the sidebars (one with options on the left; one with nodes and step-by-step buttons on right; and help box on top)
	 */
	@Override
	protected void initSidebar() {
		super.initSidebar();
		sideBarNodes = new SideBarNodes(sideBarStage, world);

		sideBar = SideBar.createModeSidebar(ModeType.LEARNING, world, sideBarStage);
		sideBarStage.addActor(sideBar);
		sideBarStage.addActor(sideBarNodes);
	}

	/**
	 * Renders the world, the three sidebars and updates the world's position and zoom depending on input from the user via the input listeners.
	 */
	@Override
	protected void tick() {
		sideBar.render();

		tutorialPopups.render();
		tutorialPopups.checkTaskCompleted(world, sideBar, sideBarNodes);
	}

	@Override
	public void initialise() {
		WorldGraph worldGraph = world.getWorldGraph();
		Agent agent = world.spawnAgent(new Vector2(worldGraph.getWidth() / 2, worldGraph.getHeight() / 2));
		BehaviourMultiContinuousPathFind behaviour = new BehaviourMultiContinuousPathFind(
				agent, SearchAlgorithm.A_STAR, worldGraph, world);
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

	/**
	 * sets the current position of the mouse to the nearest node
	 *
	 * @param currentPos the node the user's mouse is closed to
	 */
	public void setCurrentPos(Point currentPos) {
		tutorialPopups.setCurrentPos(currentPos);
	}

	/**
	 * {@link com.b3.gui.PopupDescription} should only be shown after the tutorial mode has asked for the user to click
	 * on it
	 *
	 * @return true if the {@link com.b3.gui.PopupDescription} should be shown
	 */
	public boolean needPopups() {
		return (tutorialPopups.getCounter() >= 7);
	}

	/**
	 * Sets the current page of the currently open pop-up
	 *
	 * @param currentPage the current page of the currently open pop-up
	 */
	public void setCurrentPage(int currentPage) {
		tutorialPopups.setCurrentPage(currentPage);
	}
}
