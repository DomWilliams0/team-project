package com.b3.gui;

import com.b3.gui.components.LabelComponent;
import com.b3.mode.Mode;
import com.b3.mode.ModeType;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.SearchTicker;
import com.b3.search.util.SearchAlgorithm;
import com.b3.search.util.takeable.PriorityQueueT;
import com.b3.search.util.takeable.StackT;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.b3.world.World;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.*;

/**
 * A table which will display the frontier and visited nodes
 * in a useful way to inspect what is happening internally with the algorithm.
 * <p>
 * @author lxd417 oxe410
 */
public class VisNodes extends Table {

	private final World world;
	private float timer;
	private boolean stepthrough;
	private ScrollPaneManager spm;
	private boolean descriptionShown;

	private Node newVisited;
	private List<Node> newFrontier;
	private Node highestNode;
	private Node justExpanded;

	private String newVisitedStr = "<NOTHING>";
	private String newFrontierStr = "<NOTHING>";
	private String highestNodeStr = "<NOTHING>";

	private StringBuilder stepString;
	private Formatter formatter;

	private SearchAlgorithm alg;
	private ArrayList<Node> frontier;

	/**
	 * Create a new data visualisation table
	 * @param stage The stage with which to render
	 * @param skin The skin of the table and scrollpanes (background of scrollpane is removed)
	 * @param world The world of the simulation
	 */
	public VisNodes(Stage stage, Skin skin, World world) {
		super(skin);

		this.world = world;

		stepthrough = false;
		stepString = new StringBuilder();
		formatter = new Formatter(stepString, Locale.UK);

		//anchor the table to the top-left position
		left().top();

		spm = new ScrollPaneManager(stage, this);

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
			render = render(new StackT<>(), new HashSet<>(), SearchAlgorithm.DEPTH_FIRST, false);
		} else {
			//we have data to render. get the most recent changes from the ticker
			newVisited = ticker.getMostRecentlyExpanded();
			newFrontier = ticker.getLastFrontier();
			justExpanded = ticker.getMostRecentlyExpanded();

			//only update the shown data if we need to
			if (!stepthrough || ticker.isUpdated() || SearchTicker.isInspectingSearch() == descriptionShown) {
				//tell the ticker we've used its data
				ticker.setUpdated(false);
				//render the data
				render = render(
						ticker.getFrontier(),
						ticker.getVisited(),
						ticker.getAlgorithm(),
						(!SearchTicker.isInspectingSearch() && ticker.getMode() != ModeType.PRACTICE)
				);
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
	 * This forces the node bar to reload it's data, and recalculate positioning of everything without informing the ticker
     * @param ticker {@link SearchTicker} of the current search
     */
	public void forceUpdateTable(SearchTicker ticker) {
		render(
				ticker.getFrontier(),
				ticker.getVisited(),
				ticker.getAlgorithm(),
				(!SearchTicker.isInspectingSearch() && ticker.getMode() != ModeType.PRACTICE)
		);
	}

	/**
	 * Render the table, preventing re-render based on time defined in config file.
	 * If stepthrough mode is active, calling this will force a render regardless;
	 * the calling function should ensure the search has been updated prior to calling this.
	 *
	 * @param front   	the frontier to display
	 * @param visited 	the visited set to display
	 * @param alg     	the current algorithm being used by the search
	 * @param showDesc	whether to show the dynamic description of the algorithm in progress
	 * @return the state of the render  - 0: Not yet time to render
	 * - 1: Rendered as normal
	 * - 2: The scrollpane(s) are being dragged.
	 */
	public int render(Collection<Node> front, Set<Node> visited, SearchAlgorithm alg, boolean showDesc) {
		//check if a scrollpane is being used
		if (spm.scrollpanesBeingUsed()) {
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
		if (showDesc) setupDescription();
		descriptionShown = showDesc;
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
		spm.addNodes(frontier, visitedSorted);
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

	 * Set a background colour for a cell in the scrollpanes based on the node.
	 *
	 * @param n               The node to highlight
	 * @param c               The colour to set
	 * @param singleHighlight whether this is to be the only highlighted node
	 * @return whether the colour was successful
	 */
	public boolean setCellColour(Node n, Color c, boolean singleHighlight) {

        return spm.setCellColour(n, c, singleHighlight);
	}

    /**
     * Set a background colour for a cell in the scrollpanes based on the node.
     * The colour will match the colour of the node in the world.
     *
     * @param n The node to highlight
     * @param singleHighlight whether this is to be the only highlighted node
     * @return whether the cell was coloured
     */
    public boolean setCellColour(Node n, boolean singleHighlight) {
        //singleHighlight tells us if this is the only node to be highlighted,
        //so remove all other colours if this is true
		return spm.setCellColour(n, singleHighlight);
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
		//clear the tables
		clear();
		spm.clear();

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
			float sh;
			if (SearchTicker.isInspectingSearch() || world.getWorldGraph().getCurrentSearch().getMode() == ModeType.PRACTICE) {
				sh = h / 5;
			} else {
				sh = (float) (h / 2.75);
			}

			//row 4 - display the scroll panes holding the collection tables
			add(spm.getFp()).fill().height(sh).maxHeight(sh);
			addLabel("   ");
			add(spm.getVp()).fill().height(sh).maxHeight(sh);
			row();

//			//row 5 - note that lowest frontier node is lowest priority
//			addLabel("--------------------\n" +
//					"Lowest Priority");
//			addLabel("");
//			row();

		} else {
			addLabel("No search in progress...");
			row();

			addLabel("");
			row();
		}

	}

	/**
	 * Setup the description at the bottom of the sidebar.
	 * Will display the "what I've just done" dynamic prompts.
	 */
	private void setupDescription() {
		convertNodeReps();
		stepString = new StringBuilder();
		formatter = new Formatter(stepString, Locale.UK);
		String expandedNode = "I have just expanded the node:\n" +
				"%s, which is now\n" +
				"added to the visited set.\n";
		String addedToFrontier = "I have added the following\n" +
				"nodes to the frontier:\n" +
				"%s\n";
		String nextNode = "My next node to expand is\n" +
				"%s";
		formatter.format(expandedNode + "\n" +
						addedToFrontier + "\n" +
						nextNode,
				newVisitedStr, newFrontierStr, highestNodeStr);
		addLabel(stepString.toString())
				.colspan(3).spaceTop(15);
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
	 * Preferably uses the {@link Node#toAdaptedString()} method
	 * but only if this method returns something desirable.
	 */
	private void convertNodeReps() {
		newVisitedStr =
				newVisited == null ? "<NOTHING>" : newVisited.toString();
		newFrontierStr =
				newFrontier == null ? "<NOTHING>" : convertNewFrontier();
		highestNodeStr =
				highestNode == null ? "<NOTHING>" : highestNode.toString();
	}

	/**
	 * Returns a string representation of newFrontier
	 * Displays the current priority next to the node.
	 *
	 * @return The string to display in the description
	 */
	private String convertNewFrontier() {
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
	}

	/**
	 * Query whether the user has clicked a different node in the scroll panes
	 *
	 * @return Whether the user has clicked a different node in the scroll panes
	 */
	public boolean isClickedUpdated() {
		return spm.isClickedUpdated();
	}

	/**
	 * Get the coordinates of the node which has been clicked in the scroll panes
	 * Marks it as not updated any more.
	 *
	 * @return The clicked nodes coordinates
	 */
	public Point getClickedNode() {
		return spm.getClickedNode();
	}

	/**
	 * @return the current {@link World} that this visNodes is linked to
     */
	public World getWorld() {
		return world;
	}

	/**
	 * @return the frontier of the current search, accessed using {@link SearchTicker}
     */
	public List<Node> getNewFrontier() {
		return newFrontier;
	}

	/**
	 * @return the most recently expanded node, accessed using {@link SearchTicker}; I.E. the more recently expanded
	 * node 
     */
	public Node getJustExpanded() {
		return justExpanded;
	}
}
