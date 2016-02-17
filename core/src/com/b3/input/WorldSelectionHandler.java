package com.b3.input;

import com.b3.search.Node;
import com.b3.search.Point;
import com.b3.world.World;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;

public class WorldSelectionHandler extends InputAdapter {
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
		Vector3 worldPos = world.getWorldCamera().unproject(new Vector3(screenX, screenY, 1f));
		Node node = world.getWorldGraph().getNode(new Point((int) worldPos.x, (int) worldPos.y));

		if (node == null)
			return false;

		System.out.println("You clicked node: " + node.getPoint());

		return true;
	}
}
