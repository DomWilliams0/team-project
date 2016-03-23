package com.b3.gui;

import com.b3.search.Node;
import com.b3.search.Point;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.List;

/**
 * Manages the frontier & visited {@link ScrollPane}s for a {@link VisNodes} object.
 * These are managed internally as {@link VisScrollPane}s.
 *
 * @author lxd417
 */
public class ScrollPaneManager {

	private VisScrollPane vp, fp;
	private MostRecentlyUpdated mru = MostRecentlyUpdated.NONE;

	/**
	 * Make a new {@link ScrollPaneManager}
	 * rendering on a given {@link Stage},
	 * utilising a given {@link VisNodes}.
	 *
	 * @param stage The {@link Stage} on which to render the scrollpanes
	 * @param vn    The {@link VisNodes} whose data is to be used as assistants while rendering
	 */
	public ScrollPaneManager(Stage stage, VisNodes vn) {
		Skin skin = vn.getSkin();
		//setup the style for the panes
		ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
		style.background = vn.getBackground();
		style.hScroll = skin.getDrawable("scroll_back_hor");
		style.hScrollKnob = skin.getDrawable("knob_02");
		style.vScroll = skin.getDrawable("scroll_back_ver");
		style.vScrollKnob = skin.getDrawable("knob_02");

		//make the scroll panes
		vp = new VisScrollPane(skin, style, false, vn);
		fp = new VisScrollPane(skin, style, true, vn);

		//put the panes on the stage
		stage.addActor(vp);
		stage.addActor(fp);
	}

	/**
	 * Populate the {@link VisScrollPane}s with the given {@link Node}s
	 *
	 * @param frontier the frontier to render
	 * @param visited  the visited set (ordered as desired) to render
	 */
	public void addNodes(List<Node> frontier, List<Node> visited) {
		vp.add(visited);
		fp.add(frontier);
	}

	/**
	 * Clear the {@link VisScrollPane}s of data
	 */
	public void clear() {
		vp.clearTable();
		fp.clearTable();
	}

	/**
	 * Get the {@link VisScrollPane} displaying the visited {@link Node}s
	 *
	 * @return The {@link VisScrollPane} displaying the visited {@link Node}s
	 */
	public VisScrollPane getVp() {
		return vp;
	}

	/**
	 * Get the {@link VisScrollPane} displaying the frontier {@link Node}s
	 *
	 * @return the {@link VisScrollPane} displaying the frontier {@link Node}s
	 */
	public VisScrollPane getFp() {
		return fp;
	}

	/**
	 * Check whether a {@link VisScrollPane} managed by this is being used
	 * i.e. scrolled, dragged, flicked.
	 * Utilises the internal {@link VisScrollPane#scrollpaneBeingUsed()} method
	 *
	 * @return whether a {@link VisScrollPane} inside this manager is being used.
	 */
	public boolean scrollpanesBeingUsed() {
		return vp.scrollpaneBeingUsed() || fp.scrollpaneBeingUsed();
	}

	/**
	 * Check whether a {@link Node} clicked on by the user in one of the internal {@link VisScrollPane}s
	 * has a click which has not yet been viewed.
	 * <p>
	 * Marks it as updated, such that {@link #isClickedUpdated()} will return false until a new click is registered.
	 *
	 * @return <code>true</code> if there's a new click which hasn't yet been registered.
	 */
	public boolean isClickedUpdated() {
		if (fp.isClickedUpdated()) {
			fp.getClickedNode();
			mru = MostRecentlyUpdated.FP;
		} else if (vp.isClickedUpdated()) {
			vp.getClickedNode();
			mru = MostRecentlyUpdated.VP;
		} else {
			mru = MostRecentlyUpdated.NONE;
		}
		return mru != MostRecentlyUpdated.NONE;
	}

	/**
	 * Get the coordinates of the {@link Node} which has been clicked by the user.
	 * Only gets the most recently clicked one.
	 * Also marks it as no longer updated internally,
	 * i.e. {@link #isClickedUpdated()} will return false.
	 *
	 * @return The most recently clicked {@link Node} coordinates in the {@link VisScrollPane}s, or <code>null</code> if no node was clicked
	 */
	public Point getClickedNode() {
		//check which is most recently updated
		switch (mru) {
			case FP:
				return fp.getClickedNode();
			case VP:
				return vp.getClickedNode();
		}
		//there has been no clicked node
		return null;
	}

	/**
	 * Colour a given {@link Node} in the {@link VisScrollPane}s.
	 * The colour will match that of the node in the simulation.
	 *
	 * @param n               The {@link Node} to highlight
	 * @param singleHighlight Whether all other colours should be removed (such that this is the only coloured node)
	 * @return <code>true</code> if the colour operation was successful
	 */
	public boolean setCellColour(Node n, boolean singleHighlight) {
		//apply the colour to both panes, and check if one application was successful
		boolean applied = vp.setCellColour(n, singleHighlight);
		//short circuit to ensure the colour is applied to fp.
		applied = fp.setCellColour(n, singleHighlight) || applied;

		return applied;
	}

	/**
	 * Colour a given {@link Node} in a given {@link Color} in the {@link VisScrollPane}s.
	 *
	 * @param n               The {@link Node} to highlight
	 * @param c               The {@link Color} in which to highlight n
	 * @param singleHighlight Whether all other colours should be removed (such that this is the only coloured node)
	 * @return <code>true</code> if the colour operation was successful
	 */
	public boolean setCellColour(Node n, Color c, boolean singleHighlight) {
		boolean applied = vp.setCellColour(n, c, singleHighlight);
		applied = fp.setCellColour(n, c, singleHighlight) || applied;

		return applied;
	}

	/**
	 * Defines which {@link VisScrollPane}s {@link VisScrollPane#isClickedUpdated()} method was most recently updated.
	 */
	private enum MostRecentlyUpdated {
		NONE,
		FP,
		VP
	}

}
