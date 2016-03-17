package com.b3.world;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Matrix4;

/**
 * A world camera that renders a landscape behind
 * an existing world
 */
public class WorldLandscapeCamera extends WorldCamera {

	private WorldCamera mainWorld;
	private float landscapeWidth;
	private float landscapeHeight;

	/**
	 * @param fieldOfViewY The field of view of the height, in degrees, the field of view for
	 *                     the width will be calculated according to the aspect ratio.
	 * @param tmx          The TileMap to render
	 * @param startX       The starting X coordinate
	 * @param startY       The starting Y coordinate
	 * @param startZoom    The starting Z coordinate
	 */
	public WorldLandscapeCamera(float fieldOfViewY, TiledMap tmx, float startX, float startY, float startZoom, WorldCamera mainWorld) {
		super(fieldOfViewY, tmx, startX, startY, startZoom);
		this.mainWorld = mainWorld;

		TiledMapTileLayer layer = (TiledMapTileLayer) tmx.getLayers().get(0);
		landscapeWidth = -layer.getWidth() / 3f;
		landscapeHeight = -layer.getHeight() / 3f;
	}


	@Override
	public void renderWorld() {
		combined.set(new Matrix4(mainWorld.combined)
				.translate(landscapeWidth, landscapeHeight, 0f));
		super.renderWorld();
	}
}
