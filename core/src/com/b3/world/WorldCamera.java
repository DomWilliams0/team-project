package com.b3.world;

import com.b3.entity.Agent;
import com.b3.entity.component.PhysicsComponent;
import com.b3.input.KeyboardController;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
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

	private PhysicsComponent followedAgent;
	private float zoomAmount;

	private float posX;
	private float posY;
	private float posZ;

	// todo redo camera restriction

	public WorldCamera(float fieldOfViewY, float viewportWidth, float viewportHeight) {
		super(fieldOfViewY, viewportWidth, viewportHeight);

		lastPosition = null;
		borders = new ArrayList<>(4);
		inputDelta = new Vector2();
		followedAgent = null;

		posX = 0; posY = 0; posZ = 0;
	}

	/**
	 * Sets the camera boundaries to the given world's borders
	 *
	 * @param world The world
	 */
	public void setWorld(World world) {
		lastPosition = position;

		Vector2 worldSize = world.getTileSize();

		int size = 1;
		if (Config.getBoolean(ConfigKey.CAMERA_RESTRICT)) {
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
		posX = posX + x;
		posY = posY + y;
		posZ = posZ + z;
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
	 * Polls the given keyboard controller, and moves/zooms appropriately
	 */
	public void move(KeyboardController keyboardController) {
		if (followedAgent != null) {
			trackEntity();
		} else {
			keyboardController.pollMovement(inputDelta, Config.getFloat(ConfigKey.CAMERA_MOVE_SPEED));
			translateSafe(inputDelta.x, inputDelta.y, 0f);
		}

		int zoom = keyboardController.pollZoom();
		if (zoom != 0)
			zoom(zoom * Config.getFloat(ConfigKey.CAMERA_ZOOM_SPEED)); // that's a lot of zooms
	}

	/**
	 * Zooms by the given amount, given that the new zoom value is within the zoom bounds
	 *
	 * @param delta The amount to zoom in/out; negative zooms out, positive zooms in
	 */
	public void zoom(float delta) {
		float newZ = position.z + delta;

		if (newZ >= Config.getFloat(ConfigKey.CAMERA_DISTANCE_MINIMUM)
				&& newZ <= Config.getFloat(ConfigKey.CAMERA_DISTANCE_MAXIMUM))
			translateSafe(0f, 0f, delta);

		zoomAmount = newZ;
	}

	public void setCurrrentZoom (float zoom) {
		float newZ = zoom;
		float delta = newZ - position.z;

		if (newZ >= Config.getFloat(ConfigKey.CAMERA_DISTANCE_MINIMUM)
				&& newZ <= Config.getFloat(ConfigKey.CAMERA_DISTANCE_MAXIMUM))
			translateSafe(0f, 0f, delta);

		zoomAmount = newZ;
	}

	public float getActualZoom() {
		return zoomAmount;
	}

	public float getCurrentZoom() {
		if (zoomAmount > 30) {
			return zoomAmount - 30;
		} else return 1;
	}

	private void trackEntity() {
		position.set(followedAgent.getPosition(), position.z);
		update();
	}

	public void setFollowedAgent(Agent agent) {
		followedAgent = agent.getPhysicsComponent();
	}

	public float getPosX() {
		return posX;
	}

	public float getPosY() {
		return posY;
	}

	public float getPosZ() {
		return posZ;
	}
}
