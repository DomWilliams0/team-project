package com.b3.gui.sidebars.tabs;

import com.b3.gui.GuiUtils;
import com.b3.gui.components.ButtonComponent;
import com.b3.gui.components.LabelComponent;
import com.b3.gui.components.SliderComponent;
import com.b3.gui.sidebars.SideBar;
import com.b3.search.SearchPauser;
import com.b3.search.SearchTicker;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.world.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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

		skin.add("default", font, BitmapFont.class);

		// Show grid checkbox
		GuiUtils.createCheckbox(skin, font, tab, "Show grid", ConfigKey.SHOW_GRID, preferredWidth);

		// Render static models checkbox
		GuiUtils.createCheckbox(skin, font, tab, "Static 3D objects", ConfigKey.RENDER_STATIC_MODELS,
				(visible) -> world.getModelManager().setStaticsVisible(visible), preferredWidth);

		// Agent model rendering toggle
		GuiUtils.createCheckbox(skin, font, tab, "3D agents", ConfigKey.RENDER_AGENT_MODELS, preferredWidth);

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
								searchTicker.pause(SearchPauser.PLAY_PAUSE_BUTTON);
								searchTicker.setUpdated(true);
							});
					btnplaypause.setText("Play");

				} else if (text.equals("Play")) {
					searches
							.stream()
							.forEach(searchTicker -> searchTicker.resume(SearchPauser.PLAY_PAUSE_BUTTON));
					btnplaypause.setText("Pause");
				}
			}
		});

		addComponent(searchSpeedLabel, Align.left, preferredWidth, 20, 0, 0, 0);
		addComponent(searchSpeedSlider, Align.center, preferredWidth, 5, 0, 0, 0);
		addComponent(gameSpeedLabel, Align.left, preferredWidth, 20, 0, 0, 0);
		addComponent(gameSpeedSlider, Align.center, preferredWidth, 5, 0, 0, 0);
		addComponent(playPause, Align.center, preferredWidth, 5, 0, 0, 0);
	}
	
}
