package com.b3.input;

import com.b3.entity.ai.Behaviour;
import com.b3.entity.ai.BehaviourMultiPathFind;
import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.search.WorldGraph;
import com.b3.world.World;
import com.b3.world.WorldCamera;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class WorldSelectionHandler extends InputAdapter {
	private static final Vector3 tempRayCast = new Vector3();


	private World world;

	public WorldSelectionHandler(World world) {
		this.world = world;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		// just left click
		if (button != Input.Buttons.LEFT)
			return false;

		// selecting an entity
		// todo

		// selecting a tile
		WorldCamera worldCamera = world.getWorldCamera();
		Ray ray = worldCamera.getPickRay(screenX, screenY);
		ray.getEndPoint(tempRayCast, worldCamera.position.z);

		// convert to node
		WorldGraph worldGraph = world.getWorldGraph();
		Node node = worldGraph.getNode(new Point((int) tempRayCast.x, (int) tempRayCast.y));

		if (node == null)
			return false;

		System.out.println("You clicked node: " + node.getPoint());

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
}
