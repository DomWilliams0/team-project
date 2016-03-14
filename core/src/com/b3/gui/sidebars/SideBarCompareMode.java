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

public class SideBarCompareMode extends SideBar implements Disposable {

    private ButtonComponent playPause;
    private MainGame controller;

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

	private void createCheckbox(Skin skin, BitmapFont font, Table table, String label, ConfigKey configKey) {
		createCheckbox(skin, font, table, label, configKey, null);
	}
	
	private void createCheckbox(Skin skin, BitmapFont font, Table table, String label, ConfigKey configKey,
								Consumer<Boolean> checkedListener) {
		CheckBoxComponent checkBox = new CheckBoxComponent(skin, font, label);
		checkBox.getComponent().setChecked(Config.getBoolean(configKey));
		checkBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				boolean checked = checkBox.getComponent().isChecked();
				Config.set(configKey, checked);
				if (checkedListener != null)
					checkedListener.accept(checked);
			}
		});
		
		table.add(checkBox.getComponent())
				.align(Align.left)
				.maxWidth(preferredWidth)
				.spaceBottom(10);
		table.row();
	}

    public void setWorld(World world) {
        this.world = world;
    }

    public void updatePlayPauseButton() {
        playPause.setText("Play");
    }

    public MainGame getController() {
        return controller;
    }

    public void setController(MainGame controller) {
        this.controller = controller;
    }

    @Override
    public void dispose() {
        controller.getInputHandler().clear();
        stage.dispose();
    }
}