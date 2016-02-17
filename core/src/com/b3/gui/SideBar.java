package com.b3.gui;

import com.b3.entity.Agent;
import com.b3.entity.ai.Behaviour;
import com.b3.entity.ai.BehaviourMultiPathFind;
import com.b3.entity.ai.BehaviourType;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
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
import com.badlogic.gdx.math.Vector2;
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

import java.util.List;
import java.util.stream.Collectors;

public class SideBar extends Table implements Disposable {

    private Stage stage;
    private World world;
    private ButtonComponent triggerBtn;
    private boolean isOpen;
    private float preferredWidth;

    public SideBar(Stage stage, World world) {
        this(stage, world, 230);
    }

    public SideBar(Stage stage, World world, float preferredWidth) {
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

        // Start button
        ButtonComponent startButton = new ButtonComponent(skin, font, "Start Event");
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

        //Play and Pause button
        //todo there are graphics available in the skin for play and pause
        ButtonComponent playPause = new ButtonComponent(skin, font, "Pause");
        playPause.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TextButton btnplaypause = playPause.getTextButton();
                String text = btnplaypause.getText().toString();
                SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
                if(text.equals("Pause") && !ticker.isPaused()) {
                    ticker.pause(1);
                    btnplaypause.setText("Play");
                } else if(text.equals("Play")){
                    ticker.resume(1);
                    btnplaypause.setText("Pause");
                }
            }
        });

        settingsTab.add(playPause.getTextButton())
                .align(Align.center)
                .maxWidth(preferredWidth)
                .spaceTop(5);
        settingsTab.row();

        tabbedPane.addTab("Settings", settingsTab);

        // ==================
        // === EVENTS TAB ===
        // ==================

        Table eventsTab = new Table();
        eventsTab.setFillParent(true);
        eventsTab.pad(20);

        tabbedPane.addTab("Events", eventsTab);

        // Get x and y coordinates
        WorldGraph worldGraph = world.getWorldGraph();
        List<Integer> xs = Utils.range(0, worldGraph.getMaxXValue());
        List<Integer> ys = Utils.range(0, worldGraph.getMaxYValue());

        Object[] xsStr = xs.stream().map(Object::toString).collect(Collectors.toList()).toArray();
        Object[] ysStr = ys.stream().map(Object::toString).collect(Collectors.toList()).toArray();

        // X coordinates
        SelectBoxComponent xCoordSelectBox = new SelectBoxComponent(skin, font, new Array(xsStr));

        xCoordSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("hello");
            }
        });

        eventsTab.add(xCoordSelectBox.getSelectBox())
                .maxWidth(preferredWidth)
                .spaceBottom(30);
        eventsTab.row();

        // Y coordinates
        SelectBoxComponent yCoordSelectBox = new SelectBoxComponent(skin, font, new Array(ysStr));

        yCoordSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("hello");
            }
        });

        eventsTab.add(yCoordSelectBox.getSelectBox())
                .maxWidth(preferredWidth)
                .spaceBottom(30);
        eventsTab.row();

        // Queue goal button
        ButtonComponent queueGoalButton = new ButtonComponent(skin, font, "Add to queue");
        queueGoalButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                String xStr = (String)xCoordSelectBox.getSelected();
                int x = Integer.parseInt(xStr);

                String yStr = (String)yCoordSelectBox.getSelected();
                int y = Integer.parseInt(yStr);

                Node node = worldGraph.getNode(new Point(x, y));

                if (node == null) {
                    System.out.println("nooooooooo");
                }
                else {
                    Agent currentSearchAgent = worldGraph.getCurrentSearchAgent();
                    Behaviour behaviour = currentSearchAgent.getBehaviour();

                    if (behaviour.getType() == BehaviourType.FOLLOW_PATH) {
                        BehaviourMultiPathFind multiPathFind = (BehaviourMultiPathFind)behaviour;
                        multiPathFind.addNextGoal(new Vector2(x, y));
                    }
                }
            }
        });

        eventsTab.add(queueGoalButton.getTextButton())
                .align(Align.center)
                .maxWidth(preferredWidth);
        eventsTab.row();


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
                } else {
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
