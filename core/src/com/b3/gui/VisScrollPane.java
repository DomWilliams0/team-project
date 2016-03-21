package com.b3.gui;

import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.SearchPauser;
import com.b3.search.WorldGraphRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.PixmapTextureData;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import java.util.HashMap;
import java.util.List;

/**
 * Represents a {@link ScrollPane} with extra features suited for displaying {@link Node}s.
 *
 * @author lxd417
 */
public class VisScrollPane extends ScrollPane {

	private Table outerTable;
	private Skin tableSkin;

	private HashMap<Node, Table> cellmap;
	private HashMap<Node, Color> colours;
	private final HashMap<Color, TextureRegionDrawable> colourTextureCache = new HashMap<>();
	private Node clickedNode;
	private boolean clickedNodeUpdated = false;

	private TextureRegionDrawable defaultTexture;
	private Pixmap pm;
	private static final Color defaultBackground = new Color(0.56f, 0.69f, 0.83f, 1);

	private VisNodes vn;

	private boolean isFrontier;

	public VisScrollPane(Skin skin, ScrollPaneStyle style, boolean isFrontier, VisNodes vn) {
		this(new Table(skin), style, isFrontier, vn);
	}

	private VisScrollPane(Table t, ScrollPaneStyle style, boolean isFrontier, VisNodes vn) {
		super(t, style);
		outerTable = t;
		tableSkin = t.getSkin();


		this.isFrontier = isFrontier;
		this.vn = vn;


		t.top();

		cellmap = new HashMap<>();
		colours = new HashMap<>();

		pm = new Pixmap(1, 1, Pixmap.Format.RGB565);
		pm.setColor(defaultBackground);
		pm.fill();
		defaultTexture = new TextureRegionDrawable(new TextureRegion(new Texture(pm)));
		colourTextureCache.put(defaultBackground, defaultTexture);

		setFadeScrollBars(false);
		setScrollingDisabled(true, false);
	}

	public void add(List<Node> list) {
		int index = isFrontier ? 0 : -1;
		for (Node n : list) {
			addToTable(n, index);
			if (isFrontier) index++;
		}
	}



	/**
	 * Add a given node to the given table
	 * Wraps the node in its own table, which is stored in the hashmap
	 * So that it can later be highlighted.
	 *
	 * Will apply any known colour to the node immediately.
	 * @param n The node to display in the table.
	 * @param i The priority of the node, or <code>-1</code> if not applicable.
	 */
	private void addToTable(Node n, int i) {
		//create the wrapping table
		Table row = new Table(tableSkin);
		//put the priority if applicable
		String prefix = "";
		if (i>=0) prefix = ++i + ". ";

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
				vn.getWorld().getWorldGraph().getCurrentSearch().pause(SearchPauser.VIS_SCROLL_PANE);
				return true;
			}
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				//we have received an end-of-touch event
				//ensure the scrollpanes aren't being used before saying the clicked node can be accessed
				if (clickedNode.equals(n) && !scrollpaneBeingUsed()) clickedNodeUpdated = true;
				vn.getWorld().getWorldGraph().getCurrentSearch().resume(SearchPauser.VIS_SCROLL_PANE);
			}

			@Override
			public boolean mouseMoved(InputEvent event, float x, float y) {
				//the mouse is over a node.
				//highlight it in the world.
				vn.getWorld().getWorldGraph().getRenderer().highlightOver(n.getPoint(), getColorFromGraph(n));
				return super.mouseMoved(event, x, y);
			}
		});

		//add the wrapping table to the overall table
		outerTable.add(row).width(120);
		outerTable.row().spaceBottom(1);
		//store the wrapping table in the cellmap, keyed by its node
		cellmap.put(n, row);
		//apply the highlight colour of the node, if applicable.
		applyColour(n);
	}

	// CELL COLOURING
	// -------------------------------------


	/**
	 * Apply the colour known to the hash map to the given node
	 *
	 * Adapted from code at http://stackoverflow.com/questions/24250791/make-scene2d-ui-table-with-alternate-row-colours
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
	 * Set a background colour for a cell in the scrollpanes based on the node.
	 *
	 * @param n The node to highlight
	 * @param c The colour to set
	 * @param singleHighlight whether this is to be the only highlighted node
	 * @return whether the colour was successful
	 */
	public boolean setCellColour(Node n, Color c, boolean singleHighlight) {
		//singleHighlight tells us if this is the only node to be highlighted,
		//so remove all other colours if this is true
		if (singleHighlight) colours.clear();
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
	 * @param n The node to query
	 * @return The colour of the node in the world graph
	 */
	public Color getColorFromGraph(Node n) {
		//default colour in case something goes very wrong
		Color c = defaultBackground;
		//check whether the ndde is in the tables
		if (cellmap.get(n) != null) {
			//it is, so check if the node is in frontier or visited.
			if (!isFrontier)
				c = WorldGraphRenderer.VISITED_COLOUR;
			else
				c = WorldGraphRenderer.FRONTIER_COLOUR;
		}

		//check whether the node is actually a new frontier or just expanded
		//done after table-check so that these colours take precedence.
		if (vn.getNewFrontier()!=null && vn.getNewFrontier().contains(n)) c = WorldGraphRenderer.LAST_FRONTIER_COLOUR;
		if (vn.getJustExpanded()!=null && vn.getJustExpanded().equals(n)) c = WorldGraphRenderer.JUST_EXPANDED_COLOUR;
		return c;
	}

	/**
	 * Check if a scrollpane is being used ie being dragged or is otherwise scrolling
	 * @return Whether a scrollpane is being dragged / scrolled
	 */
	public boolean scrollpaneBeingUsed() {
		return isDragging() || isFlinging() || isPanning();
	}

	/**
	 * Query whether the user has clicked a different node in the scroll panes
	 * @return Whether the user has clicked a different node in the scroll panes
	 */
	public boolean isClickedUpdated() {
		return clickedNodeUpdated;
	}

	/**
	 * Get the coordinates of the node which has been clicked in the scroll panes
	 * Marks it as not updated any more.
	 * @return The clicked nodes coordinates
	 */
	public Point getClickedNode() {
		clickedNodeUpdated = false;
		return clickedNode.getPoint();
	}

	/**
	 * Clears all items from the table
	 */
	public void clearTable() {
		outerTable.clear();
		cellmap.clear();
	}
	
}
