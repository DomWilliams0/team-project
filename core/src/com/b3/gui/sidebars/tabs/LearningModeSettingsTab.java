package com.b3.gui.sidebars.tabs;

import com.b3.MainGame;
import com.b3.gui.components.*;
import com.b3.gui.sidebars.SideBar;
import com.b3.gui.sidebars.SideBarIntensiveLearningMode;
import com.b3.input.SoundController;
import com.b3.mode.MainMenuScreen;
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

import java.util.Map;

public class LearningModeSettingsTab implements Tab {

    private Table settingsTab;
    private SideBar parent;

    public LearningModeSettingsTab(Skin skin, BitmapFont font, float preferredWidth, SideBar parent, Map<String, Object> data) {
        this.parent = parent;

        // Extract data
        World world = (World) data.get("world");
        SideBarIntensiveLearningMode sidebar = (SideBarIntensiveLearningMode) parent;

        settingsTab = new Table();
        settingsTab.setFillParent(true);
        settingsTab.pad(20);

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

        settingsTab.add(showLabelsCheckBox.getComponent())
                .align(Align.left)
                .maxWidth(preferredWidth)
                .spaceBottom(10);
        settingsTab.row();

        // Flat buildings checkbox
        CheckBoxComponent soundsOn = new CheckBoxComponent(skin, font, "Search Sounds");
        soundsOn.getComponent().setChecked(Config.getBoolean(ConfigKey.SOUNDS_ON));
        soundsOn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean soundsOn = Config.getBoolean(ConfigKey.SOUNDS_ON);
                Config.set(ConfigKey.SOUNDS_ON, !soundsOn);
            }
        });

        settingsTab.add(soundsOn.getComponent())
                .align(Align.left)
                .maxWidth(preferredWidth)
                .spaceBottom(10);
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
                    world.getPopupManager().showBuildingError();
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
        ButtonComponent removeBuildingButton = new ButtonComponent(skin, font, "Remove Building Mode");
        removeBuildingButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                sidebar.close();

                if (!world.getWorldGraph().getCurrentSearch().isTickedOnce()) {
                    Boolean currentBoolean = Config.getBoolean(ConfigKey.REMOVE_BUILDING_MODE);
                    Config.set(ConfigKey.REMOVE_BUILDING_MODE, !currentBoolean);
                    System.out.println("Remove building mode is " + !currentBoolean);
                } else {
                    world.getPopupManager().showBuildingError();
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
        ButtonComponent playPause = new ButtonComponent(skin, font, "Play");
        SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
        ticker.addObserver(playPause);

        playPause.setUpdateListener(observable -> {
            //SearchTicker searchTicker = (SearchTicker) observable;
            playPause.setText(ticker.isPaused() ? "Play" : "Pause");
            return null;
        });

        playPause.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
                if (ticker.isPaused()) {
                    ticker.resume(1);
                }
                else {
                    ticker.pause(1);
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

        settingsTab.add(labelNextSearch.getComponent())
                .align(Align.center)
                .maxWidth(preferredWidth)
                .spaceTop(90);
        settingsTab.row();

        settingsTab.add(searchSelectBox.getSelectBox())
                .maxWidth(preferredWidth)
                .spaceTop(10)
                .spaceBottom(30);
        settingsTab.row();

        settingsTab.add(playPause.getComponent())
                .align(Align.center)
                .maxWidth(preferredWidth)
                .spaceTop(5);
        settingsTab.row();

        // ====================
        // === BACK TO MENU ===
        // ====================

        ButtonComponent backToMenuBtn = new ButtonComponent(skin, font, "Main menu");
        backToMenuBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MainGame controller = sidebar.getController();

                sidebar.dispose();
                controller.setScreen(new MainMenuScreen(controller));
            }
        });

        settingsTab.add(backToMenuBtn.getComponent())
                .align(Align.center)
                .maxWidth(preferredWidth)
                .spaceTop(25);
        settingsTab.row();
    }

    @Override
    public Table getTab() {
        return settingsTab;
    }
}
