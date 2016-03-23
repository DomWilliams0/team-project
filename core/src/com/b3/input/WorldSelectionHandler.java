package com.b3.input;

import com.b3.entity.ai.Behaviour;
import com.b3.entity.ai.BehaviourMultiPathFind;
import com.b3.gui.CoordinatePopup;
import com.b3.gui.PopupDescription;
import com.b3.gui.popup.Popup;
import com.b3.gui.popup.PopupManager;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.WorldGraph;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

/**
 * A selection handler, that deals with input into the world
 * Takes the input and decides what class to call
 *
 * @author dxw405 nbg481
 */
public class WorldSelectionHandler extends InputAdapter {

	protected static final Vector3 tempRayCast = new Vector3();
	protected World world;
	protected Point currentSelection;

	public WorldSelectionHandler(World world) {
		this.world = world;
		this.currentSelection = new Point(1, 1);
	}

	/**
	 * Decides what to do when the user clicks on a place on the scren
	 *
	 * @param screenX the x position of the mouse
	 * @param screenY the y position of the mouse
	 * @param pointer unused
	 * @param button  the button clicked (left, right or middle)
	 * @return true when click is complete
	 */
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		PopupManager popupManager = world.getWorldGUI().getPopupManager();

		// Close error pop-ups if need
		int popupCount = world.getWorldGUI().getPopupManager().length();
		for (int i = 0; i < popupCount; i++) {
			Popup popup = popupManager.getPopup(i);
			if (popup.justOpen) {
				popup.shouldClose = true;
				return false;
			}
		}

		// if near bottom of screen hitting the intensive learning mode button so ignore
		if (screenX < 100 && screenY > Gdx.graphics.getHeight() - 100) {
			return false;
		}

		// selecting a tile
		WorldCamera worldCamera = world.getWorldCamera();
		Ray ray = worldCamera.getPickRay(screenX, screenY);
		ray.getEndPoint(tempRayCast, worldCamera.position.z);

		// convert to node
		WorldGraph worldGraph = world.getWorldGraph();
		Node node = worldGraph.getNode(new Point((int) tempRayCast.x, (int) tempRayCast.y));

		if (Config.getBoolean(ConfigKey.ADD_BUILDING_MODE)) {
			System.out.println("Add building @ " + (int) tempRayCast.x + "|" + (int) tempRayCast.y);
			if (world.isValidBuildingPos((int) tempRayCast.x, (int) tempRayCast.y))
				world.addBuilding(new Vector2((int) tempRayCast.x, (int) tempRayCast.y), new Vector3(4, 4, 10));
			Config.set(ConfigKey.ADD_BUILDING_MODE, !(Config.getBoolean(ConfigKey.ADD_BUILDING_MODE)));

			boolean flatBuildings = Config.getBoolean(ConfigKey.FLATTEN_BUILDINGS);
			world.flattenBuildings(flatBuildings);
		}

		if (Config.getBoolean(ConfigKey.REMOVE_BUILDING_MODE)) {
			System.out.println("Remove building @ " + (int) tempRayCast.x + "|" + (int) tempRayCast.y);
			world.removeBuilding(new Vector2((int) tempRayCast.x, (int) tempRayCast.y));
			Config.set(ConfigKey.REMOVE_BUILDING_MODE, !(Config.getBoolean(ConfigKey.REMOVE_BUILDING_MODE)));

			boolean flatBuildings = Config.getBoolean(ConfigKey.FLATTEN_BUILDINGS);
			world.flattenBuildings(flatBuildings);
		}

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

		currentSelection = new Point((int) tempRayCast.x, (int) tempRayCast.y);

		if (node == null)
			return false;

		if (button == Input.Buttons.LEFT) {
			world.getWorldGUI().setCurrentClick(node.getPoint().getX(), node.getPoint().getY());
		} else {
			world.getWorldGUI().setNextDestination(node.getPoint().getX(), node.getPoint().getY());
		}

		// no search
		if (!worldGraph.hasSearchInProgress())
			return false;

		// add to goals
		Behaviour behaviour = worldGraph.getCurrentSearchAgent().getBehaviour();
		if (!(behaviour instanceof BehaviourMultiPathFind))
			return false;

		BehaviourMultiPathFind multiPathFind = (BehaviourMultiPathFind) behaviour;
		multiPathFind.addNextGoal(node.getPoint().toVector2());

		System.out.println("Added a search goal at " + node.getPoint());

		return true;
	}

	/**
	 * Decides what to do when the mouse is moved on the screen
	 *
	 * @param screenX the x position of the mouse
	 * @param screenY the y position of the mouse
	 * @return
	 */
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// selecting a tile
		WorldCamera worldCamera = world.getWorldCamera();
		Ray ray = worldCamera.getPickRay(screenX, screenY);
		ray.getEndPoint(tempRayCast, worldCamera.position.z);

		world.getWorldGUI().setCurrentMousePos((int) tempRayCast.x, (int) tempRayCast.y);

		WorldGraph worldGraph = world.getWorldGraph();
		Node node = worldGraph.getNode(new Point((int) tempRayCast.x, (int) tempRayCast.y));

		if (node != null) {
			CoordinatePopup.visibility = true;
			CoordinatePopup.setCoordinate(node.getPoint().getX(), node.getPoint().getY());
		} else {
			CoordinatePopup.visibility = false;
		}

		return super.mouseMoved(screenX, screenY);
	}
}
