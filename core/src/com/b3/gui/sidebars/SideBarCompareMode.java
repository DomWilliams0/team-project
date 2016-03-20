package com.b3.gui.sidebars;

import com.b3.MainGame;
import com.b3.gui.components.ButtonComponent;
import com.b3.gui.components.CheckBoxComponent;
import com.b3.gui.sidebars.tabs.CompareModeSettingsTab;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.world.World;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Creates a sidebar for use in compare mode
 */
public class SideBarCompareMode extends SideBar implements Disposable {

	private MainGame controller;

	/**
	 * Constructs a new sidebar for use in comparemode
	 *
	 * @param stage the stage that this sidebar is contained in
	 * @param world the {@link World} that this sidebar is linked to
     */
	public SideBarCompareMode(Stage stage, World world) {
		super(stage, world, true, "window_03", 230, new LinkedHashMap<>());

		if (tabs != null) {
			// Add nodes tab
			Map<String, Object> data = new HashMap<String, Object>() {{
				put("world", world);
			}};
			tabs.put("Settings", new CompareModeSettingsTab(skin, font, preferredWidth, this, data));
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
	 * Dispose of the sidebar cleanly
	 */
	@Override
	public void dispose() {
		controller.getInputHandler().clear();
		stage.dispose();
	}
}
