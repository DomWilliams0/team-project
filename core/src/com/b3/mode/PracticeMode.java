package com.b3.mode;

import com.b3.MainGame;
import com.b3.entity.Agent;
import com.b3.entity.ai.BehaviourMultiContinuousPathFind;
import com.b3.gui.help.HelpBox;
import com.b3.input.InputHandler;
import com.b3.input.PracticeModeWorldSelectionHandler;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Utils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class PracticeMode extends Mode {

	private Stage popupStage;

	public PracticeMode(MainGame game) {
		// create world
		super(ModeType.PRACTICE, game, "core/assets/world/world_smaller_test_tiym.tmx", 45f, 20f, null, null);

		world.getWorldGUI().setPseudoCode(false);
		WorldGraph worldGraph = world.getWorldGraph();
		worldGraph.setLearningModeNext(SearchAlgorithm.DEPTH_FIRST);
		worldGraph.getCurrentSearch().pause(1);
		worldGraph.getCurrentSearch().setUpdated(true);

		// Display first popup
//        MessageBoxComponent descriptionPopup = new MessageBoxComponent(popupStage,
//                "Welcome to the 'Try it yourself' mode.\n" +
//                        "Here you can practice what you have learned in the 'Learning mode'.\n" +
//                        "Currently you can interact using DFS.\n" +
//                        "Now please click on the node to be expanded next.",
//                "OK");
//        descriptionPopup.show();
	}


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
		popupStage = new Stage(new ScreenViewport());

		HelpBox helpBox = new HelpBox(sideBarStage);
		sideBarStage.addActor(helpBox);
	}

	/**
	 * Renders the world, the three sidebars and updates the world's position and zoom depending on input from the user via the input listeners.
	 *
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
