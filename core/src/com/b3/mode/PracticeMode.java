package com.b3.mode;

import com.b3.MainGame;
import com.b3.entity.Agent;
import com.b3.entity.ai.BehaviourMultiContinuousPathFind;
import com.b3.gui.sidebars.tabs.PracticeModeSettingsTab;
import com.b3.input.InputHandler;
import com.b3.input.PracticeModeWorldSelectionHandler;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.HashMap;
import java.util.Map;

public class PracticeMode extends Mode {

	private Stage popupStage;

	public PracticeMode(MainGame game) {
		// create world
		super(ModeType.PRACTICE, game, "core/assets/world/world_smaller_test_tiym.tmx", 26f);

		world.setPseudoCode(false);
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

		// INITIALISE SKIN AND FONT
		// ------------------------
		TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(Config.getString(ConfigKey.TEXTURE_ATLAS)));
		Skin skin = new Skin(atlas);
		BitmapFont font = Utils.getFont(Config.getString(ConfigKey.FONT_FILE), 16);

		// SIDEBAR NODES
		// -------------

		// Get world and controller (for settings tab)
		Map<String, Object> data = new HashMap<String, Object>() {{
			put("world", world);
			put("controller", game);
		}};

        // Add settings tab
        PracticeModeSettingsTab settingsTab = new PracticeModeSettingsTab(skin, font, sideBarNodes.getPreferredWidth(), data);
        settingsTab.setName("Settings");

		sideBarNodes.addTab(settingsTab.getTab());
	}

	/**
	 * Renders the world, the three sidebars and updates the world's position and zoom depending on input from the user via the input listeners.
	 *
	 * @param delta Delta time since last frame
	 */
	@Override
	protected void tick(float delta) {
		popupStage.act(Utils.TRUE_DELTA_TIME);
		popupStage.draw();
	}

	@Override
	protected void spawnInitialEntities() {
		WorldGraph worldGraph = world.getWorldGraph();
		Agent agent = world.spawnAgent(new Vector2(worldGraph.getMaxXValue() / 2, worldGraph.getMaxYValue() / 2));
		BehaviourMultiContinuousPathFind behaviour = new BehaviourMultiContinuousPathFind(
				agent, SearchAlgorithm.DEPTH_FIRST, worldGraph, camera, world);
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
