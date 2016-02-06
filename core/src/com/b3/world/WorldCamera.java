package com.b3.world;

import com.b3.InputHandler;
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

	private float closestZoom;
	private float furthestZoom;
	private int zoomSpeed;

	public WorldCamera(float fieldOfViewY, float viewportWidth, float viewportHeight) {
		super(fieldOfViewY, viewportWidth, viewportHeight);

		lastPosition = null;
		borders = new ArrayList<>(4);
		inputDelta = new Vector2();

		closestZoom = 10f;
		furthestZoom = 100f;
		zoomSpeed = 3;
	}

	public void setWorld(World world) {
		lastPosition = position;

		Vector2 worldSize = world.getPixelSize();

		int size = 1;
		borders.add(new BoundingBox(new Vector3(0, 0, 0), new Vector3(-size, worldSize.y, 0))); // left
		borders.add(new BoundingBox(new Vector3(0, 0, 0), new Vector3(worldSize.x, size, 0))); // bottom
		borders.add(new BoundingBox(new Vector3(0, worldSize.y, 0), new Vector3(worldSize.x, worldSize.y + size, 0))); // top
		borders.add(new BoundingBox(new Vector3(worldSize.x, 0, 0), new Vector3(worldSize.x + size, worldSize.y, 0))); // right
	}

	@Override
	public void translate(float x, float y, float z) {
		lastPosition = new Vector3(position);
		super.translate(x, y, z);
	}

	public void translateSafe(float x, float y, float z) {
		translate(x, y, z);
		update();
		ensureBounds();
		update();
	}

	public void ensureBounds() {
		if (borders.stream().anyMatch(frustum::boundsInFrustum))
			position.set(lastPosition);
	}

	public void positionMapRenderer(TiledMapRenderer renderer) {
		renderer.setView(combined,
				position.x - viewportWidth / 2,
				position.y - viewportHeight / 2,
				viewportWidth, viewportHeight);
	}

	public void move(InputHandler inputHandler) {
		inputHandler.pollMovement(inputDelta, 1f);
		translateSafe(inputDelta.x, inputDelta.y, 0f);

		int zoom = inputHandler.pollZoom();
		if (zoom != 0)
			zoom(zoom * zoomSpeed); // that's a lot of zooms
	}

	public void zoom(float delta) {
		float newZ = position.z + delta;

		if (newZ >= closestZoom && newZ <= furthestZoom)
			translateSafe(0f, 0f, delta);
	}
}
