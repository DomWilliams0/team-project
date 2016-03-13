package com.b3.gui.sidebars;

import com.b3.MainGame;
import com.b3.gui.TabbedPane;
import com.b3.gui.components.ButtonComponent;
import com.b3.gui.components.CheckBoxComponent;
import com.b3.gui.components.LabelComponent;
import com.b3.gui.components.SliderComponent;
import com.b3.input.SoundController;
import com.b3.mode.MainMenuScreen;
import com.b3.search.SearchTicker;
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
import com.badlogic.gdx.utils.Disposable;

import java.util.function.Consumer;

public class SideBarCompareMode extends SideBar implements Disposable {

    private Stage stage;
    private World world;
    private ButtonComponent triggerBtn;
    private boolean isOpen;
    private float preferredWidth;

    private ButtonComponent playPause;
    private MainGame controller;

    public SideBarCompareMode(Stage stage, World world) {
        this(stage, world, 230);
    }

    public SideBarCompareMode(Stage stage, World world, float preferredWidth) {
        this.stage = stage;
        this.world = world;
        this.isOpen = false;
        this.preferredWidth = preferredWidth;

        setPosition(-preferredWidth, 0);
        setSize(preferredWidth, Gdx.graphics.getHeight());

        initComponents();
    }

    public void setController(MainGame controller) {
        this.controller = controller;
    }


    private void setBackgroundColor(float r, float g, float b, float a) {
        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pm1.setColor(r, g, b, a);
        pm1.fill();
        setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
    }
	
	private void createCheckbox(Skin skin, BitmapFont font, Table table, String label, ConfigKey configKey) {
		createCheckbox(skin, font, table, label, configKey, null);
	}
	
	private void createCheckbox(Skin skin, BitmapFont font, Table table, String label, ConfigKey configKey,
								Consumer<Boolean> checkedListener) {
		CheckBoxComponent checkBox = new CheckBoxComponent(skin, font, label);
		checkBox.getComponent().setChecked(Config.getBoolean(configKey));
		checkBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				boolean checked = checkBox.getComponent().isChecked();
				Config.set(configKey, checked);
				if (checkedListener != null)
					checkedListener.accept(checked);
			}
		});
		
		table.add(checkBox.getComponent())
				.align(Align.left)
				.maxWidth(preferredWidth)
				.spaceBottom(10);
		table.row();
	}

    public void setWorld(World world) {
        this.world = world;
    }

    public void updatePlayPauseButton() {
        playPause.setText("Play");
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

        // Show grid checkbox
        createCheckbox(skin, font, settingsTab, "Show grid", ConfigKey.SHOW_GRID);

        // Flat buildings checkbox
        createCheckbox(skin, font, settingsTab, "Flat buildings", ConfigKey.FLATTEN_BUILDINGS,
                (flatBuildings) -> world.flattenBuildings(flatBuildings));

        // Render static models checkbox
        createCheckbox(skin, font, settingsTab, "Static 3D objects", ConfigKey.RENDER_STATIC_MODELS,
                (visible) -> world.getModelManager().setStaticsVisible(visible));

        // im removing this toggle for now, as it requires a complex design decision
        //
        // we should remove the FLOCKING_ENABLED config flag, and have it iterate all
        // non-searching-agents and set them to invisible. unfortunately, models are
        // (for some reason) rendered outside of RenderSystem so this is currently
        // difficult to do without mangling everything

        // Flocking enable/disable
//        CheckBoxComponent showFlockingCheckBox = new CheckBoxComponent(skin, font, "Roaming civilians");
//        showFlockingCheckBox.getComponent().setChecked(Config.getBoolean(ConfigKey.FLOCKING_ENABLED));
//        showFlockingCheckBox.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Config.set(ConfigKey.FLOCKING_ENABLED, showFlockingCheckBox.getComponent().isChecked());
//            }
//        });
//
//        settingsTab.add(showFlockingCheckBox.getComponent())
//                .align(Align.left)
//                .maxWidth(preferredWidth)
//                .spaceBottom(10);
//        settingsTab.row();

        // Agent model rendering toggle
        createCheckbox(skin, font, settingsTab, "3D agents", ConfigKey.RENDER_AGENT_MODELS);

        // Show paths checkbox
        createCheckbox(skin, font, settingsTab, "Show paths", ConfigKey.SHOW_PATHS);

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

        // Add Building button
        ButtonComponent addBuildingMode = new ButtonComponent(skin, font, "Add Building Mode");
        addBuildingMode.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                //Close Sidebar
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
        ButtonComponent removeBuildingButton = new ButtonComponent(skin, font, "Remove Building");
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

        //Play and Pause button
        //String btnText = world.getWorldGraph().getCurrentSearch().isPaused(1) ? "Play" : "Pause";
        playPause = new ButtonComponent(skin, font, "Pause");
        playPause.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TextButton btnplaypause = playPause.getComponent();
                String text = btnplaypause.getText().toString();
                SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
                if(text.equals("Pause")) {
                    ticker.pause(1);
                    ticker.setUpdated(true);
                    btnplaypause.setText("Play");
                } else if(text.equals("Play")){
                    ticker.resume(1);
                    btnplaypause.setText("Pause");
                }
            }
        });

        settingsTab.add(playPause.getComponent())
                .align(Align.center)
                .maxWidth(preferredWidth)
                .spaceTop(5);
        settingsTab.row();

        ButtonComponent backToMenuBtn = new ButtonComponent(skin, font, "Main menu");
        backToMenuBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dispose();
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
        this.stage.addActor(triggerBtn.getComponent());
    }

    @Override
    public void resize(int width, int height) {
        setHeight(height);
        triggerBtn.getComponent().setY(height / 2);
    }

    @Override
    public void render() {}

    @Override
    public float getPreferredWidth() {
        return preferredWidth;
    }

    @Override
    public void dispose() {
        controller.getInputHandler().clear();
        stage.dispose();
    }
}
