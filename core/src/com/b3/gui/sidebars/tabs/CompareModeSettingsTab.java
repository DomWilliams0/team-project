package com.b3.gui.sidebars.tabs;

import com.b3.gui.GuiUtils;
import com.b3.gui.components.ButtonComponent;
import com.b3.gui.components.LabelComponent;
import com.b3.gui.components.SliderComponent;
import com.b3.gui.sidebars.SideBar;
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

import java.util.Collection;
import java.util.Map;

/**
 * Represents the compare mode settings tab
 *
 * @author oxe410
 */
public class CompareModeSettingsTab extends Tab {

	/**
<<<<<<< Updated upstream
=======
	 * Creates the compare mode settings tab
	 *
>>>>>>> Stashed changes
	 * @param skin           The libGDX skin
	 * @param font           The font to apply
	 * @param preferredWidth The tab width
	 * @param parent         The {@link SideBar} which contains this tab
	 * @param data           Additional data
	 */
	public CompareModeSettingsTab(Skin skin, BitmapFont font, float preferredWidth, SideBar parent, Map<String, Object> data) {
		super(skin, font, preferredWidth, parent, data);

		// Extract data
		World world = (World) data.get("world");

		tab.setFillParent(true);
		tab.pad(20);

		skin.add("default", font, BitmapFont.class);

		// Show grid checkbox
		GuiUtils.createCheckbox(skin, font, tab, "Show grid", ConfigKey.SHOW_GRID, preferredWidth);

		// Flat buildings checkbox
		GuiUtils.createCheckbox(skin, font, tab, "Flat buildings", ConfigKey.FLATTEN_BUILDINGS,
				world::flattenBuildings, preferredWidth);

		// Render static models checkbox
		GuiUtils.createCheckbox(skin, font, tab, "Static 3D objects", ConfigKey.RENDER_STATIC_MODELS,
				(visible) -> world.getModelManager().setStaticsVisible(visible), preferredWidth);

		// Agent model rendering toggle
		GuiUtils.createCheckbox(skin, font, tab, "3D agents", ConfigKey.RENDER_AGENT_MODELS, preferredWidth);

		// Show paths checkbox
//		GuiUtils.createCheckbox(skin, font, tab, "Show paths", ConfigKey.SHOW_PATHS, preferredWidth);

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
		ButtonComponent removeBuildingButton = new ButtonComponent(skin, font, "Remove Building");
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
		//String btnText = world.getWorldGraph().getCurrentSearch().isPaused(1) ? "Play" : "Pause";
		ButtonComponent playPause = new ButtonComponent(skin, font, "Pause");
		playPause.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				TextButton btnplaypause = playPause.getComponent();
				String text = btnplaypause.getText().toString();
				Collection<SearchTicker> searches = world.getWorldGraph().getAllSearches();

				if (text.equals("Pause")) {
					searches
							.stream()
							.forEach(searchTicker -> {
								searchTicker.pause(1);
								searchTicker.setUpdated(true);
							});
					btnplaypause.setText("Play");

				} else if (text.equals("Play")) {
					searches
							.stream()
							.forEach(searchTicker -> searchTicker.resume(1));
					btnplaypause.setText("Pause");
				}
			}
		});

		tab.add(playPause.getComponent())
				.align(Align.center)
				.maxWidth(preferredWidth)
				.spaceTop(5);
		tab.row();
	}
}
