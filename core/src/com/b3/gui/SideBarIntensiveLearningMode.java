package com.b3.gui;

import com.b3.entity.Agent;
import com.b3.entity.ai.Behaviour;
import com.b3.entity.ai.BehaviourMultiPathFind;
import com.b3.entity.ai.BehaviourType;
import com.b3.gui.components.*;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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

public class SideBarIntensiveLearningMode extends Table implements Disposable {

    private Stage stage;
    private World world;
    private ButtonComponent triggerBtn;
    private boolean isOpen;
    private float preferredWidth;
    private ButtonComponent next;

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

    private void initComponents() {
        setBackgroundColor(0.56f, 0.69f, 0.83f, 1);

        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(Config.getString(ConfigKey.TEXTURE_ATLAS)));
        Skin skin = new Skin(atlas);
        BitmapFont font = new BitmapFont(
                Gdx.files.internal(Config.getString(ConfigKey.FONT_FILE)),
                Gdx.files.internal(Config.getString(ConfigKey.FONT_IMAGE_FILE)),
                false
        );

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


        // Flat buildings checkbox
        // todo the name needs changing?
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


        // ======================
        // === LABELS AND STU ===
        // ======================

        LabelComponent labelOne = new LabelComponent(skin, "Left click the nodes to see more information", Color.BLACK);
        labelOne.getLabel().setPosition(-20, Gdx.graphics.getHeight() / 2);

        LabelComponent labelTwo = new LabelComponent(skin, "Right click a node to set the next destination", Color.BLACK);
        labelTwo.getLabel().setPosition(-20, Gdx.graphics.getHeight() / 2);

        //Play and Pause button
        //todo there are graphics available in the skin for play and pause
        //String btnText = world.getWorldGraph().getCurrentSearch().isPaused(1) ? "Play" : "Pause";
        ButtonComponent playPause = new ButtonComponent(skin, font, "Pause");
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

        settingsTab.add(labelOne.getComponent())
                .align(Align.center)
                .maxWidth(preferredWidth)
                .spaceTop(5);
        settingsTab.row();

        settingsTab.add(labelTwo.getComponent())
                .align(Align.center)
                .maxWidth(preferredWidth)
                .spaceTop(5);
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

    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Resize this menu
     * Should be called whenever the window is resized.
     *
     * @param width Window width
     * @param height Window height
     */
    public void resize(int width, int height) {
        setHeight(height);
//        triggerBtn.getComponent().setY(height / 2);
    }

    public void render() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
