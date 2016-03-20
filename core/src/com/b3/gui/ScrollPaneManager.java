package com.b3.gui;

import com.b3.search.Node;
import com.b3.search.Point;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import java.util.List;

/**
 * @author lxd417
 * TODO JAVADOC
 */
public class ScrollPaneManager {

	private VisScrollPane vp, fp;
	private MostRecentlyUpdated mru = MostRecentlyUpdated.NONE;


	public ScrollPaneManager(Stage stage, VisNodes vn) {
		Skin skin = vn.getSkin();

		ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
		style.background = vn.getBackground();
		style.hScroll = skin.getDrawable("scroll_back_hor");
		style.hScrollKnob = skin.getDrawable("knob_02");
		style.vScroll = skin.getDrawable("scroll_back_ver");
		style.vScrollKnob = skin.getDrawable("knob_02");

		vp = new VisScrollPane(skin, style, false, vn);
		fp = new VisScrollPane(skin, style, true, vn);

		stage.addActor(vp);
		stage.addActor(fp);
	}

	public void addNodes(List<Node> frontier, List<Node> visited) {
		vp.add(visited);
		fp.add(frontier);
	}

	public void clear() {
		vp.clearTable();
		fp.clearTable();
	}

	public VisScrollPane getVp() {
		return vp;
	}

	public VisScrollPane getFp() {
		return fp;
	}

	public boolean scrollpanesBeingUsed() {
		return vp.scrollpaneBeingUsed() || fp.scrollpaneBeingUsed();
	}

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

	public Point getClickedNode() {
		switch (mru) {
			case FP: return fp.getClickedNode();
			case VP: return vp.getClickedNode();
		}
		//problems...
		return null;
	}

	public boolean setCellColour(Node n, boolean singleHighlight) {
		boolean applied = vp.setCellColour(n, singleHighlight);
		applied = fp.setCellColour(n, singleHighlight) || applied;

		return applied;
	}

	public boolean setCellColour(Node n, Color c, boolean singleHighlight) {
		boolean applied = vp.setCellColour(n, c, singleHighlight);
		applied = fp.setCellColour(n, c, singleHighlight) || applied;

		return applied;
	}

	private enum MostRecentlyUpdated {
		NONE,
		FP,
		VP
	}
}
