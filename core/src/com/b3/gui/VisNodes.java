package com.b3.gui;

import com.b3.gui.components.LabelComponent;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.SearchTicker;
import com.b3.search.WorldGraph;
import com.b3.search.util.SearchAlgorithm;
import com.b3.search.util.takeable.PriorityQueueT;
import com.b3.search.util.takeable.StackT;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.b3.world.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.*;

/**
 * A table which will display the frontier and visited nodes
 * in a useful way to inspect what is happening internally with the algorithm.
 * <p>
 * Created by Lewis, worked on mostly by Lewis but refactored a lot by whole group.
 */
public class VisNodes extends Table {

	private final World world;
	//todo extract these to a separate class?
	private ScrollPane fp, vp;
	private Table ft, vt;
	private float timer;
	private boolean stepthrough;
	private HashMap<Node, Table> cellmap;
	private HashMap<Node, Color> colours;
	private final HashMap<Color, TextureRegionDrawable> colourTextureCache = new HashMap<>();
	private Node clickedNode;
	private boolean clickedNodeUpdated = false;

	private TextureRegionDrawable defaultTexture;
	private Pixmap pm;

	/**
	 * Provides a description of how the search algorithms work,
	 * giving instructions of how the nodes are managed.
	 * <p>
	 * todo should this be short / concise, or long / descriptive?
	 * todo should this even exist any more
	 */
	private final String description =
			"Description while waiting for next search:\n" +
					"Starting at the start node, its \n" +
					"neighbours (successors) are inspected.\n" +
					"These are inserted into the data \n" +
					"collection, which depends on the search.\n" +
					"This collection forms the frontier, \n" +
					"which we expand in the order defined\n" +
					"by the collection (e.g. a stack \n" +
					"being First In, First Out).\n" +
					"\n" +
					"With each expansion we mark the node \n" +
					"as visited (using a hash set),\n" +
					"to ensure we do not expand it again.";
	private static final Color defaultBackground = new Color(0.56f, 0.69f, 0.83f, 1);

	private Node newVisited;
	private List<Node> newFrontier;
	private Node highestNode;
	private Node justExpanded;

	private String newVisitedStr = "<NOTHING>";
	private String newFrontierStr = "<NOTHING>";
	private String highestNodeStr = "<NOTHING>";

	private final String expandedNode = "I have just expanded the node:\n" +
			"%s, which is now\n" +
			"added to the visited set.\n";
	private final String addedToFrontier = "I have added the following\n" +
			"nodes to the frontier:\n" +
			"%s\n";
	private final String nextNode = "My next node to expand is\n" +
			"%s";

	private StringBuilder stepString;
	private Formatter formatter;

	private SearchAlgorithm alg;
	private ArrayList<Node> frontier;

	/**
	 * Create a new data visualisation table
	 *
	 * @param stage The stage with which to render
	 * @param skin  The skin of the table and scrollpanes (background of scrollpane is removed)
	 * @param world The world of the simulation
	 */
	public VisNodes(Stage stage, Skin skin, World world) {
		super(skin);

		this.world = world;

		stepthrough = false;
		stepString = new StringBuilder();
		formatter = new Formatter(stepString, Locale.UK); //todo change locale based on config
		cellmap = new HashMap<>();
		colours = new HashMap<>();

		pm = new Pixmap(1, 1, Pixmap.Format.RGB565);
		pm.setColor(defaultBackground);
		pm.fill();
		defaultTexture = new TextureRegionDrawable(new TextureRegion(new Texture(pm)));
		colourTextureCache.put(defaultBackground, defaultTexture);

		//anchor the table to the top-left position
		left().top();

		ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
		style.background = getBackground();
		style.hScroll = skin.getDrawable("scroll_back_hor");
		style.hScrollKnob = skin.getDrawable("knob_02");
		style.vScroll = skin.getDrawable("scroll_back_ver");
		style.vScrollKnob = skin.getDrawable("knob_02");

		//frontier table, encapsulated in a scrollpane
		ft = new Table(getSkin());
		ft.top();
		fp = new ScrollPane(ft, style);
		fp.setFadeScrollBars(false);
		fp.setScrollingDisabled(true, false);

		//visited table, encapsulated in a scrollpane
		vt = new Table(getSkin());
		vt.top();
		vp = new ScrollPane(vt, style);
		vp.setFadeScrollBars(false);
		vp.setScrollingDisabled(true, false);

		stage.addActor(vp);
		stage.addActor(fp);

	}

