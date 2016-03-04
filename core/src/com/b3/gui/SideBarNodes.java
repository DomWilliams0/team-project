package com.b3.gui;

import com.b3.Mode;
import com.b3.gui.components.ButtonComponent;
import com.b3.gui.components.MessageBoxComponent;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.SearchTicker;
import com.b3.search.util.SearchAlgorithm;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Font;
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
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;

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
    private ButtonComponent next;

    private ButtonComponent inspectSearchBtn;
    private ButtonComponent manualAutoBtn;
    private ButtonComponent nextBtn;

    /**
     * Create a new gui element with a default preferred size
     *
     * @param stage The stage on which to act and draw.
     */
    public SideBarNodes(Stage stage, World world) {
        this(stage, world, 420);
    }

    /**
     * Create a new gui element
     *
     * @param stage The stage on which to act and draw
     * @param preferredWidth The preferred width of the gui table
     */
    public SideBarNodes(Stage stage, World world, float preferredWidth) {
        this.stage = stage;
        this.isOpen = false;
        this.preferredWidth = preferredWidth;
        this.world = world;

        //set the position to be off-screen to the right
        setPosition(Gdx.graphics.getWidth(), 0);
        //set the size to be full-height and preferred width.
        setSize(preferredWidth, Gdx.graphics.getHeight());

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
        BitmapFont font = Font.getFont(Config.getString(ConfigKey.FONT_FILE), 16);
        skin.add("default", font, BitmapFont.class);
        Label.LabelStyle style = new Label.LabelStyle(font, Color.BLACK);
        skin.add("default", style);

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

        // ==================
        // === NODES PANE ===
        // ==================

        Table nodesTab = new Table();
        nodesTab.setFillParent(true);
        //nodesTab.pad(10);

        //create the data table which will display the nodes
        ui = new VisNodes(stage, skin, world);

        next = new ButtonComponent(skin, font, "Next step");
        next.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
                ticker.tick(true);
            }
        });
        next.getComponent().setVisible(false);

        //put the nodes ui onto this
        nodesTab.add(ui).maxWidth(preferredWidth).top().pad(20);
        nodesTab.row();
        nodesTab.add(next.getComponent());

        tabbedPane.addTab("Nodes", nodesTab);

        // =======================
        // === PSEUDOCODE PANE ===
        // =======================

        Table pseudocodeTab = new Table();
        pseudocodeTab.setFillParent(true);

        // Pseudocode visualiser
        // ---------------------
        PseudocodeVisualiser pseudocodeVisualiser = PseudocodeVisualiser.getInstance(skin);
        pseudocodeTab.add(pseudocodeVisualiser).spaceBottom(30).row();

        // Next button
        // -----------
        nextBtn = new ButtonComponent(skin, font, "Next");
        nextBtn.getComponent().setVisible(false);
        nextBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
                ticker.setInspectSearch(true);
                ticker.tick(true);
            }
        });

        // Manual/Automatic inspection
        // ---------------------------
        manualAutoBtn = new ButtonComponent(skin, font, "Manual inspect");
        manualAutoBtn.setData(true);
        manualAutoBtn.getComponent().setVisible(false);
        manualAutoBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if ((Boolean) manualAutoBtn.getData()) {
                    // Currently automatic -> manual
                    SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
                    ticker.pause(1);
                    ticker.setUpdated(true);
                    ticker.setInspectSearch(true);

                    nextBtn.getComponent().setVisible(true);
                    manualAutoBtn.setData(false);
                    manualAutoBtn.setText("Automatic inspect");
                }
                else {
                    // Currently manual -> automatic
                    SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
                    ticker.resume(1);

                    nextBtn.getComponent().setVisible(false);
                    manualAutoBtn.setData(true);
                    manualAutoBtn.setText("Manual inspect");
                }
            }
        });

        // Inspect search button (start/stop)
        // ----------------------------------
        inspectSearchBtn = new ButtonComponent(skin, font, "Activate");
        inspectSearchBtn.setData(false);
        inspectSearchBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                boolean currentlyStarted = (Boolean) inspectSearchBtn.getData();

                SearchTicker ticker = world.getWorldGraph().getCurrentSearch();

                if (ticker.getAlgorithm() == SearchAlgorithm.DIJKSTRA) {
                    MessageBoxComponent.show("Dijkstra not yet implemented", "Warning");
                }
                else if (ticker.isTickedOnce()) {
                    ticker.setInspectSearch(!currentlyStarted);
                    ticker.resume(1);

                    manualAutoBtn.getComponent().setVisible(!currentlyStarted);
                    inspectSearchBtn.setData(!currentlyStarted);
                    inspectSearchBtn.setText(currentlyStarted ? "Activate" : "Stop");

                    // Clear pseudocode information
                    if (currentlyStarted) {
                        nextBtn.getComponent().setVisible(false);
                        manualAutoBtn.setData(true);
                        manualAutoBtn.setText("Manual inspect");
                        ticker.clearPseudocodeInfo();
                    }
                }
                else {
                    //Close Sidebar
                    TextButton _triggerBtn = triggerBtn.getComponent();

                    if (!isOpen) {
                        setX(0);
                        setY(0);
                        _triggerBtn.setText(">");
                        _triggerBtn.setX(Gdx.graphics.getWidth() - preferredWidth - _triggerBtn.getWidth() + 20);

                        isOpen = true;
                    } else {
                        setX(-preferredWidth);
                        _triggerBtn.setText("<");
                        _triggerBtn.setX(Gdx.graphics.getWidth() - _triggerBtn.getWidth() + 20);

                        isOpen = false;
                    }

                    world.showPopupError(2);
                }
            }
        });

        pseudocodeTab.add(inspectSearchBtn.getComponent()).spaceBottom(10).row();
        pseudocodeTab.add(manualAutoBtn.getComponent()).spaceBottom(10).row();
        pseudocodeTab.add(nextBtn.getComponent());
        tabbedPane.addTab("Pseudocode", pseudocodeTab);

        // ======================
        // === TRIGGER BUTTON ===
        // ======================

        triggerBtn = new ButtonComponent(skin, font, "<");
        triggerBtn.getComponent().setPosition(Gdx.graphics.getWidth() - triggerBtn.getComponent().getWidth() + 20, Gdx.graphics.getHeight() / 2);
        triggerBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                TextButton _triggerBtn = triggerBtn.getComponent();

                if (!isOpen) {
                    setX(Gdx.graphics.getWidth()- preferredWidth);
                    setY(0);
                    _triggerBtn.setText(">");
                    _triggerBtn.setX(Gdx.graphics.getWidth() - preferredWidth - _triggerBtn.getWidth() + 20);
                    isOpen = true;

                    /*if (world.getMode() != Mode.COMPARE) {
                        float posX = world.getWorldCamera().getPosX();
                        if (posX < 10) {
                            world.getWorldCamera().translateSafe(10 - posX, 0, 0);
                        }
                    }*/
                }
                else {
                    setX(Gdx.graphics.getWidth());
                    _triggerBtn.setText("<");
                    _triggerBtn.setX(Gdx.graphics.getWidth() - _triggerBtn.getWidth() + 20);
                    isOpen = false;
                    
                    /*if (world.getMode() != Mode.COMPARE) {
                        float posX = world.getWorldCamera().getPosX();
                        if (posX >= 10) {
                            System.out.println(posX);
                            world.getWorldCamera().translateSafe(-posX, 0, 0);
                        }
                    }*/

                }

            }
        });
