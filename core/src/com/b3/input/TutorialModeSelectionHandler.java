package com.b3.input;

import com.b3.gui.PopupDescription;
import com.b3.mode.TutorialMode;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.WorldGraph;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * A listener class for use with tutorial mode
 *
 * @author nbg481
 */
public class TutorialModeSelectionHandler extends InputAdapter {

	private static final Vector3 tempRayCast = new Vector3();
	private final TutorialMode tutorialMode;
	private World world;
	private Point currentSelection;

	/**
	 * initialises the input listeners for the tutorial mode
	 *
	 * @param world        the world that this listener is linked to
	 * @param tutorialMode the instance of tutorialMode that this listener is linked to
	 */
	public TutorialModeSelectionHandler(World world, TutorialMode tutorialMode) {
		this.world = world;
		this.currentSelection = new Point(1, 1);
		this.tutorialMode = tutorialMode;
	}

	/**
	 * Defines what happens on touch down: firstly close errors, then show pop-ups
	 *
	 * @param screenX the x position of the mouse
	 * @param screenY the y position of the mouse
	 * @param button  left or right click
	 * @return true if clicked
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// selecting a tile
		WorldCamera worldCamera = world.getWorldCamera();
		Ray ray = worldCamera.getPickRay(screenX, screenY);
		ray.getEndPoint(tempRayCast, worldCamera.position.z);

		// convert to node
		WorldGraph worldGraph = world.getWorldGraph();
		Node node = worldGraph.getNode(new Point((int) tempRayCast.x, (int) tempRayCast.y));

		if (node != null)
			tutorialMode.setCurrentPos(node.getPoint());

		if (!tutorialMode.needPopups())
			return true;

		// Check if node page no. should be incremented or reset to beginning (as clicked on different node)
		PopupDescription popupDescription = world.getWorldGUI().getPopupDescription();
		if (currentSelection.x == (int) tempRayCast.x && currentSelection.y == (int) tempRayCast.y) {
			// old node so change page number
			if (popupDescription.getPopupShowing())
				// if popup showing
				popupDescription.resetCounterAnimation();
			popupDescription.flipPageRight();
		} else {
			// new node so reset page number
			if (popupDescription.getPopupShowing())
				// if popup showing
				popupDescription.resetPage();
		}

		tutorialMode.setCurrentPage(popupDescription.getPageNo());

		currentSelection = new Point((int) tempRayCast.x, (int) tempRayCast.y);

		if (node != null) {
			if (button == Input.Buttons.LEFT) {
				world.getWorldGUI().setCurrentClick(node.getPoint().getX(), node.getPoint().getY());
			} else {
				world.getWorldGUI().setNextDestination(node.getPoint().getX(), node.getPoint().getY());
			}
		}
		return true;
	}

}
