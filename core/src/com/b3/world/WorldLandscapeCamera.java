package com.b3.world;

import com.b3.search.util.Function2;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Matrix4;

/**
 * A world camera that renders a landscape behind
 * an existing world
 */
public class WorldLandscapeCamera extends WorldCamera {

	private WorldCamera mainWorld;
	private float renderOffsetX;
	private float renderOffsetY;

	/**
	 * @param fieldOfViewY The field of view of the height, in degrees, the field of view for
	 *                     the width will be calculated according to the aspect ratio.
	 * @param tmx          The TileMap to render
	 * @param startX       The starting X coordinate
	 * @param startY       The starting Y coordinate
	 * @param startZoom    The starting Z coordinate
	 */
	public WorldLandscapeCamera(float fieldOfViewY, TiledMap tmx, float startX, float startY, float startZoom, WorldCamera mainWorld) {
		super(fieldOfViewY, LandscapeGenerator.generate(tmx), startX, startY, startZoom);
		this.mainWorld = mainWorld;

		TiledMapTileLayer landscapeLayer = map.getLayers().getByType(TiledMapTileLayer.class).first();
		TiledMapTileLayer originalLayer = tmx.getLayers().getByType(TiledMapTileLayer.class).first();

		renderOffsetX = -landscapeLayer.getWidth() / 2 + originalLayer.getWidth() / 2;
		renderOffsetY = -landscapeLayer.getHeight() / 2 + originalLayer.getHeight() / 2;
	}


	@Override
	public void renderWorld() {
		combined.set(new Matrix4(mainWorld.combined)
				.translate(renderOffsetX, renderOffsetY, 0f));
		super.renderWorld();
	}

	private static class LandscapeGenerator {

		private TiledMap landscapeMap;
		private TiledMapTileLayer exampleLayer, landscapeLayer;

		private int width, height;

		public LandscapeGenerator(TiledMap originalMap) {
			exampleLayer = originalMap.getLayers().getByType(TiledMapTileLayer.class).first();

			landscapeMap = new TiledMap();
			width = height = 60; // todo calculate
			landscapeLayer = new TiledMapTileLayer(width, height,
					(int) exampleLayer.getTileWidth(), (int) exampleLayer.getTileHeight());

			expandTilesVertically(this::getExampleCell);
			expandTilesHorizontally(this::getExampleCell);
			expandTilesVertically(this::getLandscapeCell);

			landscapeMap.getTileSets().addTileSet(originalMap.getTileSets().getTileSet(0));
			landscapeMap.getLayers().add(landscapeLayer);
		}

		private void expandTilesHorizontally(Function2<Integer, Integer, TiledMapTileLayer.Cell> cellGetter) {
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					TiledMapTileLayer.Cell cell = cellGetter.apply(x, y);
					if (cell != null) {
						setRow(cell, y, 0, x);
						break;
					}
				}

			}
			for (int y = 0; y < height; y++) {
				for (int x = width - 1; x >= 0; x--) {
					TiledMapTileLayer.Cell cell = cellGetter.apply(x, y);
					if (cell != null) {
						setRow(cell, y, x, width);
						break;
					}
				}
			}
		}

		private void expandTilesVertically(Function2<Integer, Integer, TiledMapTileLayer.Cell> cellGetter) {
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					TiledMapTileLayer.Cell cell = cellGetter.apply(x, y);
					if (cell != null) {
						setColumn(cell, x, 0, y);
						break;
					}
				}

			}
			for (int x = 0; x < width; x++) {
				for (int y = height - 1; y >= 0; y--) {
					TiledMapTileLayer.Cell cell = cellGetter.apply(x, y);
					if (cell != null) {
						setColumn(cell, x, y, height);
						break;
					}
				}
			}
		}

		private TiledMapTileLayer.Cell getExampleCell(int x, int y) {
			return exampleLayer.getCell(
					x - width / 2 + exampleLayer.getWidth() / 2,
					y - height / 2 + exampleLayer.getHeight() / 2);
		}

		private TiledMapTileLayer.Cell getLandscapeCell(int x, int y) {
			return landscapeLayer.getCell(x, y);
		}

		private void setColumn(TiledMapTileLayer.Cell cell, int column, int from, int to) {
			for (int y = from; y < to; y++)
				landscapeLayer.setCell(column, y, cell);
		}

		private void setRow(TiledMapTileLayer.Cell cell, int row, int from, int to) {
			for (int x = from; x < to; x++)
				landscapeLayer.setCell(x, row, cell);
		}


		public static TiledMap generate(TiledMap tmx) {
			return new LandscapeGenerator(tmx).landscapeMap;
		}
	}
}
