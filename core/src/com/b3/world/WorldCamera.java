package com.b3.world;

import com.b3.InputHandler;
import com.b3.util.Config;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import java.util.ArrayList;
import java.util.List;

public class WorldCamera extends PerspectiveCamera {

	private List<BoundingBox> borders;
	private Vector3 lastPosition;
	private Vector2 inputDelta;

	public WorldCamera(float fieldOfViewY, float viewportWidth, float viewportHeight) {
		super(fieldOfViewY, viewportWidth, viewportHeight);

		lastPosition = null;
		borders = new ArrayList<>(4);
		inputDelta = new Vector2();
	}

	/**
	 * Sets the camera boundaries to the given world's borders
	 *
	 * @param world The world
	 */
	public void setWorld(World world) {
		lastPosition = position;

		Vector2 worldSize = world.getPixelSize();

		int size = 1;
		if (Config.getBoolean("camera-restrict")) {
			borders.clear();
			borders.add(new BoundingBox(new Vector3(0, 0, 0), new Vector3(-size, worldSize.y, 0))); // left
			borders.add(new BoundingBox(new Vector3(0, 0, 0), new Vector3(worldSize.x, size, 0))); // bottom
			borders.add(new BoundingBox(new Vector3(0, worldSize.y, 0), new Vector3(worldSize.x, worldSize.y + size, 0))); // top
			borders.add(new BoundingBox(new Vector3(worldSize.x, 0, 0), new Vector3(worldSize.x + size, worldSize.y, 0))); // right
		}
	}

	@Override
	public void translate(float x, float y, float z) {
		lastPosition = new Vector3(position);
		super.translate(x, y, z);
	}

	/**
	 * Translates by the given coordinates, and reverts to the last position if moved outside of the boundaries
	 */
	public void translateSafe(float x, float y, float z) {
		translate(x, y, z);
		update();
		ensureBounds();
		update();
	}

	/**
	 * Moves to the last position if currently outside of the boundaries
	 */
	public void ensureBounds() {
		if (borders.stream().anyMatch(frustum::boundsInFrustum))
			position.set(lastPosition);
	}

	/**
	 * Updates the given renderer with the current camera position
	 *
	 * @param renderer The renderer
	 */
	public void positionMapRenderer(TiledMapRenderer renderer) {
		renderer.setView(combined,
				position.x - viewportWidth / 2,
				position.y - viewportHeight / 2,
				viewportWidth, viewportHeight);
	}

	/**
	 * Polls the given InputHandler, and moves/zooms appropriately
	 *
	 * @param inputHandler The input handler
	 */
	public void move(InputHandler inputHandler) {
		inputHandler.pollMovement(inputDelta, Config.getFloat("camera-move-speed"));
		translateSafe(inputDelta.x, inputDelta.y, 0f);

		int zoom = inputHandler.pollZoom();
		if (zoom != 0)
			zoom(zoom * Config.getFloat("camera-zoom-speed")); // that's a lot of zooms
	}

	/**
	 * Zooms by the given amount, given that the new zoom value is within the zoom bounds
	 * @param delta The amount to zoom in/out; negative zooms out, positive zooms in
	 */
	public void zoom(float delta) {
		float newZ = position.z + delta;

		if (newZ >= Config.getFloat("camera-distance-min")
				&& newZ <= Config.getFloat("camera-distance-max"))
			translateSafe(0f, 0f, delta);
	}
}