	public void setStepthrough(boolean stepthrough) {
		this.stepthrough = stepthrough;
	}

	/**
	 * Render the table, using a given search ticker
	 * This method will handle null values appropriately to save this occurring outside the object.
	 *
	 * @param ticker The search ticker whose values are to be rendered.
	 */
	public void render(SearchTicker ticker) {
		int render;
		//check that the ticker exists; if it does, see if the ticker has data to render
		if (ticker == null || ticker.getVisited() == null || ticker.getFrontier() == null) {
			//no data to render, so render this with dummy contents.
			render = render(new StackT<>(), new HashSet<>(), SearchAlgorithm.DEPTH_FIRST);
		} else {
			//we have data to render. get the most recent changes from the ticker
			newVisited = ticker.getMostRecentlyExpanded();
			newFrontier = ticker.getLastFrontier();
			justExpanded = ticker.getMostRecentlyExpanded();

			//only update the shown data if we need to
			if (!stepthrough || ticker.isUpdated()) {
				//tell the ticker we've used its data
				ticker.setUpdated(false);
				//render the data
				render = render(ticker.getFrontier(), ticker.getVisited(), ticker.getAlgorithm());
			} else
				render = 0;
		}
		//pause or resume the ticker based on scrollpane usage
		if (ticker != null) {
			if (render == 2) {
				//the scrollpanes are being used, so pause the ticker
				ticker.pause(0);
			} else if (render == 1) {
				//the scrollpanes are not being used, so release the #0 pause-lock
				//(if somewhere else has paused it, this will not resume immediately)
				ticker.resume(0);
			}
		}
	}

	/**
	 * Render the table, preventing re-render based on time defined in config file.
	 * If stepthrough mode is active, calling this will force a render regardless;
	 * the calling function should ensure the search has been updated prior to calling this.
	 *
	 * @param front   the frontier to display
	 * @param visited the visited set to display
	 * @param alg     the current algorithm being used by the search
	 * @return the state of the render  - 0: Not yet time to render
	 * - 1: Rendered as normal
	 * - 2: The scrollpane(s) are being dragged.
	 */
	public int render(Collection<Node> front, Set<Node> visited, SearchAlgorithm alg) {
		//check if a scrollpane is being used
		if (scrollpanesBeingUsed()) {
			return 2;
		}

		this.alg = alg;

		//stop it rendering every frame if we're on play mode
		//if stepping through, it's safe to update every time the user clicks next
		if (!stepthrough) {
			if (updateTimer()) return 0;
		}

		//check whether we need to render a data collection
		boolean rendermore = !front.isEmpty() || !visited.isEmpty();

		//setup the table
		//TOP of the sidebar
		setupTable(alg, rendermore);

		//if we need to render a data collection
		if (rendermore) {
			//put the data in the tables
			//MIDDLE of the sidebar
			populateTables(front, visited);

		}

		//put the description on the sidebar
		//BOTTOM of the sidebar
		setupDescription();
		return 1;
	}

	/**
	 * Populate the tables with the given data
	 *
	 * @param front   The frontier nodes to display
	 * @param visited The visited nodes to display
	 */
	private void populateTables(Collection<Node> front, Set<Node> visited) {
		//make the arraylist be ordered based on take-order of the collection
		frontier = sortFront(front);

		//get the highest priority node
		if (frontier.size() > 0)
			highestNode = frontier.get(0);

		//get the visited set and sort it numerically by x then y
		LinkedList<Node> visitedSorted = new LinkedList<>(visited);
		Collections.sort(visitedSorted, (p1, p2) -> {
			if (p1.getPoint().getX() == p2.getPoint().getX()) {
				return p1.getPoint().getY() - p2.getPoint().getY();
			}
			return p1.getPoint().getX() - p2.getPoint().getX();
		});

		//populate the list tables
		int index = 0;
		for (Node n : frontier) {
			addToTable(ft, n, index++);
		}
		for (Node n : visitedSorted) {
			addToTable(vt, n, -1);
		}
	}

