package com.b3.gui.sidebars;

import com.b3.gui.TabbedPane;
import com.b3.gui.components.ButtonComponent;
import com.b3.gui.sidebars.tabs.PseudocodeTab;
import com.b3.gui.sidebars.tabs.Tab;
import com.b3.mode.ModeType;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.b3.world.World;
import com.badlogic.gdx.Gdx;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SideBar extends Table {

    protected Stage stage;
    protected World world;

    protected TabbedPane tabbedPane;
    protected ButtonComponent triggerBtn;
    protected Map<String, Tab> tabs;

    protected Skin skin;
    protected BitmapFont font;
    protected String background;

    protected float preferredWidth;
    protected boolean left;
    protected boolean isOpen;

    public SideBar(Stage stage,
                   World world,
                   boolean left,
                   String background,
                   float preferredWidth,
                   Map<String, Tab> tabs) {

        this.stage = stage;
        this.preferredWidth = preferredWidth;
        this.world = world;
        this.tabs = tabs;
        this.left = left;
        this.background = background;
        isOpen = false;

        // Setup the skin
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal(Config.getString(ConfigKey.TEXTURE_ATLAS)));
        this.skin = new Skin(atlas);
        this.font = Utils.getFont(Config.getString(ConfigKey.FONT_FILE), 16);
        //this.skin.add("default", font, BitmapFont.class);

        // Set position and size
        setPosition(left ? -preferredWidth : Gdx.graphics.getWidth(), 0);
        setSize(preferredWidth, Gdx.graphics.getHeight());
    }

    /**
     * Set the background colour of this menu
     * @param r Red colour component
     * @param g Green colour component
     * @param b Blue colour component
     * @param a Alpha component
     */
    protected void setBackgroundColor(float r, float g, float b, float a) {
        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pm1.setColor(r, g, b, a);
        pm1.fill();
        setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
    }

    public void initComponents() {
        // Set a default background colour
        setBackgroundColor(0.56f, 0.69f, 0.83f, 1);

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
        tabbedPane = new TabbedPane(skin);

        // ============
        // === TABS ===
        // ============

        if (tabs != null) {
            for (String tabName : tabs.keySet()) {
                tabbedPane.addTab(tabName, tabs.get(tabName).getTab());
            }
        }

        // ======================
        // === TRIGGER BUTTON ===
        // ======================

        triggerBtn = new ButtonComponent(skin, font, left ? ">" : "<");
        triggerBtn.getComponent().setPosition(left ? -20 : Gdx.graphics.getWidth() - triggerBtn.getComponent().getWidth() + 20, Gdx.graphics.getHeight() / 2);
        triggerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!isOpen) {
                    open();
                }
                else {
                    close();
                }

            }
        });

        add(tabbedPane).maxWidth(preferredWidth);
        background(skin.getDrawable(background));
        this.stage.addActor(triggerBtn.getComponent());
    }

    public void open() {
        TextButton _triggerBtn = triggerBtn.getComponent();

        if (left) {
            setX(0);
            setY(0);
            _triggerBtn.setText("<");
            _triggerBtn.setX(preferredWidth - 20);
        }
        else {
            setX(Gdx.graphics.getWidth()- preferredWidth);
            setY(0);
            _triggerBtn.setText(">");
            _triggerBtn.setX(Gdx.graphics.getWidth() - preferredWidth - _triggerBtn.getWidth() + 20);
        }

        isOpen = true;
    }

    public void close() {
        TextButton _triggerBtn = triggerBtn.getComponent();

        if (left) {
            setX(-preferredWidth);
            _triggerBtn.setText(">");
            _triggerBtn.setX(-20);
        }
        else {
            setX(Gdx.graphics.getWidth());
            _triggerBtn.setText("<");
            _triggerBtn.setX(Gdx.graphics.getWidth() - _triggerBtn.getWidth() + 20);
        }

        isOpen = false;
    }

    public void toggle() {
        if (isOpen) {
            close();
        }
        else {
            open();
        }
    }

    public float getPreferredWidth() {
        return preferredWidth;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public ButtonComponent getTriggerBtn() {
        return triggerBtn;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void addTab(Table tab) {
        tabbedPane.addTab(tab.getName(), tab);
    }

    public void resize(int width, int height) {
        setHeight(height);
        triggerBtn.getComponent().setY(height / 2);

        TextButton _triggerBtn = triggerBtn.getComponent();
        if (isOpen) {
            if (left) {
                setX(0);
                setY(0);
                _triggerBtn.setX(preferredWidth - 20);
            }
            else {
                setX(width - preferredWidth);
                setY(0);
                _triggerBtn.setX(width - preferredWidth - _triggerBtn.getWidth() + 20);
            }
        }
        else {
            if (left) {
                setX(-preferredWidth);
                _triggerBtn.setX(-20);
            }
            else {
                setX(width);
                _triggerBtn.setX(width - _triggerBtn.getWidth() + 20);
            }
        }
    }

    public void render() {}

}
