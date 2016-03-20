package com.b3.mode;

import com.b3.MainGame;
import com.b3.entity.Agent;
import com.b3.entity.ai.BehaviourMultiContinuousPathFind;
import com.b3.gui.sidebars.SideBar;
import com.b3.gui.sidebars.SideBarNodes;
import com.b3.input.InputHandler;
import com.b3.input.PracticeModeWorldSelectionHandler;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Utils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * A mode to allow the user to practice what they've learnt by choosing what should be added to the frontier and
 * visited themselves
 */
public class PracticeMode extends Mode {

	private Stage popupStage;

	/**
	 * Constructs the world, sets up the camera, loads to worldmap and launches the world paused.
	 *
	 * @param game used to set up the world, contains directories to config files
	 */
	public PracticeMode(MainGame game) {
		// create world
		super(game, "world/world_smaller_test_tiym.tmx", 45f, 20f, null, null);

		WorldGraph worldGraph = world.getWorldGraph();
		worldGraph.setLearningModeNext(SearchAlgorithm.DEPTH_FIRST);
		worldGraph.getCurrentSearch().pause(1);
		worldGraph.getCurrentSearch().setUpdated(true);
	}

	/**
	 * Adds the special {@link PracticeModeWorldSelectionHandler} which disables pop-up and other unnecessary controls
	 * and functions, allowing the user to concentrate on Practice Mode.
	 *
	 * @param inputHandler the current input handler
	 */
	@Override
	protected void registerFurtherInputProcessors(InputHandler inputHandler) {
		// popup clicking
		inputHandler.addProcessor(popupStage);

		// world clicking
		inputHandler.addProcessor(new PracticeModeWorldSelectionHandler(world, popupStage));
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


		sideBarStage.addActor(SideBar.createModeSidebar(ModeType.PRACTICE, world, sideBarStage));

		popupStage = new Stage(new ScreenViewport());
	}

	/**
	 * Renders the world, the three sidebars and updates the world's position and zoom depending on input from the user via the input listeners.
	 */
	@Override
	protected void tick() {
		popupStage.act(Utils.TRUE_DELTA_TIME);
		popupStage.draw();

	}

	@Override
	public void initialise() {
		WorldGraph worldGraph = world.getWorldGraph();
		Agent agent = world.spawnAgent(new Vector2(worldGraph.getWidth() / 2, worldGraph.getHeight() / 2));
		BehaviourMultiContinuousPathFind behaviour = new BehaviourMultiContinuousPathFind(
				agent, SearchAlgorithm.DEPTH_FIRST, worldGraph, world);
		agent.setBehaviour(behaviour);

		worldGraph.setCurrentSearch(agent, behaviour.getSearchTicker());
		SearchTicker.setInspectSearch(true);
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
		popupStage.getViewport().update(width, height, true);
	}
}
