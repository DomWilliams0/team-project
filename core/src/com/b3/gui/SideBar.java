package com.b3.gui;

import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.world.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

public class SideBar extends Table implements Disposable {

    private Stage stage;
    private World world;
    private ButtonComponent triggerBtn;
    private boolean isOpen;
    private float preferredWidth;

    public SideBar(Stage stage) {
        this(stage, 230);
    }

    public SideBar(Stage stage, float preferredWidth) {
        // Config stuff
        Config.set(ConfigKey.SHOW_GRID, true);
        Config.set(ConfigKey.SHOW_PATHS, true);
        Config.set(ConfigKey.FLATTEN_BUILDINGS, false);

        this.stage = stage;
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

    private void initComponents() {
        setBackgroundColor(0.56f, 0.69f, 0.83f, 1);

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("core/assets/gui/ui-blue.atlas"));
        Skin skin = new Skin(atlas);
        BitmapFont font = new BitmapFont(Gdx.files.internal("core/assets/gui/default.fnt"),
                Gdx.files.internal("core/assets/gui/default.png"), false);

        // ===================
        // === TABBED PANE ===
        // ===================

        TabbedPane.TabbedPaneStyle tabbedPaneStyle = new TabbedPane.TabbedPaneStyle();
        skin.add("default", font, BitmapFont.class);
        tabbedPaneStyle.font = skin.getFont("default");
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

        // Entity selectbox
        String[] entities = {"Civilian", "Police officer", "Firefighter", "Delivery guy"};
        SelectBoxComponent entitySelectBox = new SelectBoxComponent(skin, font, new Array(entities));
        entitySelectBox.setSelected("Civilian");

        entitySelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("sfdf");
            }
        });

        settingsTab.add(entitySelectBox.getSelectBox())
                .maxWidth(preferredWidth)
                .spaceBottom(10);
        settingsTab.row();

        // Building selectbox
        String[] buildings = {"House", "Police station", "Fire stations", "Restaurant"};
        SelectBoxComponent buildingSelectBox = new SelectBoxComponent(skin, font, new Array(buildings));
        buildingSelectBox.setSelected("House");

        buildingSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("hello");
            }
        });

        settingsTab.add(buildingSelectBox.getSelectBox())
                .maxWidth(preferredWidth)
                .spaceBottom(30);
        settingsTab.row();

        // Show grid checkbox
        CheckBoxComponent showGridCheckBox = new CheckBoxComponent(skin, font, "Show grid");
        showGridCheckBox.getCheckBox().setChecked(Config.getBoolean(ConfigKey.SHOW_GRID));
        showGridCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean showGrid = Config.getBoolean(ConfigKey.SHOW_GRID);
                Config.set(ConfigKey.SHOW_GRID, !showGrid);
            }
        });

        settingsTab.add(showGridCheckBox.getCheckBox())
                .align(Align.left)
                .maxWidth(preferredWidth)
                .spaceBottom(10);
        settingsTab.row();

        // Flat buildings checkbox
        // todo the name needs changing?
        CheckBoxComponent showLabelsCheckBox = new CheckBoxComponent(skin, font, "Flat buildings");
        showLabelsCheckBox.getCheckBox().setChecked(Config.getBoolean(ConfigKey.FLATTEN_BUILDINGS));
        showLabelsCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean flatBuildings = Config.getBoolean(ConfigKey.FLATTEN_BUILDINGS);
                world.flattenBuildings(!flatBuildings);
                Config.set(ConfigKey.FLATTEN_BUILDINGS, !flatBuildings);
            }
        });

        settingsTab.add(showLabelsCheckBox.getCheckBox())
                .align(Align.left)
                .maxWidth(preferredWidth)
                .spaceBottom(10);
        settingsTab.row();

        // Show paths checkbox
        CheckBoxComponent showPathsCheckbox = new CheckBoxComponent(skin, font, "Show paths");
        showPathsCheckbox.getCheckBox().setChecked(Config.getBoolean(ConfigKey.SHOW_PATHS));
        showPathsCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Config.set(ConfigKey.SHOW_PATHS, !Config.getBoolean(ConfigKey.SHOW_PATHS));
            }
        });

        settingsTab.add(showPathsCheckbox.getCheckBox())
                .align(Align.left)
                .maxWidth(preferredWidth);
        settingsTab.row();

        // Speed slider
        SliderComponent speedSlider = new SliderComponent(skin, 1f, 10f, 1f);
        speedSlider.setValue(Config.getInt(ConfigKey.STEPS_PER_TICK));
        speedSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Config.set(ConfigKey.STEPS_PER_TICK, (int)speedSlider.getValue());
            }
        });

        settingsTab.add(speedSlider.getSlider())
                .align(Align.center)
                .maxWidth(preferredWidth)
                .spaceTop(20);
        settingsTab.row();

        // Start button
        ButtonComponent startButton = new ButtonComponent(skin, font, "Start");
        startButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Started the event system");
            }
        });

        settingsTab.add(startButton.getTextButton())
                .align(Align.center)
                .maxWidth(preferredWidth)
                .spaceTop(20);
        settingsTab.row();

        tabbedPane.addTab("Settings", settingsTab);

        // ==================
        // === EVENTS TAB ===
        // ==================

        Table eventsTab = new Table();
        eventsTab.setFillParent(true);
        eventsTab.pad(20);

        tabbedPane.addTab("Events", eventsTab);

        // =================
        // === STATS TAB ===
        // =================

        Table statsTab = new Table();
        statsTab.setFillParent(true);
        statsTab.pad(20);

        tabbedPane.addTab("Stats", statsTab);

        // ======================
        // === TRIGGER BUTTON ===
        // ======================

        triggerBtn = new ButtonComponent(skin, font, ">");
        triggerBtn.getTextButton().setPosition(-20, Gdx.graphics.getHeight() / 2);
        triggerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TextButton _triggerBtn = triggerBtn.getTextButton();

                if (!isOpen) {
                    setX(0);
                    setY(0);
                    _triggerBtn.setText("<");
                    _triggerBtn.setX(preferredWidth - 20);

                    isOpen = true;
                }
                else {
                    setX(-preferredWidth);
                    _triggerBtn.setText(">");
                    _triggerBtn.setX(-20);

                    isOpen = false;
                }

            }
        });

        add(tabbedPane).maxWidth(preferredWidth);
        background(skin.getDrawable("window_03"));
        this.stage.addActor(triggerBtn.getTextButton());
    }

    /*public void setPreferredWidth(float preferredWidth) {
        this.preferredWidth = preferredWidth;

        setSize(preferredWidth, Gdx.graphics.getHeight());

        if (isOpen) {
            triggerBtn.getTextButton().setX(preferredWidth - 20);
        }

        for (Actor component : getChildren()) {
            getCell(component).maxWidth(preferredWidth);
        }
    }*/

    public void setWorld(World world) {
        this.world = world;
    }

    public void act() {
        stage.act(Gdx.graphics.getDeltaTime());
    }

    public void render() {
        setHeight(Gdx.graphics.getHeight());
        triggerBtn.getTextButton().setY(Gdx.graphics.getHeight() / 2);

        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
