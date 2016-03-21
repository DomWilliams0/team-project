package com.b3.gui.sidebars.tabs;

import com.b3.gui.components.LabelComponent;
import com.b3.gui.components.SelectBoxComponent;
import com.b3.gui.components.SliderComponent;
import com.b3.gui.sidebars.SideBar;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.world.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.Map;

/**
 * Represents the practice mode settings tab
 *
 * @author oxe410
 */
public class PracticeModeSettingsTab extends Tab {

	/**
	 * @param skin           The libGDX skin
	 * @param font           The font to apply
	 * @param preferredWidth The tab width
	 * @param parent         The {@link SideBar} which contains this tab
	 * @param data           Additional data
	 */
	public PracticeModeSettingsTab(Skin skin, BitmapFont font, float preferredWidth, SideBar parent, Map<String, Object> data) {
		super(skin, font, preferredWidth, parent, data);

		// ======================
		// === LABELS AND STU ===
		// ======================

		skin.add("default", font, BitmapFont.class);
		//new LabelComponent("", 16, "", Color.BLACK)
		LabelComponent labelOne = new LabelComponent(skin, "Left click the nodes to see more information", Color.BLACK);
		labelOne.getComponent().setPosition(-20, Gdx.graphics.getHeight() / 2);

		LabelComponent labelOneUnderneath = new LabelComponent(skin, "Click again on the same node to see more info", Color.BLACK);
		labelOneUnderneath.getComponent().setPosition(-20, Gdx.graphics.getHeight() / 2);

		LabelComponent labelTwo = new LabelComponent(skin, "Right click a node to set the next destination", Color.BLACK);
		labelTwo.getComponent().setPosition(-20, Gdx.graphics.getHeight() / 2);

		// ========================
		// === WHAT SEARCH ALGO ===
		// ========================

		Object[] searches = SearchAlgorithm
				.allNames()
				.stream()
				.filter(name -> SearchAlgorithm.fromName(name) == SearchAlgorithm.DEPTH_FIRST || SearchAlgorithm.fromName(name) == SearchAlgorithm.BREADTH_FIRST)
				.toArray();
		SelectBoxComponent searchSelectBox = new SelectBoxComponent(skin, font, new Array(searches));
		searchSelectBox.setSelected(searches[0]);

		searchSelectBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				SearchAlgorithm algorithm = SearchAlgorithm.fromName((String) searchSelectBox.getSelected());
				if (algorithm == null) return;
				((World) data.get("world")).getWorldGraph().setLearningModeNext(algorithm);
			}
		});

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

		tab.add(searchSelectBox.getComponent())
				.maxWidth(preferredWidth)
				.spaceTop(100)
				.spaceBottom(30);
		tab.row();

		tab.add(labelOne.getComponent())
				.align(Align.center)
				.maxWidth(preferredWidth)
				.spaceTop(5);
		tab.row();

		tab.add(labelOneUnderneath.getComponent())
				.align(Align.center)
				.maxWidth(preferredWidth)
				.spaceTop(5);
		tab.row();

		tab.add(labelTwo.getComponent())
				.align(Align.center)
				.maxWidth(preferredWidth)
				.spaceTop(5);
		tab.row();
	}

	/**
	 * Sets the name of the tab
	 *
	 * @param name The name
	 */
	public void setName(String name) {
		tab.setName(name);
	}
	
}
