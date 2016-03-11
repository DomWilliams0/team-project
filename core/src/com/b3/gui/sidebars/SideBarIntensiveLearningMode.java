package com.b3.gui.sidebars;

import com.b3.MainGame;
import com.b3.mode.MainMenuScreen;
import com.b3.gui.TabbedPane;
import com.b3.gui.components.*;
import com.b3.input.SoundController;
import com.b3.search.SearchTicker;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.b3.world.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class SideBarIntensiveLearningMode extends SideBar implements Disposable {

    public static boolean isOpen;

    private Stage stage;
    private World world;
    private ButtonComponent triggerBtn;
    private float preferredWidth;
    private MainGame controller;

    public SideBarIntensiveLearningMode(Stage stage, World world) {
        this(stage, world, 400);
    }

    public SideBarIntensiveLearningMode(Stage stage, World world, float preferredWidth) {
        this.stage = stage;
        this.world = world;
        this.isOpen = false;
        this.preferredWidth = preferredWidth;

        setPosition(-preferredWidth, 0);
        setSize(preferredWidth, Gdx.graphics.getHeight());

        initComponents();
    }

    private void setBackgroundColor(float r, float g, float b, float a) {
        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pm1.setColor(r, g, b, a);
        pm1.fill();
        setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setController(MainGame controller) {
        this.controller = controller;
    }

    @Override
    protected void initComponents() {
        setBackgroundColor(0.56f, 0.69f, 0.83f, 1);

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(Config.getString(ConfigKey.TEXTURE_ATLAS)));
        Skin skin = new Skin(atlas);
        BitmapFont font = Utils.getFont(Config.getString(ConfigKey.FONT_FILE), 16);

        // ===================
        // === TABBED PANE ===
        // ===================

        TabbedPane.TabbedPaneStyle tabbedPaneStyle = new TabbedPane.TabbedPaneStyle();
        skin.add("default", font, BitmapFont.class);
        tabbedPaneStyle.font = font;
        tabbedPaneStyle.bodyBackground = skin.getDrawable("knob_06");
        tabbedPaneStyle.titleButtonSelected = skin.getDrawable("button_02");
        tabbedPaneStyle.titleButtonUnselected = skin.getDrawable("button_01");
        skin.add("default", tabbedPaneStyle);
        TabbedPane tabbedPane = new TabbedPane(skin);

        // ====================
        // === SETTINGS TAB ===
        // ====================

        Table settingsTab = new Table();
        settingsTab.setFillParent(true);
        settingsTab.pad(20);


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

        // Add Building button
        ButtonComponent addBuildingMode = new ButtonComponent(skin, font, "Add Building Mode");
        addBuildingMode.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                TextButton _triggerBtn = triggerBtn.getComponent();

                if (!isOpen) {
                    setX(0);
                    setY(0);
                    _triggerBtn.setText("<");
                    _triggerBtn.setX(preferredWidth - 20);

                    isOpen = true;
                } else {
                    setX(-preferredWidth);
                    _triggerBtn.setText(">");
                    _triggerBtn.setX(-20);

                    isOpen = false;
                }

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

                TextButton _triggerBtn = triggerBtn.getComponent();

                if (!isOpen) {
                    setX(0);
                    setY(0);
                    _triggerBtn.setText("<");
                    _triggerBtn.setX(preferredWidth - 20);

                    isOpen = true;
                } else {
                    setX(-preferredWidth);
                    _triggerBtn.setText(">");
                    _triggerBtn.setX(-20);

                    isOpen = false;
                }

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

        // ======================
        // === LABELS AND STU ===
        // ======================

        LabelComponent labelOne = new LabelComponent(skin, "Left click the nodes to see more information", Color.BLACK);
        labelOne.getLabel().setPosition(-20, Gdx.graphics.getHeight() / 2);

        LabelComponent labelOneUnderneath = new LabelComponent(skin, "Click again on the same node to see more info", Color.BLACK);
        labelOneUnderneath.getLabel().setPosition(-20, Gdx.graphics.getHeight() / 2);

        LabelComponent labelTwo = new LabelComponent(skin, "Right click a node to set the next destination", Color.BLACK);
        labelTwo.getLabel().setPosition(-20, Gdx.graphics.getHeight() / 2);

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
        LabelComponent searchSpeedLabel = new LabelComponent(skin, "Search speed", Color.WHITE);
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
        LabelComponent gameSpeedLabel = new LabelComponent(skin, "Game speed", Color.WHITE);
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

        ButtonComponent backToMenuBtn = new ButtonComponent(skin, font, "Main menu");
        backToMenuBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                controller.setScreen(new MainMenuScreen(controller));
            }
        });

        settingsTab.add(backToMenuBtn.getComponent())
                .align(Align.center)
                .maxWidth(preferredWidth)
                .spaceTop(25);
        settingsTab.row();

        tabbedPane.addTab("Settings", settingsTab);

        // ======================
        // === TRIGGER BUTTON ===
        // ======================

        triggerBtn = new ButtonComponent(skin, font, ">");
        triggerBtn.getComponent().setPosition(-20, Gdx.graphics.getHeight() / 2);
        triggerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TextButton _triggerBtn = triggerBtn.getComponent();

                if (!isOpen) {
                    //closes it
                    setX(0);
                    setY(0);
                    _triggerBtn.setText("<");
                    _triggerBtn.setX(preferredWidth - 20);

                    isOpen = true;

//                    float posX = world.getWorldCamera().getPosX();
//                    if (posX > -10) {
//                        System.out.println(posX);
//                        world.getWorldCamera().translateSafe(-(10+posX), 0, 0);
//                    }

                } else {
                    setX(-preferredWidth);
                    _triggerBtn.setText(">");
                    _triggerBtn.setX(-20);

                    isOpen = false;

//                    float posX = world.getWorldCamera().getPosX();
//                    if (posX < 0) {
//                        world.getWorldCamera().translateSafe(-posX, 0, 0);
//                    }
                }

            }
        });

        add(tabbedPane).maxWidth(preferredWidth);
        background(skin.getDrawable("window_03"));
        this.stage.addActor(triggerBtn.getComponent());

    }

    @Override
    public float getPreferredWidth() {
        return preferredWidth;
    }

    @Override
    public void resize(int width, int height) {
        setHeight(height);
        triggerBtn.getComponent().setY(height / 2);
    }

    @Override
    public void render() {}

    @Override
    public void dispose() {
        stage.dispose();
    }

    /**
     * @return true if the sidebar is currently open
     */
    public static boolean isOpen() {
        return isOpen;
    }
}
