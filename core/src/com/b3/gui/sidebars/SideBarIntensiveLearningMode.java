package com.b3.gui.sidebars;

import com.b3.MainGame;
import com.b3.gui.sidebars.tabs.LearningModeSettingsTab;
import com.b3.world.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class SideBarIntensiveLearningMode extends SideBar implements Disposable {

    private MainGame controller;

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

    public void setWorld(World world) {
        this.world = world;
    }

    public MainGame getController() {
        return controller;
    }

    public void setController(MainGame controller) {
        this.controller = controller;
    }

    @Override
    public float getPreferredWidth() {
        return preferredWidth;
    }

    @Override
    public void dispose() {
        controller.getInputHandler().clear();
        stage.dispose();
    }

}
