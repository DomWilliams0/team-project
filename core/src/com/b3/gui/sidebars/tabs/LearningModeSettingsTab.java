package com.b3.gui.sidebars.tabs;

import com.b3.gui.components.*;
import com.b3.gui.sidebars.SideBar;
import com.b3.input.SoundController;
import com.b3.search.SearchPauser;
import com.b3.search.SearchTicker;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.world.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.Arrays;
import java.util.Map;

/**
 * Represents the learning mode settings tab
 *
 * @author oxe410
 */
public class LearningModeSettingsTab extends Tab {

	/**
	 * @param skin           The libGDX skin
	 * @param font           The font to apply
	 * @param preferredWidth The tab width
	 * @param parent         The {@link SideBar} which contains this tab
	 * @param data           Additional data
	 */
	public LearningModeSettingsTab(Skin skin, BitmapFont font, float preferredWidth, SideBar parent, Map<String, Object> data) {
		super(skin, font, preferredWidth, parent, data);

		// Extract data
		World world = (World) data.get("world");

		skin.add("default", font, BitmapFont.class);

		// Flat buildings checkbox
		CheckBoxComponent showLabelsCheckBox = new CheckBoxComponent(skin, font, "Flat buildings");
		showLabelsCheckBox.getComponent().setChecked(Config.getBoolean(ConfigKey.FLATTEN_BUILDINGS));
		showLabelsCheckBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				boolean flatBuildings = Config.getBoolean(ConfigKey.FLATTEN_BUILDINGS);
				world.flattenBuildings(!flatBuildings);
				Config.set(ConfigKey.FLATTEN_BUILDINGS, !flatBuildings);
			}
		});

		// Search sounds checkbox
		CheckBoxComponent soundsOn = new CheckBoxComponent(skin, font, "Search Sounds");
		soundsOn.getComponent().setChecked(Config.getBoolean(ConfigKey.SOUNDS_ON));
		soundsOn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SoundController.stopSound(3);
				boolean soundsOn = Config.getBoolean(ConfigKey.SOUNDS_ON);
				Config.set(ConfigKey.SOUNDS_ON, !soundsOn);
			}
		});

		// Pseudocode checkbox
		CheckBoxComponent pseudocodeOn = new CheckBoxComponent(skin, font, "Pseudocode");
		pseudocodeOn.getComponent().setChecked(false);
		pseudocodeOn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SearchTicker.setInspectSearch(pseudocodeOn.getComponent().isChecked());
			}
		});

		// Add Building button
		ButtonComponent addBuildingButton = new ButtonComponent(skin, font, "Add Building Mode");
		addBuildingButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				parent.close();

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

		// Remove Building button
		ButtonComponent removeBuildingButton = new ButtonComponent(skin, font, "Remove Building Mode");
		removeBuildingButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				parent.close();

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

		// Play and Pause button
		ButtonComponent playPause = new ButtonComponent(skin, font, "Play");
		SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
		ticker.addObserver(playPause);

		playPause.setCallback(observable -> playPause.setText(ticker.isPaused() ? "Play" : "Pause"));

		playPause.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				if (ticker.isPaused()) {
					ticker.resume(SearchPauser.PLAY_PAUSE_BUTTON);
				} else {
					ticker.pause(SearchPauser.PLAY_PAUSE_BUTTON);
					ticker.setUpdated(true);
				}
			}
		});

		// ========================
		// === WHAT SEARCH ALGO ===
		// ========================

		LabelComponent labelNextSearch = new LabelComponent(skin, "The next search will use:", Color.BLACK);

		Object[] searches = Arrays.copyOfRange(SearchAlgorithm.allNames().toArray(), 0, SearchAlgorithm.allNames().size() - 1);
		SelectBoxComponent searchSelectBox = new SelectBoxComponent(skin, font, new Array(searches));
		searchSelectBox.setSelected(searches[0]);

		searchSelectBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SearchAlgorithm algorithm = SearchAlgorithm.fromName((String) searchSelectBox.getSelected());
				if (algorithm == null) return;
				world.getWorldGraph().setLearningModeNext(algorithm);
			}
		});
		//ensure the box is showing the default algorithm selected
		searchSelectBox.setSelected(world.getWorldGraph().getCurrentSearch().getAlgorithm().getName());

		// Search speed slider
		LabelComponent searchSpeedLabel = new LabelComponent(skin, "Search speed", Color.BLACK);

		SliderComponent searchSpeedSlider = new SliderComponent(skin,
				Config.getFloat(ConfigKey.TIME_BETWEEN_TICKS_MIN),
				Config.getFloat(ConfigKey.TIME_BETWEEN_TICKS_MAX),
				Config.getFloat(ConfigKey.TIME_BETWEEN_TICKS_STEP));
		searchSpeedSlider.setValue(searchSpeedSlider.getComponent().getMaxValue() - Config.getFloat(ConfigKey.TIME_BETWEEN_TICKS));
		searchSpeedSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Config.set(ConfigKey.TIME_BETWEEN_TICKS, searchSpeedSlider.getComponent().getMaxValue() - searchSpeedSlider.getValue());
			}
		});

		// Game speed slider
		LabelComponent gameSpeedLabel = new LabelComponent(skin, "Game speed", Color.BLACK);

		SliderComponent gameSpeedSlider = new SliderComponent(skin, 0f, 4f, 0.1f);
		gameSpeedSlider.setValue(Config.getFloat(ConfigKey.GAME_SPEED));
		gameSpeedSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Config.set(ConfigKey.GAME_SPEED, gameSpeedSlider.getValue());
			}
		});

		addComponent(showLabelsCheckBox, Align.left, preferredWidth, 0, 0, 10, 0);
		addComponent(soundsOn, Align.left, preferredWidth, 0, 0, 10, 0);
		addComponent(pseudocodeOn, Align.left, preferredWidth, 0, 0, 10, 0);
		addComponent(addBuildingButton, Align.center, preferredWidth, 20, 0, 0, 0);
		addComponent(removeBuildingButton, Align.center, preferredWidth, 20, 0, 0, 0);
		addComponent(searchSpeedLabel, Align.left, preferredWidth, 20, 0, 0, 0);
		addComponent(searchSpeedSlider, Align.center, preferredWidth, 5, 0, 0, 0);
		addComponent(gameSpeedLabel, Align.left, preferredWidth, 20, 0, 0, 0);
		addComponent(gameSpeedSlider, Align.center, preferredWidth, 5, 0, 0, 0);
		addComponent(labelNextSearch, Align.center, preferredWidth, 90, 0, 0, 0);
		addComponent(searchSelectBox, Align.center, preferredWidth, 20, 0, 0, 0);
		addComponent(playPause, Align.center, preferredWidth, 5, 0, 0, 0);
	}

}
