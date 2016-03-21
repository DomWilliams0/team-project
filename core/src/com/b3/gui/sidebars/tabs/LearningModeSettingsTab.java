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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.Collection;
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

		tab.add(showLabelsCheckBox.getComponent())
				.align(Align.left)
				.maxWidth(preferredWidth)
				.spaceBottom(10);
		tab.row();

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

		tab.add(soundsOn.getComponent())
				.align(Align.left)
				.maxWidth(preferredWidth)
				.spaceBottom(10);
		tab.row();

		// Pseudocode checkbox
		CheckBoxComponent pseudocodeOn = new CheckBoxComponent(skin, font, "Pseudocode");
		pseudocodeOn.getComponent().setChecked(false);
		pseudocodeOn.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SearchTicker currentSearch = world.getWorldGraph().getCurrentSearch();
				currentSearch.setInspectSearch(pseudocodeOn.getComponent().isChecked());
			}
		});

		tab.add(pseudocodeOn.getComponent())
				.align(Align.left)
				.maxWidth(preferredWidth)
				.spaceBottom(10);
		tab.row();

		// Add Building button
		ButtonComponent addBuildingMode = new ButtonComponent(skin, font, "Add Building Mode");
		addBuildingMode.addListener(new ChangeListener() {
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

		tab.add(addBuildingMode.getComponent())
				.align(Align.center)
				.maxWidth(preferredWidth)
				.spaceTop(20);
		tab.row();

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

		tab.add(removeBuildingButton.getComponent())
				.align(Align.center)
				.maxWidth(preferredWidth)
				.spaceTop(20);
		tab.row();

		//Play and Pause button
		ButtonComponent playPause = new ButtonComponent(skin, font, "Play");
		SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
		ticker.addObserver(playPause);

		playPause.setCallback(observable -> playPause.setText(ticker.isPaused() ? "Play" : "Pause"));

		playPause.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
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

		Object[] searches = SearchAlgorithm.allNames().toArray(); // TODO - Not badly done with Object[].
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

		// Search speed slider
		LabelComponent searchSpeedLabel = new LabelComponent(skin, "Search speed", Color.BLACK);
		tab.add(searchSpeedLabel.getComponent())
				.align(Align.left)
				.maxWidth(preferredWidth)
				.spaceTop(20);
		tab.row();

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

		tab.add(searchSpeedSlider.getComponent())
				.align(Align.center)
				.maxWidth(preferredWidth)
				.spaceTop(5);
		tab.row();

		// Game speed slider
		LabelComponent gameSpeedLabel = new LabelComponent(skin, "Game speed", Color.BLACK);
		tab.add(gameSpeedLabel.getComponent())
				.align(Align.left)
				.maxWidth(preferredWidth)
				.spaceTop(20);
		tab.row();

		SliderComponent gameSpeedSlider = new SliderComponent(skin, 0f, 4f, 0.1f);
		gameSpeedSlider.setValue(Config.getFloat(ConfigKey.GAME_SPEED));
		gameSpeedSlider.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Config.set(ConfigKey.GAME_SPEED, gameSpeedSlider.getValue());
			}
		});

		tab.add(gameSpeedSlider.getComponent())
				.align(Align.center)
				.maxWidth(preferredWidth)
				.spaceTop(5);
		tab.row();

		tab.add(labelNextSearch.getComponent())
				.align(Align.center)
				.maxWidth(preferredWidth)
				.spaceTop(90);
		tab.row();

		tab.add(searchSelectBox.getComponent())
				.maxWidth(preferredWidth)
				.spaceTop(10)
				.spaceBottom(30);
		tab.row();

		tab.add(playPause.getComponent())
				.align(Align.center)
				.maxWidth(preferredWidth)
				.spaceTop(5);
		tab.row();
	}
	
}