//        next = new ButtonComponent(skin, font, "Next step");
//        next.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
//                ticker.tick(true);
//            }
//        });
//        next.getComponent().setVisible(false);

        add(tabbedPane).maxWidth(preferredWidth);
        background(skin.getDrawable("window_02"));
        this.stage.addActor(triggerBtn.getComponent());

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
        triggerBtn.getComponent().setY(height / 2);

        TextButton _triggerBtn = triggerBtn.getComponent();
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
     * Render this menu and the underlying nodes table.
     * Will update the current progress of the search from the world provided in {@link SideBarNodes#setWorld(World)}
     */
    public void render() {
        SearchTicker currentSearch = world.getWorldGraph().getCurrentSearch();
        if(currentSearch!=null) {
            setStepthrough(currentSearch.isPaused(1));
        } else {
            setStepthrough(false);
        }
        ui.render(currentSearch);
        //ui.setCellColour(new Node(new Point(11,11)), Color.GREEN, true);
    }

    /**
     * Highlight a node in the sidebar by a given point
     * with a given colour
     * @param p The point whose node to highlight
     * @param c The colour to highlight it in
     * @param singleHighlight whether this is to be the only highlighted node
     * @return Whether the highlight was successful
     */
    public boolean highlightNode(Point p, Color c, boolean singleHighlight) {
        return highlightNode(new Node(p), c, singleHighlight);
    }
    /**
     * Highlight a given node in the sidebar
     * with a given colour
     * @param n The Node to highlight in the sidebar
     * @param c The colour to highlight it in
     * @param singleHighlight whether this is to be the only highlighted node
     * @return Whether the highlight was successful
     */
    public boolean highlightNode(Node n, Color c, boolean singleHighlight) {
        return ui.setCellColour(n, c, singleHighlight);
    }

    /**
     * Highlight a node in the sidebar by a given point
     * with a colour to match the colour on the graph
     * @param p The point whose node to highlight
     * @param singleHighlight whether this is to be the only highlighted node
     * @return Whether the highlight was successful
     */
    public boolean highlightNode(Point p, boolean singleHighlight) {
        return highlightNode(new Node(p), singleHighlight);
    }
    /**
     * Highlight a given node in the sidebar
     * with a colour to match the colour on the graph
     * @param n The Node to highlight in the sidebar
     * @param singleHighlight whether this is to be the only highlighted node
     * @return Whether the highlight was successful
     */
    public boolean highlightNode(Node n, boolean singleHighlight) {
        return ui.setCellColour(n, singleHighlight);
    }

    public void setStepthrough(boolean stepthrough) {
        ui.setStepthrough(stepthrough);
        next.getComponent().setVisible(stepthrough);
    }

    public boolean hasNewClick() {
        return ui.isClickedUpdated();
    }

    public Point getNewClick() {
        return ui.getClickedNode();
    }

    public void resetPseudoCode() {

        SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
            ticker.setInspectSearch(false);
            ticker.resume(1);

            manualAutoBtn.getComponent().setVisible(false);
            inspectSearchBtn.setData(false);
            inspectSearchBtn.setText("Activate");
    }

    /**
     * Dispose of this menu and the stage within.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }


}
