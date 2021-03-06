package com.b3.gui.sidebars;

import com.b3.MainGame;
import com.b3.gui.sidebars.tabs.NodesTab;
import com.b3.gui.sidebars.tabs.Tab;
import com.b3.mode.ModeType;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.SearchPauser;
import com.b3.search.SearchTicker;
import com.b3.world.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides a table to display the frontier and visited nodes
 * As part of the gui.
 * <p>
 *
 * @author lxd417 oxe410
 */
public class SideBarNodes extends SideBar implements Disposable {

	private ModeType mode;

	/**
	 * Creates a new sidebar full of nodes for use in learning mode
	 *
	 * @param stage the stage that this sidebar is contained in
	 * @param world the {@link World} that this sidebar is linked to
	 */
	public SideBarNodes(Stage stage, World world) {
		this(stage, world, 480);
	}

	/**
	 * Creates a new sidebar full of nodes for use in learning mode
	 *
	 * @param stage          the stage that this sidebar is contained in
	 * @param world          the {@link World} that this sidebar is linked to
	 * @param preferredWidth the preferred width of this sidebar, if space allows it will take up this amount of space max
	 */
	public SideBarNodes(Stage stage, World world, float preferredWidth) {
		super(stage, world, false, "window_02", preferredWidth);
		mode = MainGame.getCurrentMode();
		initTabs();
		initComponents();
	}

	/**
	 * Creates a new sidebar full of nodes for use in learning mode
	 *
	 * @param stage          the stage that this sidebar is contained in
	 * @param world          the {@link World} that this sidebar is linked to
	 * @param preferredWidth the preferred width of this sidebar, if space allows it will take up this amount of space max
	 * @param left           if true then on left hand side, otherwise on right
	 * @param deferred       if true then don't immediately generate components, otherwise do
	 */
	public SideBarNodes(Stage stage, World world, float preferredWidth, boolean left, boolean deferred) {
		super(stage, world, left, "window_02", preferredWidth);
		initTabs();

		if (!deferred)
			initComponents();
	}

	/**
	 * Initialise the nodes tab using {@link NodesTab}
	 */
	private void initTabs() {
		if (tabs != null) {
			// Add nodes tab
			Map<String, Object> data = new HashMap<String, Object>() {{
				put("world", world);
				put("stage", stage);
			}};
			tabs.put("Nodes", new NodesTab(skin, font, preferredWidth, null, data));
		}
	}

	/**
	 * Add multiple tabs onto the sidebar
	 *
	 * @param additionalTabs a {@link Map} of String to {@link Tab} to add to the sidebar
	 */
	public void addTabs(Map<String, Tab> additionalTabs) {
		tabs.putAll(additionalTabs);
	}

	/**
	 * Highlight a node in the sidebar by a given point
	 * with a given colour
	 *
	 * @param p               The point whose node to highlight
	 * @param c               The colour to highlight it in
	 * @param singleHighlight whether this is to be the only highlighted node
	 * @return Whether the highlight was successful
	 */
	public boolean highlightNode(Point p, Color c, boolean singleHighlight) {
		return highlightNode(new Node(p), c, singleHighlight);
	}

	/**
	 * Highlight a given node in the sidebar
	 * with a given colour
	 *
	 * @param n               The Node to highlight in the sidebar
	 * @param c               The colour to highlight it in
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
	 *
	 * @param p               The point whose node to highlight
	 * @param singleHighlight whether this is to be the only highlighted node
	 * @return Whether the highlight was successful
	 */
	public boolean highlightNode(Point p, boolean singleHighlight) {
		return highlightNode(new Node(p), singleHighlight);
	}

	/**
	 * Highlight a given node in the sidebar
	 * with a colour to match the colour on the graph
	 *
	 * @param n               The Node to highlight in the sidebar
	 * @param singleHighlight whether this is to be the only highlighted node
	 * @return Whether the highlight was successful
	 */
	public boolean highlightNode(Node n, boolean singleHighlight) {
		return ((NodesTab) tabs.get("Nodes")).getUI().setCellColour(n, singleHighlight);
	}

	/**
	 * @param stepthrough if true then sets the pseudocode mode to on, otherwise off
	 */
	public void setStepthrough(boolean stepthrough) {
		NodesTab nodesTab = (NodesTab) tabs.get("Nodes");
		nodesTab.getUI().setStepthrough(stepthrough);
		nodesTab.getNextBtn().getComponent().setVisible(stepthrough && (mode != ModeType.PRACTICE));
	}

	/**
	 * @return true if world has new click on this sidebar
	 */
	public boolean hasNewClick() {
		return ((NodesTab) tabs.get("Nodes")).getUI().isClickedUpdated();
	}

	/**
	 * @return the {@link Point} that the user clicked on, specifically a node
	 */
	public Point getNewClick() {
		return ((NodesTab) tabs.get("Nodes")).getUI().getClickedNode();
	}

	/**
	 * Render this menu and the underlying nodes table.
	 * Will update the current progress of the search from the world provided in {@link SideBarNodes#setWorld(World)}
	 */
	@Override
	public void render() {
		SearchTicker currentSearch = world.getWorldGraph().getCurrentSearch();
		NodesTab nodesTab = (NodesTab) tabs.get("Nodes");
		if (currentSearch != null) {
			setStepthrough(currentSearch.isPaused(SearchPauser.PLAY_PAUSE_BUTTON));
			nodesTab.setPseudocodeVisible(SearchTicker.isInspectingSearch() || currentSearch.getMode() == ModeType.PRACTICE);
		} else {
			setStepthrough(false);
			nodesTab.setPseudocodeVisible(false);
		}
		nodesTab.getUI().render(currentSearch);

		if (isOpen) {
			setSize(preferredWidth + nodesTab.getSize(), Gdx.graphics.getHeight());
			setX(Gdx.graphics.getWidth() - preferredWidth - nodesTab.getSize());
		} else {
			setSize(preferredWidth, Gdx.graphics.getHeight());
		}

	}

	/**
	 * Dispose of this menu and the stage within.
	 */
	@Override
	public void dispose() {
		stage.dispose();
	}

	/**
	 * When screen is resized update node bar accordingly
	 *
	 * @param width  The new width of the window.
	 * @param height The new height of the window.
	 */
	@Override
	public void resize(int width, int height) {
		SearchTicker currentSearch = world.getWorldGraph().getCurrentSearch();
		NodesTab nodesTab = (NodesTab) tabs.get("Nodes");
		nodesTab.getUI().forceUpdateTable(currentSearch);
		super.resize(width, height);
	}

}
