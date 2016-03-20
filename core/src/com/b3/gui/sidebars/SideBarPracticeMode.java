package com.b3.gui.sidebars;

import com.b3.MainGame;
import com.b3.gui.sidebars.tabs.PracticeModeSettingsTab;
import com.b3.gui.sidebars.tabs.Tab;
import com.b3.world.World;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SideBarPracticeMode extends SideBar {

	private MainGame controller;

	public SideBarPracticeMode(Stage stage, World world, MainGame controller, float preferredWidth) {
		super(stage, world, true, "window_02", preferredWidth, new LinkedHashMap<>());

		this.controller = controller;

		// Add settings tab
		Map<String, Object> data = new HashMap<String, Object>() {{
			put("world", world);
			put("controller", controller);
		}};

		tabs.put("Settings", new PracticeModeSettingsTab(skin, font, preferredWidth, data));

		initComponents();
	}

	public MainGame getController() {
		return controller;
	}

	public void setController(MainGame controller) {
		this.controller = controller;
	}

}