	/**
	 * Update the timer and check if enough time has elapsed
	 *
	 * @return if enough time has elapsed - true indicates the render should go ahead as planned
	 */
	private boolean updateTimer() {
		float timeBetweenTicks = Config.getFloat(ConfigKey.TIME_BETWEEN_TICKS);
		timer += Utils.TRUE_DELTA_TIME;

		//not enough time has elapsed since last tick - do nothing
		if (timer < timeBetweenTicks)
			return false;

		if (timer > 2 * timeBetweenTicks)
			//it has been a long time since last render so reset it instead of decrementing it
			timer = 0;
		else
			// it hasn't been too long since last render so decrement it
			timer -= timeBetweenTicks;
		return true;
	}

	/**
	 * Add a given node to the given table
	 * Wraps the node in its own table, which is stored in the hashmap
	 * So that it can later be highlighted.
	 * <p>
	 * Will apply any known colour to the node immediately.
	 *
	 * @param t The table to add the node to
	 * @param n The node to display in the table.
	 * @param i The priority of the node, or <code>-1</code> if not applicable.
	 */
	private void addToTable(Table t, Node n, int i) {
		//create the wrapping table
		Table row = new Table(this.getSkin());
		//put the priority if applicable
		String prefix = "";
		if (i >= 0) prefix = ++i + ". ";

		//add the node text to the wrapping table
		//edit here if you want to use adapted string
		row.add(prefix + n.toString());

		//add a touch listener to the wrapping table in order to detect a click on the given node.
		row.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				//we have received a start-of-touch event
				//note the clicked node
				clickedNode = n;
				//don't yet say that this is ready to be accessed; the user might be scrolling the pane.
				clickedNodeUpdated = false;
				return true;
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				//we have received an end-of-touch event
				//ensure the scrollpanes aren't being used before saying the clicked node can be accessed
				if (clickedNode.equals(n) && !scrollpanesBeingUsed()) clickedNodeUpdated = true;
			}

