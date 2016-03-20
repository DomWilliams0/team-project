package com.b3.gui.sidebars;

import com.b3.MainGame;
import com.b3.gui.sidebars.tabs.PracticeModeSettingsTab;
import com.b3.gui.sidebars.tabs.Tab;
import com.b3.world.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Creates a sidebar for use in practice mode
 */
public class SideBarPracticeMode extends SideBar {

	private MainGame controller;

	/**
 	 * Constructs a new sidebar for use in practice mode
 	 *
 	 * @param stage the stage that this sidebar is contained in
	 * @param world the {@link World} that this sidebar is linked to
	 * @param controller the {@link MainGame} that is used for input handling on this sidebar
	 * @param preferredWidth the preferred width of this sidebar, if space allows it will take up this amount of space max
 	*/
	public SideBarPracticeMode(Stage stage, World world, MainGame controller, float preferredWidth) {
		super(stage, world, true, "window_02", preferredWidth, new LinkedHashMap<>());

		this.controller = controller;

		// Add settings tab
		Map<String, Object> data = new HashMap<String, Object>() {{
			put("world", world);
			put("controller", controller);
		}};

		tabs.put("Settings", new PracticeModeSettingsTab(skin, font, preferredWidth, null, data));

		initComponents();

	}

	/**
	 * @return the {@link MainGame} that input handles on this sidebar
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

}
