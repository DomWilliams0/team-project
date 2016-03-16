package com.b3.mode;

import com.b3.MainGame;
import com.b3.entity.Agent;
import com.b3.entity.ai.BehaviourMultiContinuousPathFind;
import com.b3.gui.popup.TutorialPopups;
import com.b3.gui.sidebars.SideBarIntensiveLearningMode;
import com.b3.input.InputHandler;
import com.b3.input.TutorialModeSelectionHandler;
import com.b3.search.Point;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.badlogic.gdx.math.Vector2;

public class TutorialMode extends Mode {

	private SideBarIntensiveLearningMode sideBar;

	private final TutorialPopups tutorialPopups;

	public TutorialMode(MainGame game) {
		// create world
		super(ModeType.TUTORIAL, game, "core/assets/world/world_smaller_test_tiym.tmx", 26f);

		world.getWorldGraph().setLearningModeNext(SearchAlgorithm.A_STAR);
		world.getWorldGraph().getCurrentSearch().pause(1);
		world.getWorldGraph().getCurrentSearch().setUpdated(true);

		tutorialPopups = new TutorialPopups();
	}

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

		sideBar = new SideBarIntensiveLearningMode(sideBarStage, world);
		sideBar.setController(game);
		sideBarStage.addActor(sideBar);
	}

	/**
	 * Renders the world, the three sidebars and updates the world's position and zoom depending on input from the user via the input listeners.
	 *
	 */
	@Override
	protected void tick() {
		if (!world.getWorldGUI().getPseudoCode()) {
			sideBarNodes.resetPseudoCode();
			world.getWorldGUI().setPseudoCode(true);
		}
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

	public void setCurrentPos(Point currentPos) {
		tutorialPopups.setCurrentPos(currentPos);
	}

	public boolean needPopups() {
		return (tutorialPopups.getCounter() >= 7);
	}

	public void setCurrentPage(int currentPage) {
		tutorialPopups.setCurrentPage(currentPage);
	}
}