			@Override
			public boolean mouseMoved(InputEvent event, float x, float y) {
				//the mouse is over a node.
				//highlight it in the world.
				world.getWorldGraph().highlightOver(n.getPoint(), getColorFromGraph(n));
				return super.mouseMoved(event, x, y);
			}
		});

		//add the wrapping table to the overall table
		t.add(row).width(120);
		t.row().spaceBottom(1);
		//store the wrapping table in the cellmap, keyed by its node
		cellmap.put(n, row);
		//apply the highlight colour of the node, if applicable.
		applyColour(n);
	}

	// CELL COLOURING
	// -------------------------------------


	/**
	 * Apply the colour known to the hash map to the given node
	 * <p>
	 * Adapted from code at http://stackoverflow.com/questions/24250791/make-scene2d-ui-table-with-alternate-row-colours
	 *
	 * @param n The node whose colour to apply
	 * @return Whether the node was successfully highlighted
	 */
	private boolean applyColour(Node n) {
		//get the wrapping table which is displaying the node
		Table t = cellmap.get(n);
		//there is no table
		if (t == null) return false;

		//get the desired colour, or default to white
		Color c = colours.getOrDefault(n, defaultBackground);

		TextureRegionDrawable backgroundTexture = colourTextureCache.get(c);
		if (backgroundTexture == null) {
			//setup a pixmap with the desired colour
			Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGB565);
			pm.setColor(c);
			pm.fill();
			backgroundTexture = new TextureRegionDrawable(new TextureRegion(new Texture(new PixmapTextureData(pm, null, false, false))));
			colourTextureCache.put(c, backgroundTexture);
		}

		//highlight the node text then cleanup
		t.setBackground(backgroundTexture);
		//we have reached this point iff the highlight was successful.
		return true;
	}

	/**
	 * Update the colour of all nodes known to this object.
	 *
	 * @return Whether all nodes were correctly highlighted
	 */
	private boolean applyColourAll() {
		//keeps track of whether all colour applications were successful
		boolean all = true;

		//iterate over the cellmap keys, i.e. those nodes currently known by the in-progress search
		for (Node n : cellmap.keySet()) {
			//apply the colour and update all
			//ordered this way to avoid short-circuit evaluation; we must apply all node colours regardless.
			all = applyColour(n) && all;
		}
		return all;
	}


	/**
	 * Set a background colour for a cell in the scrollpanes based on the node.
	 *
	 * @param n               The node to highlight
	 * @param c               The colour to set
	 * @param singleHighlight whether this is to be the only highlighted node
	 * @return whether the colour was successful
	 */
	public boolean setCellColour(Node n, Color c, boolean singleHighlight) {
		//singleHighlight tells us if this is the only node to be highlighted,
		//so remove all other colours if this is true
		if (singleHighlight) colours.clear();
		//store the given colour
		colours.put(n, c);
		//apply all node colours, since we may have deleted other colours by using this method.
		return applyColourAll();
	}

	/**
	 * Set a background colour for a cell in the scrollpanes based on the node.
	 * The colour will match the colour of the node in the world.
	 *
	 * @param n               The node to highlight
	 * @param singleHighlight whether this is to be the only highlighted node
	 * @return whether the cell was coloured
	 */
	public boolean setCellColour(Node n, boolean singleHighlight) {
		//singleHighlight tells us if this is the only node to be highlighted,
		//so remove all other colours if this is true
		if (singleHighlight) colours.clear();
		Color c = getColorFromGraph(n);

		//store the given colour
		colours.put(n, c);
		//apply all node colours, since we may have deleted other colours by using this method.
		return applyColourAll();
	}

	/**
	 * Get the colour of a given node as corresponds with the colours in the world
	 *
	 * @param n The node to query
	 * @return The colour of the node in the world graph
	 */
	public Color getColorFromGraph(Node n) {
		//default colour in case something goes very wrong
		Color c = defaultBackground;
		//check whether the ndde is in the tables
		if (cellmap.get(n) != null) {
			//it is, so check if the node is in frontier or visited.
			if (cellmap.get(n).getParent().equals(vt)) c = WorldGraph.VISITED_COLOUR;
			if (cellmap.get(n).getParent().equals(ft)) c = WorldGraph.FRONTIER_COLOUR;
		}

		//check whether the node is actually a new frontier or just expanded
		//done after table-check so that these colours take precedence.
		if (newFrontier != null && newFrontier.contains(n)) c = WorldGraph.LAST_FRONTIER_COLOUR;
		if (justExpanded != null && justExpanded.equals(n)) c = WorldGraph.JUST_EXPANDED_COLOUR;
		return c;
	}

	// SETUP METHODS
	// --------------------------------

	/**
	 * Converts a data collection to a list,
	 * which is of the same order as when taking from the collection
	 * <p>
	 * Importantly, leaves given frontier untouched.
	 *
	 * @param front The frontier to sort
	 * @return The frontier, in the current intended order of node expansion
	 */
	private ArrayList<Node> sortFront(Collection<Node> front) {
		ArrayList<Node> list = new ArrayList<>(front);
		// BFS is already sorted correctly.
		switch (alg) {
			// DFS requires a reverse due to stack
			case DEPTH_FIRST:
				Collections.reverse(list);
				break;
			// A* asks the pq to give a sorted list of its elements
			case DIJKSTRA:
			case A_STAR:
				list = ((PriorityQueueT<Node>) front).sortedOrder();
				break;
		}
		return list;
	}

	/**
	 * Reset (clear) the table,
	 * Setting up the layout of the table in the process:
	 * Adds the cells and rows required to make the table.
	 * Nothing further needs be performed after calling this, add data to vt and ft
	 *
	 * @param alg        The search algorithm being used by the search
	 * @param rendermore Whether the data collections are being rendered
	 */
	private void setupTable(SearchAlgorithm alg, boolean rendermore) {
		//clear the map of stored nodes with their wrapper tables
		cellmap.clear();

		//clear the tables
		clear();
		ft.clear();
		vt.clear();

		//we need to render the data collections
		if (rendermore) {
			//full title;
			addLabel("Running search using " + alg.getName(), true)
					.colspan(3).spaceBottom(5);
			row();

			//row 1 - titles
			addLabel("Frontier");
			addLabel("   ");
			addLabel("Visited nodes");
			row().padBottom(10);

			//row 2 - description of data collections
			addLabel(alg.getFrontierDescription());
			addLabel("   ");
			addLabel("Using Hash Set");
			row();
			//row 3 - note that highest frontier node is highest priority
			addLabel("Highest Priority\n" +
					"--------------------");
			addLabel("   ");
			addLabel("");
			row();

			//set up height to set for the scroll panes
			float h = Gdx.graphics.getHeight();
			float sh = h / 5;

			//row 4 - display the scroll panes holding the collection tables
			add(fp).fill().height(sh).maxHeight(sh);
			addLabel("   ");
			add(vp).fill().height(sh).maxHeight(sh);
			row();

			//row 5 - note that lowest frontier node is lowest priority
			addLabel("--------------------\n" +
					"Lowest Priority");
			addLabel("");
			row();
			addLabel("_________________________________________")
					.colspan(3).padTop(5);
			row();

		} else {
			addLabel("No search in progress...");
			row();

			addLabel("");
			row();
		}

	}

	/**
	 * Setup the description at the bottom of the sidebar.
	 * Will either display the "what I've just done" prompts,
	 * or a generic description.
	 */
	private void setupDescription() {
		if (ft.hasChildren() || vt.hasChildren()) {
			convertNodeReps();
			stepString = new StringBuilder();
			formatter = new Formatter(stepString, Locale.UK); //todo change locale based on config
			formatter.format(expandedNode + "\n" +
							addedToFrontier + "\n" +
							nextNode,
					newVisitedStr, newFrontierStr, highestNodeStr);
			addLabel(stepString.toString())
					.colspan(3).spaceTop(15);
		} else {
			//final row
			addLabel(description)
					.colspan(3).spaceTop(15);
		}
	}

	/**
	 * Add text to this table encapsulated in a label
	 * Uses default size settings etc based on not being a title in {@link VisNodes#addLabel(String, boolean)}
	 *
	 * @param text The string to encapsulate in a label and add to the table
	 * @return The cell created by adding the label
	 */
	private Cell addLabel(String text) {
		return addLabel(text, false);
	}

	/**
	 * Add a text label to this table
	 * The size is defined by whether this is a title
	 *
	 * @param text    The string to encapsulate in a label and add to the table
	 * @param isTitle whether this string is a title
	 * @return The cell created by adding the label
	 */
	private Cell addLabel(String text, boolean isTitle) {
		//titles are a bit larger
		int size = isTitle ? 18 : 16;

		//make the label and add it
		LabelComponent lbl = new LabelComponent("aller/Aller_Rg.ttf", size, text, Color.BLACK);
		return add(lbl.getComponent());
	}

	/**
	 * Convert the nodes we have stored for later use
	 * into the representations we wish to display them
	 * Preferably uses the Node.toAdaptedString() method
	 * as long as this method returns something desirable.
	 */
	private void convertNodeReps() {
		newVisitedStr = newVisited == null ? "<NOTHING>" : newVisited.toString();
		newFrontierStr = newFrontier == null ? "<NOTHING>" : convertNewFrontier();
		highestNodeStr = highestNode == null ? "<NOTHING>" : highestNode.toString();
	}

	/**
	 * Returns a string representation of newFrontier
	 * Based on current search algorithm being used
	 *
	 * @return The string to display in the description
	 */
	private String convertNewFrontier() {
		if (alg == SearchAlgorithm.A_STAR || alg == SearchAlgorithm.DIJKSTRA) {
			//the algorithm uses a priority queue
			//it can be hard to see where insertion occurs, so note this down in the description.

			String s = "";
			int i = 0;
			for (Node node : newFrontier) {
				//add "#<priority number>: <node string>" to the string
				s += "#" + (frontier.indexOf(node) + 1) + ": " + node.toString() + "  ";
				//limit the string to only have 2 per row
				//(only works for newFrontier list size < 5, since there should never be more than 4)
				if (++i == 2) s += "\n";
			}
			//ensure there are always 2 rows here otherwise the sidebar will keep resizing
			if (i < 2) s += "\n";
			return s;
		} else {
			//DFS or BFS being used - insertion into frontier is easy to see
			return newFrontier.toString();
		}
	}

	/**
	 * Query whether the user has clicked a different node in the scroll panes
	 *
	 * @return Whether the user has clicked a different node in the scroll panes
	 */
	public boolean isClickedUpdated() {
		return clickedNodeUpdated;
	}

	/**
	 * Get the coordinates of the node which has been clicked in the scroll panes
	 * Marks it as not updated any more.
	 *
	 * @return The clicked nodes coordinates
	 */
	public Point getClickedNode() {
		clickedNodeUpdated = false;
		return clickedNode.getPoint();
	}

	/**
	 * Check if a scrollpane is being used ie being dragged or is otherwise scrolling
	 *
	 * @return Whether a scrollpane is being dragged / scrolled
	 */
	private boolean scrollpanesBeingUsed() {
		return vp.isDragging() || vp.isFlinging() || vp.isPanning() || fp.isDragging() || fp.isFlinging() || fp.isPanning();
	}
}
