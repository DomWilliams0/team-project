package com.b3.gui;

import com.b3.search.SearchTicker;
import com.b3.search.util.SearchAlgorithm;
import com.b3.search.util.takeable.StackT;
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
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashSet;

/**
 * Provides a table to display the frontier and visited nodes
 * As part of the gui.
 *
 * Created by lewis on 15/02/16.
 */
public class SideBarNodes extends Table implements Disposable {
    private Stage stage;
    private ButtonComponent triggerBtn;
    private VisNodes ui;
    private World world;
    private boolean isOpen;
    private float preferredWidth;

    /**
     * Create a new gui element with a default preferred size
     *
     * @param stage The stage on which to act and draw.
     */
    public SideBarNodes(Stage stage) {
        this(stage, 370);
    }

    /**
     * Create a new gui element
     *
     * @param stage The stage on which to act and draw
     * @param preferredWidth The preferred width of the gui table
     */
    public SideBarNodes(Stage stage, float preferredWidth) {
        this.stage = stage;
        this.isOpen = false;
        this.preferredWidth = preferredWidth;

        //set the position to be off-screen to the right
        setPosition(Gdx.graphics.getWidth(), 0);
        //set the size to be full-height and preferred width.
        setSize(preferredWidth, Gdx.graphics.getHeight());

        top();
        initComponents();
    }

    /**
     * Set the world with which to get the search ticker
     *
     * @param world The world being simulated.
     */
    public void setWorld(World world) {
        this.world = world;
    }

    /**
     * Set the background colour of this menu
     * @param r Red colour component
     * @param g Green colour component
     * @param b Blue colour component
     * @param a Alpha component
     */
    private void setBackgroundColor(float r, float g, float b, float a) {
        Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pm1.setColor(r, g, b, a);
        pm1.fill();
        setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm1))));
    }

    /**
     * Initialise the components used by this object
     */
    private void initComponents() {
        //set a default background colour
        setBackgroundColor(0.56f, 0.69f, 0.83f, 1);

        //setup the skin
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("core/assets/gui/ui-blue.atlas"));
        Skin skin = new Skin(atlas);
//        skin.addRegions(new TextureAtlas(Gdx.files.internal("core/assets/gui/uiscrollskin.atlas")));
        BitmapFont font = new BitmapFont(Gdx.files.internal("core/assets/gui/default.fnt"),
                Gdx.files.internal("core/assets/gui/default.png"), false);
        skin.add("default", font, BitmapFont.class);
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        skin.add("default", style);


        //create the data table which will display the nodes
        ui = new VisNodes(stage, skin);
        //ui.setBackground(skin.getDrawable("knob_06"));

        // ======================
        // === TRIGGER BUTTON ===
        // ======================

        triggerBtn = new ButtonComponent(skin, font, "<");
        triggerBtn.getTextButton().setPosition(Gdx.graphics.getWidth() - triggerBtn.getTextButton().getWidth() + 20, Gdx.graphics.getHeight() / 2);
        triggerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TextButton _triggerBtn = triggerBtn.getTextButton();

                if (!isOpen) {
                    setX(Gdx.graphics.getWidth()- preferredWidth);
                    setY(0);
                    _triggerBtn.setText(">");
                    _triggerBtn.setX(Gdx.graphics.getWidth() - preferredWidth - _triggerBtn.getWidth() + 20);

                    isOpen = true;
                }
                else {
                    setX(Gdx.graphics.getWidth());
                    _triggerBtn.setText("<");
                    _triggerBtn.setX(Gdx.graphics.getWidth() - _triggerBtn.getWidth() + 20);

                    isOpen = false;
                }

            }
        });

        //put the nodes ui onto this
        add(ui).maxWidth(preferredWidth).top();
        //set the background
        background(skin.getDrawable("window_03"));
        //add the button to the stage.
        this.stage.addActor(triggerBtn.getTextButton());

    }

    /**
     * Resize this menu
     * Should be called whenever the window is resized.
     *
     * @param width Window width
     * @param height Window height
     */
    public void resize(int width, int height) {
        TextButton _triggerBtn = triggerBtn.getTextButton();
        if (isOpen) {
            setX(width- preferredWidth);
            setY(0);
            _triggerBtn.setX(width - preferredWidth - _triggerBtn.getWidth() + 20);
        } else {
            setX(width);
            _triggerBtn.setX(width - _triggerBtn.getWidth() + 20);
        }
    }

    /**
     * Tell the stage held by this object to act.
     */
    public void act() {
        stage.act(Gdx.graphics.getDeltaTime());
    }

    /**
     * Render this menu and the underlying nodes table.
     * Will update the current progress of the search from the world provided in {@link SideBarNodes#setWorld(World)}
     */
    public void render() {
        setHeight(Gdx.graphics.getHeight());
        triggerBtn.getTextButton().setY(Gdx.graphics.getHeight() / 2);
        SearchTicker currentSearch = world.getWorldGraph().getCurrentSearch();
        ui.render(currentSearch);

        stage.draw();
    }

    /**
     * Dispose of this menu and the stage within.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }


}
