package com.b3.gui.sidebars.tabs;

import com.b3.gui.components.ButtonComponent;
import com.b3.gui.components.CheckBoxComponent;
import com.b3.gui.components.LabelComponent;
import com.b3.gui.components.SliderComponent;
import com.b3.gui.sidebars.SideBar;
import com.b3.gui.sidebars.SideBarCompareMode;
import com.b3.input.SoundController;
import com.b3.search.SearchTicker;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.world.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;

import java.util.Map;
import java.util.function.Consumer;

public class CompareModeSettingsTab implements Tab {

	private Table settingsTab;

	public CompareModeSettingsTab(Skin skin, BitmapFont font, float preferredWidth, SideBar parent, Map<String, Object> data) {
		// Extract data
		World world = (World) data.get("world");
		SideBarCompareMode sidebar = (SideBarCompareMode) parent;

		settingsTab = new Table();
		settingsTab.setFillParent(true);
		settingsTab.pad(20);

		skin.add("default", font, BitmapFont.class);

		// Show grid checkbox
		createCheckbox(skin, font, settingsTab, "Show grid", ConfigKey.SHOW_GRID);

		// Flat buildings checkbox
		createCheckbox(skin, font, settingsTab, "Flat buildings", ConfigKey.FLATTEN_BUILDINGS,
				world::flattenBuildings);

		// Render static models checkbox
		createCheckbox(skin, font, settingsTab, "Static 3D objects", ConfigKey.RENDER_STATIC_MODELS,
				(visible) -> world.getModelManager().setStaticsVisible(visible));

		// im removing this toggle for now, as it requires a complex design decision
		//
		// we should remove the FLOCKING_ENABLED config flag, and have it iterate all
		// non-searching-agents and set them to invisible. unfortunately, models are
		// (for some reason) rendered outside of RenderSystem so this is currently
		// difficult to do without mangling everything

		// Flocking enable/disable
//        CheckBoxComponent showFlockingCheckBox = new CheckBoxComponent(skin, font, "Roaming civilians");
//        showFlockingCheckBox.getComponent().setChecked(Config.getBoolean(ConfigKey.FLOCKING_ENABLED));
//        showFlockingCheckBox.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Config.set(ConfigKey.FLOCKING_ENABLED, showFlockingCheckBox.getComponent().isChecked());
//            }
//        });
//
//        settingsTab.add(showFlockingCheckBox.getComponent())
//                .align(Align.left)
//                .maxWidth(preferredWidth)
//                .spaceBottom(10);
//        settingsTab.row();

		// Agent model rendering toggle
		createCheckbox(skin, font, settingsTab, "3D agents", ConfigKey.RENDER_AGENT_MODELS);

		// Show paths checkbox
		createCheckbox(skin, font, settingsTab, "Show paths", ConfigKey.SHOW_PATHS);

		// Search speed slider
		LabelComponent searchSpeedLabel = new LabelComponent(skin, "Search speed", Color.BLACK);
		settingsTab.add(searchSpeedLabel.getLabel())
				.align(Align.left)
				.maxWidth(preferredWidth)
				.spaceTop(20);
		settingsTab.row();

		SliderComponent searchSpeedSlider = new SliderComponent(skin,
				Config.getFloat(ConfigKey.TIME_BETWEEN_TICKS_MIN),
				Config.getFloat(ConfigKey.TIME_BETWEEN_TICKS_MAX),
				Config.getFloat(ConfigKey.TIME_BETWEEN_TICKS_STEP));
		searchSpeedSlider.setValue(searchSpeedSlider.getSlider().getMaxValue() - Config.getFloat(ConfigKey.TIME_BETWEEN_TICKS));
		searchSpeedSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Config.set(ConfigKey.TIME_BETWEEN_TICKS, searchSpeedSlider.getSlider().getMaxValue() - searchSpeedSlider.getValue());
			}
		});

		settingsTab.add(searchSpeedSlider.getSlider())
				.align(Align.center)
				.maxWidth(preferredWidth)
				.spaceTop(5);
		settingsTab.row();

		// Game speed slider
		LabelComponent gameSpeedLabel = new LabelComponent(skin, "Game speed", Color.BLACK);
		settingsTab.add(gameSpeedLabel.getLabel())
				.align(Align.left)
				.maxWidth(preferredWidth)
				.spaceTop(20);
		settingsTab.row();

		SliderComponent gameSpeedSlider = new SliderComponent(skin, 0f, 4f, 0.1f);
		gameSpeedSlider.setValue(Config.getFloat(ConfigKey.GAME_SPEED));
		gameSpeedSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Config.set(ConfigKey.GAME_SPEED, gameSpeedSlider.getValue());
			}
		});

		settingsTab.add(gameSpeedSlider.getSlider())
				.align(Align.center)
				.maxWidth(preferredWidth)
				.spaceTop(5);
		settingsTab.row();

		// Add Building button
		ButtonComponent addBuildingMode = new ButtonComponent(skin, font, "Add Building Mode");
		addBuildingMode.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				sidebar.close();

				if (!world.getWorldGraph().getCurrentSearch().isTickedOnce()) {
					Boolean currentBoolean = Config.getBoolean(ConfigKey.ADD_BUILDING_MODE);
					Config.set(ConfigKey.ADD_BUILDING_MODE, !currentBoolean);
					System.out.println("Add building mode is " + !currentBoolean);
				} else {
					world.getWorldGUI().getPopupManager().showBuildingError();
					SoundController.playSounds(2);
					System.err.println("Search has begun cannot add");
				}
			}
		});

		settingsTab.add(addBuildingMode.getComponent())
				.align(Align.center)
				.maxWidth(preferredWidth)
				.spaceTop(20);
		settingsTab.row();

		// Remove Building button
		ButtonComponent removeBuildingButton = new ButtonComponent(skin, font, "Remove Building");
		removeBuildingButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				sidebar.close();

				if (!world.getWorldGraph().getCurrentSearch().isTickedOnce()) {
					Boolean currentBoolean = Config.getBoolean(ConfigKey.REMOVE_BUILDING_MODE);
					Config.set(ConfigKey.REMOVE_BUILDING_MODE, !currentBoolean);
					System.out.println("Remove building mode is " + !currentBoolean);
				} else {
					world.getWorldGUI().getPopupManager().showBuildingError();
					SoundController.playSounds(2);
					System.err.println("Search has begun cannot add");
				}
			}
		});

		settingsTab.add(removeBuildingButton.getComponent())
				.align(Align.center)
				.maxWidth(preferredWidth)
				.spaceTop(20);
		settingsTab.row();

		//Play and Pause button
		//String btnText = world.getWorldGraph().getCurrentSearch().isPaused(1) ? "Play" : "Pause";
		ButtonComponent playPause = new ButtonComponent(skin, font, "Pause");
		playPause.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				TextButton btnplaypause = playPause.getComponent();
				String text = btnplaypause.getText().toString();
				SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
				if (text.equals("Pause")) {
					ticker.pause(1);
					ticker.setUpdated(true);
					btnplaypause.setText("Play");
				} else if (text.equals("Play")) {
					ticker.resume(1);
					btnplaypause.setText("Pause");
				}
			}
		});

		settingsTab.add(playPause.getComponent())
				.align(Align.center)
				.maxWidth(preferredWidth)
				.spaceTop(5);
		settingsTab.row();
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
				//.maxWidth(preferredWidth)
				.spaceBottom(10);
		table.row();
	}

	@Override
	public Table getTab() {
		return settingsTab;
	}
}
