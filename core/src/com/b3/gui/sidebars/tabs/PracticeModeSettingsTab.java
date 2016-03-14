package com.b3.gui.sidebars.tabs;

import com.b3.MainGame;
import com.b3.gui.sidebars.SideBar;
import com.b3.gui.sidebars.SideBarPracticeMode;
import com.b3.mode.MainMenuScreen;
import com.b3.gui.components.ButtonComponent;
import com.b3.gui.components.LabelComponent;
import com.b3.gui.components.SelectBoxComponent;
import com.b3.gui.components.SliderComponent;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.world.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

import java.util.Map;

public class PracticeModeSettingsTab implements Tab {

    private Table settingsTab;
    private SideBar parent;

    public PracticeModeSettingsTab(Skin skin, BitmapFont font, float preferredWidth, SideBar parent, Map<String, Object> data) {
        this.parent = parent;

        settingsTab = new Table();
        settingsTab.setFillParent(true);
        settingsTab.pad(20);

        // ======================
        // === LABELS AND STU ===
        // ======================

        skin.add("default", font, BitmapFont.class);
        //new LabelComponent("", 16, "", Color.BLACK)
        LabelComponent labelOne = new LabelComponent(skin, "Left click the nodes to see more information", Color.BLACK);
        labelOne.getLabel().setPosition(-20, Gdx.graphics.getHeight() / 2);

        LabelComponent labelOneUnderneath = new LabelComponent(skin, "Click again on the same node to see more info", Color.BLACK);
        labelOneUnderneath.getLabel().setPosition(-20, Gdx.graphics.getHeight() / 2);

        LabelComponent labelTwo = new LabelComponent(skin, "Right click a node to set the next destination", Color.BLACK);
        labelTwo.getLabel().setPosition(-20, Gdx.graphics.getHeight() / 2);

        // ========================
        // === WHAT SEARCH ALGO ===
        // ========================

        Object[] searches = SearchAlgorithm
                .allNames()
                .stream()
                .filter(name -> SearchAlgorithm.fromName(name) == SearchAlgorithm.DEPTH_FIRST || SearchAlgorithm.fromName(name) == SearchAlgorithm.BREADTH_FIRST)
                .toArray(); // TODO - Not badly done with Object[].
        SelectBoxComponent searchSelectBox = new SelectBoxComponent(skin, font, new Array(searches));
        searchSelectBox.setSelected(searches[0]);

        searchSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SearchAlgorithm algorithm = SearchAlgorithm.fromName((String) searchSelectBox.getSelected());
                if (algorithm == null) return;
                ((World)data.get("world")).getWorldGraph().setLearningModeNext(algorithm);
            }
        });

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

        settingsTab.add(searchSelectBox.getSelectBox())
                .maxWidth(preferredWidth)
                .spaceTop(100)
                .spaceBottom(30);
        settingsTab.row();

        settingsTab.add(labelOne.getComponent())
                .align(Align.center)
                .maxWidth(preferredWidth)
                .spaceTop(5);
        settingsTab.row();

        settingsTab.add(labelOneUnderneath.getComponent())
                .align(Align.center)
                .maxWidth(preferredWidth)
                .spaceTop(5);
        settingsTab.row();

        settingsTab.add(labelTwo.getComponent())
                .align(Align.center)
                .maxWidth(preferredWidth)
                .spaceTop(5);
        settingsTab.row();

        // ====================
        // === BACK TO MENU ===
        // ====================

        MainGame controller = (MainGame)data.get("controller");
        ButtonComponent backToMenuBtn = new ButtonComponent(skin, font, "Main menu");
        backToMenuBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.getInputHandler().clear();
                controller.setScreen(new MainMenuScreen(controller));
            }
        });

        settingsTab.add(backToMenuBtn.getComponent())
                .align(Align.center)
                .maxWidth(preferredWidth)
                .spaceTop(25);
        settingsTab.row();
    }

    public void setName(String name) {
        settingsTab.setName(name);
    }

    @Override
    public Table getTab() {
        return settingsTab;
    }
}
