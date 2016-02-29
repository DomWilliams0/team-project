package com.b3.gui;

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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.*;

/**
 * A table which will display the frontier and visited nodes
 * in a useful way to inspect what is happening internally with the algorithm.
 *
 * Created by lewis on 08/02/16.
 */
public class VisNodes extends Table {

	private ScrollPane fp, vp;
	private Table ft, vt;
	private float timer;
	private boolean stepthrough;
	private HashMap<Node, Table> cellmap;
	private HashMap<Node, Color> colours;
	private Node clickedNode;
	private boolean clickedNodeUpdated = false;

	/**
	 * Provides a description of how the search algorithms work,
	 * giving instructions of how the nodes are managed.
	 *
	 * todo should this be short / concise, or long / descriptive?
	 * todo
	 */
	private final String description = "How it works:\n" +
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
	private final String nextNode = "My next node to expand is\n (it is at the top of the \ndata structure):\n" +
			"%s";

	private StringBuilder stepString;
	private Formatter formatter;

	/**
	 * Create a new data visualisation table
	 *
	 * @param stage The stage with which to render
	 * @param skin The skin of the table and scrollpanes (background of scrollpane is removed)
	 */
	public VisNodes(Stage stage, Skin skin) {
		super(skin);

		stepthrough = false;
		stepString = new StringBuilder();
		formatter = new Formatter(stepString, Locale.UK); //todo change locale based on config
		cellmap = new HashMap<>();
		colours = new HashMap<>();

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

		//setup style of scrollpane - remove the grey background
		//from the style generated automatically
		// ScrollPane.ScrollPaneStyle style = fp.getStyle();
//        style.background = null;
//        fp.setStyle(style);

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
	 * This method will handle null values appropriately to save this occurring outside the object
	 *
	 * @param ticker The search ticker whose values are to be rendered.
	 */
	public void render(SearchTicker ticker) {
		int render;
		if(ticker==null || ticker.getVisited()==null || ticker.getFrontier()==null) {
			render =  render(new StackT<>(), new HashSet<>(), SearchAlgorithm.DEPTH_FIRST);
		} else {
			newVisited = ticker.getMostRecentlyExpanded();
			newFrontier = ticker.getLastFrontier();
            justExpanded = ticker.getMostRecentlyExpanded();
			if(!stepthrough || ticker.isUpdated()) {
				ticker.setUpdated(false);
				render = render(ticker.getFrontier(), ticker.getVisited(), ticker.getAlgorithm());
			} else
				render = 0;
		}
		if (ticker != null) {
			if(render==2) {
				ticker.pause(0);
			} else if(render == 1) {
				ticker.resume(0);
			}
		}
	}

	/**
	 * Render the table, preventing re-render based on time defined in config file.
	 * If stepthrough mode is active, calling this will force a render regardless;
	 * the calling function should ensure the search has been updated prior to calling this.
	 *
	 * @param front the frontier to display
	 * @param visited the visited set to display
	 * @param alg the algorithm currently being used by the search
	 *
	 * @return the state of the render  - 0: Not yet time to render
	 *                                  - 1: Rendered as normal
	 *                                  - 2: The scrollpane(s) are being dragged.
	 */
	public int render(Collection<Node> front, Set<Node> visited, SearchAlgorithm alg) {
		//check if a scrollpane is being used
		if(scrollpanesBeingUsed()) {
			return 2;
		}

		//stop it rendering every frame unless we're stepping through
		if(!stepthrough) {
			float timeBetweenTicks = Config.getFloat(ConfigKey.TIME_BETWEEN_TICKS);
			timer += Utils.TRUE_DELTA_TIME;
			if (timer < timeBetweenTicks)
				return 0;

			if(timer > 2*timeBetweenTicks)
				//it has been a long time since last render so reset it instead of decrementing it
				timer = 0;
			else
				// it hasn't been too long since last render so decrement it
				timer -= timeBetweenTicks;
		}

		//check whether we need to render a data collection
		boolean rendermore = !front.isEmpty() || !visited.isEmpty();

		//setup the table
		setupTable(alg, rendermore);

		//if we need to render a data collection
		if(rendermore) {
			//make the arraylist be ordered based on take-order of the collection
			//todo perhaps change what is shown for A*. Could do with describing distance (unless it is shown in tooltips??), and the nodes move more with A* so is less useful

			ArrayList<Node> frontier = sortFront(front,alg);
			highestNode = frontier.get(0);

			//get the visited set and sort it numerically by x then y
			ArrayList<Node> visitedSorted = new ArrayList<>(visited);
			Collections.sort(visitedSorted, (p1, p2) -> {
				if (p1.getPoint().getX() == p2.getPoint().getX()) {
					return p1.getPoint().getY() - p2.getPoint().getY();
				}
				return p1.getPoint().getX() - p2.getPoint().getX();
			});

			//populate the list tables
			for (int i = 0; i < Math.max(frontier.size(), visitedSorted.size()); i++) {
				vt.row();
				ft.row();
				// ========================
				// EDIT IF YOU WANT TO
				// CHANGE TO ADAPTED STRING
				// ========================
				if (frontier.size() > i) {
					addToTable(ft, frontier.get(i));
				}
				if (visitedSorted.size() > i) {
					addToTable(vt, visitedSorted.get(i));
				}
			}
		}
		setupDescription();
		return 1;
	}

	/**
     * Check if a scrollpane is being used ie being dragged or is otherwise scrolling
     * @return Whether a scrollpane is being dragged / scrolled
     */
    private boolean scrollpanesBeingUsed() {
        return vp.isDragging() || vp.isFlinging() || vp.isPanning() || fp.isDragging() || fp.isFlinging() || fp.isPanning();
    }

	/**
     * Add a given node to the given table
     * Wraps the node in its own table, which is stored in the hashmap
     * So that it can later be highlighted.
     *
     * Will apply any known colour to the node immediately.
     * @param t The table to add the node to
     * @param n The node to display in the table.
     */
    private void addToTable(Table t, Node n) {
        //create the wrapping table
		Table row = new Table(this.getSkin());
        //add the node text to the wrapping table
		row.add(n.toString());

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
                if(clickedNode.equals(n) && !scrollpanesBeingUsed()) clickedNodeUpdated = true;
			}
		});

        //add the wrapping table to the overall table
		t.add(row);
        //store the wrapping table in the hashmap, keyed by its node
		cellmap.put(n,row);
        //apply the highlight colour of the node, if applicable.
		applyColour(n);
	}

	/**
	 * Set a background colour for a cell in the scrollpanes based on the node.
	 *
	 * @param n The node to highlight
	 * @param c The colour to set
	 * @param singleHighlight whether this is to be the only highlighted node
	 * @return whether the cell was coloured
	 */
	public boolean setCellColour(Node n, Color c, boolean singleHighlight) {
        //singleHighlight tells us if this is the only node to be highlighted,
        //so remove all other colours if this is true
		if(singleHighlight) colours.clear();
        //store the given colour
		colours.put(n,c);
        //apply all node colours, since we may have deleted other colours by using this method.
		return applyColourAll();
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
        //so remove all other colours if this is true\
			if (singleHighlight) colours.clear();
			Color c = Color.YELLOW;
		if (cellmap.get(n) != null)
			if (cellmap.get(n).getParent().equals(vt)) c = WorldGraph.VISITED_COLOUR;
		if (cellmap.get(n) != null)
			if (cellmap.get(n).getParent().equals(ft)) c = WorldGraph.FRONTIER_COLOUR;
		if (newFrontier != null)
			if (newFrontier.contains(n)) c = WorldGraph.NEW_FRONTIER_COLOUR;
		if (justExpanded != null)
			if (justExpanded.equals(n)) c = WorldGraph.JUST_EXPANDED_COLOUR;
			//store the given colour
			colours.put(n, c);
			//apply all node colours, since we may have deleted other colours by using this method.
			return applyColourAll();
		}



	/**
	 * Update the colour of all nodes known to this object.
	 * @return Whether all nodes were correctly highlighted
	 */
	private boolean applyColourAll() {
        //keeps track of whether all colour applications were successful
		boolean all = true;
        //iterate over the cellmap keys, i.e. those nodes currently known by the in-progress search
		for(Node n : cellmap.keySet()) {
            //apply the colour and update all
            //ordered this way to avoid short-circuit evaluation; we must apply all node colours regardless.
			all = applyColour(n) && all;
		}
		return all;
	}

	/**
	 * Apply the colour known to the hash map to the given node
	 *
	 * Adapted from code at http://stackoverflow.com/questions/24250791/make-scene2d-ui-table-with-alternate-row-colours
	 * @param n The node whose colour to apply
	 * @return Whether the node was successfully highlighted
	 */
	private boolean applyColour(Node n) {
        //get the desired colour, or default to white
		Color c = colours.get(n);
		if (c == null) c = Color.WHITE;

        //setup a pixmap with the desired colour
		Pixmap pm = new Pixmap(1, 1, Pixmap.Format.RGB565);
		pm.setColor(c);
		pm.fill();

        //get the wrapping table which is displaying the node
		Table t = cellmap.get(n);
		if(t==null) {
            //there was no table corresponding, so just cleanup and return
			pm.dispose();
			return false;
		} else {
            //highlight the node text then cleanup
			t.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(pm))));
			pm.dispose();
		}
        //we have reached this point iff the highlight was successful.
		return true;
	}

	/**
	 * Converts a data collection to a list,
	 * which is of the same order as when taking from the collection
     *
     * Importantly, leaves given frontier untouched.
     *
	 * @param front The frontier to sort
	 * @param alg The algorithm with which to adapt the frontier
	 * @return The frontier, in the current intended order of node expansion
	 */
	private ArrayList<Node> sortFront(Collection<Node> front, SearchAlgorithm alg) {
		ArrayList<Node> list = new ArrayList<>(front);
		//check the algorithm
		//BFS is already sorted correctly.
		switch(alg) {
			//DFS requires a reverse due to stack
			case DEPTH_FIRST: Collections.reverse(list); break;
			//A* will utilise a temporary pq which will take all its items in order and add to list.
			case A_STAR: PriorityQueueT<Node> temp = new PriorityQueueT((PriorityQueueT)front);
				list = new ArrayList();
				for(int i=0;i<front.size();i++) {
					list.add(temp.take());
				}
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
	 * @param alg The search algorithm being used by the search
	 * @param rendermore Whether the data collections are being rendered
	 */
	private void setupTable(SearchAlgorithm alg, boolean rendermore) {
		//clear the tables
		clear();
		ft.clear();
		vt.clear();
        //clear the map of stored nodes with their wrapper tables
		cellmap.clear();

		//get what type the frontier is using
		String frontierDesc = "";
		switch(alg) {
			case DEPTH_FIRST: frontierDesc = "DFS: LIFO (stack)"; break;
			case BREADTH_FIRST: frontierDesc = "BFS: FIFO (queue)"; break;
			case A_STAR: frontierDesc = "A*: Priority Queue"; break;
		}

		//we need to render the data collections
		if(rendermore) {
			//row 1 - titles
			add("Frontier");
			add("   ");
			add("Visited nodes");
			row();

			//spacing
//            add("");
//            row();

			//row 2 - description of data collections
			add(frontierDesc);
			add("   ");
			add("Using Hash Set");
			row();
			//row 3 - note that highest frontier node is highest priority
			add("--Highest Priority--");
			add("   ");
			add("");
			row();

			//set up height to set for the scroll panes
			float h = Gdx.graphics.getHeight();
			float sh = h / 3;

			//row 4 - display the scroll panes holding the collection tables
			add(fp).fill().height(sh).maxHeight(sh);
			add("   ");
			add(vp).fill().height(sh).maxHeight(sh);
			row();

			//row 5 - note that lowest frontier node is lowest priority
			add("--Lowest Priority--");
			add("");
			row();

		} else {
			add("No search in progress...");
			row();

			add("");
			row();
		}

	}

	/**
	 * Setup the description at the bottom of the sidebar.
	 * Will either display the "what I've just done" prompts,
	 * or a generic description.
	 */
	private void setupDescription() {
		if(stepthrough) {
			convertNodeReps();
			stepString = new StringBuilder();
			formatter = new Formatter(stepString, Locale.UK); //todo change locale based on config
			formatter.format(expandedNode + "\n" +
							addedToFrontier + "\n" +
							nextNode,
					newVisitedStr, newFrontierStr, highestNodeStr);
			add(stepString).colspan(3);
		} else {
			//final row - describe the algorithm in words
			add(description).colspan(3);
		}
	}

	/**
	 * Convert the nodes we have stored for later use
	 * into the representations we wish to display them
	 * Preferably uses the Node.toAdaptedString() method
	 * as long as this method returns something desirable.
	 */
	private void convertNodeReps() {
		newVisitedStr = newVisited==null?"<NOTHING>":newVisited.toString();
		newFrontierStr = newFrontier==null?"<NOTHING>":newFrontier.toString();
		highestNodeStr = highestNode==null?"<NOTHING>":highestNode.toString();

		// =====================
		// TO USE ADAPTED STRING
		// =====================
		/*
		newVisitedStr = newVisited==null?"<NOTHING>":newVisited.toAdaptedString();
		newFrontierStr = "[ ";
		if(newFrontier!=null && newFrontier.size()>0) {
			for (int i = 0; i < newFrontier.size(); i++) {
				newFrontierStr += newFrontier.get(i).toAdaptedString();
				if(i<newFrontier.size()-1) newFrontierStr += ", ";
			}
		} else {
			newFrontierStr += "NOTHING";
		}
		newFrontierStr += " ]";
		highestNodeStr = highestNode==null?"<NOTHING>":highestNode.toAdaptedString();
		*/
	}

	public boolean isClickedUpdated() {
		return clickedNodeUpdated;
	}

	public Point getClickedNode() {
		clickedNodeUpdated = false;
		return clickedNode.getPoint();
	}
}
