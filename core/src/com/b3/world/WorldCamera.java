package com.b3.world;

import com.b3.input.KeyboardController;
import com.b3.util.Config;
import com.b3.util.ConfigKey;
import com.b3.util.Utils;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * The control class for the main {@link com.badlogic.gdx.graphics.Camera}.
 *
 * @author dxw405
 */
public class WorldCamera extends PerspectiveCamera {

	private final Vector2 inputDelta;
	private float zoomAmount;

	private TiledMapRenderer renderer;
	protected final TiledMap map;

	/**
	 * @param fieldOfViewY The field of view of the height, in degrees, the field of view for
	 *                     the width will be calculated according to the aspect ratio.
	 * @param tmx          The TileMap to render
	 * @param startX       The starting X coordinate
	 * @param startY       The starting Y coordinate
	 * @param startZoom    The starting Z coordinate
	 */
	public WorldCamera(float fieldOfViewY, TiledMap tmx, float startX, float startY, float startZoom) {
		super(fieldOfViewY, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		position.set(startX, startY, startZoom);
		near = 1f;
		far = 300f;
		lookAt(startX, startY, 0);
		update();

		inputDelta = new Vector2();

		renderer = new OrthogonalTiledMapRenderer(tmx, 1f / Utils.TILESET_RESOLUTION);
		map = tmx;
	}

	/**
	 * Renders the tilemap
	 */
	public void renderWorld() {
		renderer.setView(combined,
				position.x - viewportWidth / 2,
				position.y - viewportHeight / 2,
				viewportWidth, viewportHeight);
		renderer.render();
	}

	/**
	 * Polls the given keyboard controller, and moves/zooms appropriately
	 */
	public void move(KeyboardController keyboardController) {
		keyboardController.pollMovement(inputDelta, Config.getFloat(ConfigKey.CAMERA_MOVE_SPEED));
		translate(inputDelta.x, inputDelta.y, 0f);

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

		if (isZoomInRange(newZ))
			translate(0f, 0f, delta);

		zoomAmount = newZ;
	}

	/**
	 * Sets the current zoom level with a smooth animation.
	 * Obeys the max and min config limits.
	 *
	 * @param zoom The new zoom level.
	 */
	public void setCurrentZoom(float zoom) {
		float delta = zoom - position.z;

		if (isZoomInRange(zoom))
			translate(0f, 0f, delta);

		zoomAmount = zoom;
	}

	private boolean isZoomInRange(float newZoom) {

		return (newZoom >= Config.getFloat(ConfigKey.CAMERA_DISTANCE_MINIMUM)
				&& newZoom <= Config.getFloat(ConfigKey.CAMERA_DISTANCE_MAXIMUM));
	}

	/**
	 * @return the z position of the camera.
	 */
	public float getActualZoom() {
		return zoomAmount;
	}

	/**
	 * @return 1 if high contrast mode shouldn't be shown, >1 if it should
	 */
	public float getCurrentZoom() {
		if (zoomAmount > 30) {
			return zoomAmount - 30;
		} else return 1;
	}

	/**
	 * @return the x position
	 */
	public float getPosX() {
		return position.x;
	}

	/**
	 * @return the y position
	 */
	public float getPosY() {
		return position.y;
	}

}
