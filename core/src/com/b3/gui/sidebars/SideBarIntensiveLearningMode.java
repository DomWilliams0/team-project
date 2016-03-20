package com.b3.gui.sidebars;

import com.b3.MainGame;
import com.b3.gui.sidebars.tabs.LearningModeSettingsTab;
import com.b3.world.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The sidebar used for intensive learning mode
 *
 * @author oxe410
 */
public class SideBarIntensiveLearningMode extends SideBar implements Disposable {

	private MainGame controller;

	/**
	 * Create a new sidebar for {@link com.b3.mode.LearningMode}
	 *
	 * @param stage the stage that this sidebar is contained in
	 * @param world the {@link World} that this sidebar is linked to
	 */
	public SideBarIntensiveLearningMode(Stage stage, World world) {
		super(stage, world, true, "window_03", 320, new LinkedHashMap<>());

		if (tabs != null) {
			// Add settings tab
			Map<String, Object> data = new HashMap<String, Object>() {{
				put("world", world);
			}};
			tabs.put("Settings", new LearningModeSettingsTab(skin, font, preferredWidth, this, data));
		}

		initComponents();
	}

	/**
	 * @param world the {@link World} to use with this sidebar
     */
	public void setWorld(World world) {
		this.world = world;
	}

	/**
	 * @return the {@link MainGame} controller that this sidebar uses for input handling
	 */
	public MainGame getController() {
		return controller;
	}

	/**
	 * Sets the controller used for input handling to this sidebar
	 * @param controller the new {@link MainGame} to set this sidebar tom
	 */
	public void setController(MainGame controller) {
		this.controller = controller;
	}

	/**
	 * @return the preferred width of this sidebar, if space allows it will take up this amount of space max
	 */
	@Override
	public float getPreferredWidth() {
		return preferredWidth;
	}

	/**
	 * Dispose of the sidebar cleanly
	 */
	@Override
	public void dispose() {
		controller.getInputHandler().clear();
		stage.dispose();
	}

}
