package com.b3.gui.sidebars;

import com.b3.gui.sidebars.tabs.NodesTab;
import com.b3.gui.sidebars.tabs.PseudocodeTab;
import com.b3.gui.sidebars.tabs.Tab;
import com.b3.mode.ModeType;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.SearchTicker;
import com.b3.world.World;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Provides a table to display the frontier and visited nodes
 * As part of the gui.
 *
 * Created by Lewis adapted from Ossama's code, worked on mostly by Lewis but added to and converted to SideBar by whole group.
 */
public class SideBarNodes extends SideBar implements Disposable {
	
	/**
	 * TODO - Should use non-static {@link #isOpen()}.
	 */
    public static boolean s_isOpen;

    public SideBarNodes(Stage stage, World world) {
        this(stage, world, 460);
    }

    public SideBarNodes(Stage stage, World world, float preferredWidth) {
        super(stage, world, false, "window_02", preferredWidth, new LinkedHashMap<>());
        initTabs();
        initComponents();
    }

    public SideBarNodes(Stage stage, World world, float preferredWidth, boolean left, boolean deferred) {
        super(stage, world, left, "window_02", preferredWidth, new LinkedHashMap<>());
        initTabs();

        if (!deferred)
            initComponents();
    }

    private void initTabs() {
        // Add pseudocode tab
        if (tabs != null) {
            // Add nodes tab
            Map<String, Object> data = new HashMap<String, Object>() {{
                put("world", world);
                put("stage", stage);
            }};
            tabs.put("Nodes", new NodesTab(skin, font, preferredWidth, this, data));

            // Add pseudocode tab
            if ((world.getMode() == ModeType.LEARNING) || (world.getMode() == ModeType.TUTORIAL)) {
                data = new HashMap<String, Object>() {{
                    put("world", world);
                }};
                tabs.put("Pseudocode", new PseudocodeTab(skin, font, data));
            }
        }
    }

    public void addTabs(Map<String, Tab> additionalTabs) {
        tabs.putAll(additionalTabs);
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
        NodesTab nodesTab = (NodesTab) tabs.get("Nodes");
        return nodesTab.getUI().setCellColour(n, c, singleHighlight);
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
        return ((NodesTab)tabs.get("Nodes")).getUI().setCellColour(n, singleHighlight);
    }

    public void setStepthrough(boolean stepthrough) {
        NodesTab nodesTab = (NodesTab) tabs.get("Nodes");
        nodesTab.getUI().setStepthrough(stepthrough);
        nodesTab.getNextBtn().getComponent().setVisible(stepthrough && (world.getMode() != ModeType.PRACTICE));
    }

    public boolean hasNewClick() {
        return ((NodesTab)tabs.get("Nodes")).getUI().isClickedUpdated();
    }

    public Point getNewClick() {
        return ((NodesTab)tabs.get("Nodes")).getUI().getClickedNode();
    }

    public void resetPseudoCode() {

        SearchTicker ticker = world.getWorldGraph().getCurrentSearch();
        ticker.setInspectSearch(false);
        //ticker.resume(1);

        if (world.getMode() == ModeType.LEARNING) {
            PseudocodeTab pseudocodeTab = (PseudocodeTab) tabs.get("Pseudocode");

            pseudocodeTab.getManualAutoBtn().getComponent().setVisible(false);
            pseudocodeTab.getInspectSearchBtn().setData(false);
            pseudocodeTab.getInspectSearchBtn().setText("Begin");
        }
    }
    
    @Override
    public void open() {
        super.open();
        s_isOpen = true;
    }
    
    @Override
    public void close() {
        super.close();
        s_isOpen = false;
    }

    /**
     * Render this menu and the underlying nodes table.
     * Will update the current progress of the search from the world provided in {@link SideBarNodes#setWorld(World)}
     */
    @Override
    public void render() {
        SearchTicker currentSearch = world.getWorldGraph().getCurrentSearch();
        if(currentSearch!=null) {
            setStepthrough(currentSearch.isPaused(1));
        } else {
            setStepthrough(false);
        }
        ((NodesTab)tabs.get("Nodes")).getUI().render(currentSearch);
    }

    /**
     * Dispose of this menu and the stage within.
     */
    @Override
    public void dispose() {
        stage.dispose();
    }

    public Boolean getPseudocodeBegin() {
        return ((PseudocodeTab)tabs.get("Pseudocode")).getInspectSearchBtn().getText().toString().equals("Begin");
    }
}
