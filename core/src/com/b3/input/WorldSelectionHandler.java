package com.b3.input;

import com.b3.entity.ai.Behaviour;
import com.b3.entity.ai.BehaviourMultiPathFind;
import com.b3.gui.CoordinatePopup;
import com.b3.gui.ErrorPopups;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.WorldGraph;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.world.BuildingType;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class WorldSelectionHandler extends InputAdapter {

	private static final Vector3 tempRayCast = new Vector3();
	private World world;
	private Point currentSelection;

	public WorldSelectionHandler(World world) {
		this.world = world;
		this.currentSelection = new Point(1,1);
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		//TODO selecting an entity

		//Close error pop-ups if need
		if (ErrorPopups.justOpen) {
			ErrorPopups.shouldClose = true;
			return false;
		}

		//if near bottom of screen hitting the intensive learning mode button so ignore
		if (screenX < 100 && screenY > Gdx.graphics.getHeight()-100) {
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
			System.out.println("Add building @ "+(int)tempRayCast.x+"|"+(int)tempRayCast.y);
			if (world.isValidBuildingPos((int)tempRayCast.x, (int)tempRayCast.y))
				world.addBuilding(new Vector2((int)tempRayCast.x, (int)tempRayCast.y), new Vector3(4, 4, 10), BuildingType.HOUSE);
			Config.set(ConfigKey.ADD_BUILDING_MODE, !(Config.getBoolean(ConfigKey.ADD_BUILDING_MODE)));

			boolean flatBuildings = Config.getBoolean(ConfigKey.FLATTEN_BUILDINGS);
			world.flattenBuildings(flatBuildings);
		}

		if (Config.getBoolean(ConfigKey.REMOVE_BUILDING_MODE)) {
			System.out.println("Remove building @ "+(int)tempRayCast.x+"|"+(int)tempRayCast.y);
			world.removeBuilding(new Vector2((int) tempRayCast.x, (int) tempRayCast.y));
			Config.set(ConfigKey.REMOVE_BUILDING_MODE, !(Config.getBoolean(ConfigKey.REMOVE_BUILDING_MODE)));

			boolean flatBuildings = Config.getBoolean(ConfigKey.FLATTEN_BUILDINGS);
			world.flattenBuildings(flatBuildings);
		}

		// Check if node page no. should be incremented or reset to beginning (as clicked on different node)
		if (currentSelection.x == (int) tempRayCast.x && currentSelection.y == (int) tempRayCast.y) {
			//old node so change page number
			if (world.getrt().getPopupShowing())
				//if popup showing
				world.getrt().resetCounterAnimation();
				world.getrt().flipPageRight();
		} else {
			//new node so reset page number
			if (world.getrt().getPopupShowing())
				//if popup showing
				world.getrt().resetPage();
		}

		currentSelection = new Point((int) tempRayCast.x, (int) tempRayCast.y);

		if (node == null)
			return false;

		System.out.println("You clicked node: " + node.getPoint());

		if (button == Input.Buttons.LEFT)
			world.setCurrentClick(node.getPoint().getX(), node.getPoint().getY());
		else {
			world.setNextDestination(node.getPoint().getX(), node.getPoint().getY());
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

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// selecting a tile
		WorldCamera worldCamera = world.getWorldCamera();
		Ray ray = worldCamera.getPickRay(screenX, screenY);
		ray.getEndPoint(tempRayCast, worldCamera.position.z);

		world.setCurrentMousePos((int)tempRayCast.x, (int) tempRayCast.y);

		WorldGraph worldGraph = world.getWorldGraph();
		Node node = worldGraph.getNode(new Point((int) tempRayCast.x, (int) tempRayCast.y));

		if (node != null) {
			CoordinatePopup.visibility = true;
			CoordinatePopup.setCoordinate(node.getPoint().getX(), node.getPoint().getY());
		} else CoordinatePopup.visibility = false;

		return super.mouseMoved(screenX, screenY);
	}
}
