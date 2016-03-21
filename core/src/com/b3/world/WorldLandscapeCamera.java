package com.b3.world;

import com.b3.search.util.Function2;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Matrix4;

/**
 * A world camera that renders a landscape behind
 * an existing world
 *
 * @author dxw405
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

	/**
	 * Generates a landscape TiledMap, extrapolating
	 * the tiles on the edges of the given TiledMap
	 */
	private static class LandscapeGenerator {

		private TiledMap landscapeMap;
		private TiledMapTileLayer exampleLayer, landscapeLayer;

		private int width, height;
		private float scale;

		/**
		 * @param originalMap The TiledMap to extrapolate
		 */
		public LandscapeGenerator(TiledMap originalMap) {
			exampleLayer = originalMap.getLayers().getByType(TiledMapTileLayer.class).first();

			landscapeMap = new TiledMap();
			scale = 3f;
			width = (int) (exampleLayer.getWidth() * scale);
			height = (int) (exampleLayer.getHeight() * scale);


			landscapeLayer = createLayer();

			// expand terrain
			expandTilesVertically(this::getExampleCell);
			expandTilesHorizontally(this::getExampleCell);
			expandTilesVertically(this::getLandscapeCell);

			landscapeMap.getTileSets().addTileSet(originalMap.getTileSets().getTileSet(0));
		}

		/**
		 * Creates a new layer based on the exampleLayer and adds it to the landscape map
		 *
		 * @return The newly created layer
		 */
		private TiledMapTileLayer createLayer() {
			TiledMapTileLayer newLayer = new TiledMapTileLayer(width, height,
					(int) exampleLayer.getTileWidth(), (int) exampleLayer.getTileHeight());

			landscapeMap.getLayers().add(newLayer);
			return newLayer;
		}

		/**
		 * Extrapolates border tiles horizontally
		 *
		 * @param cellGetter A function that returns a cell at given coordinates
		 */
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

		/**
		 * Extrapolates border tiles vertically
		 *
		 * @param cellGetter A function that returns a cell at given coordinates
		 */
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

		/**
		 * Gets the cell in the original world at the given scaled coordinates
		 *
		 * @param x Landscape scale X coordinate
		 * @param y Landscape scale Y coordinate
		 * @return The cell at the given coordinates
		 */
		private TiledMapTileLayer.Cell getExampleCell(int x, int y) {
			return exampleLayer.getCell(
					x - width / 2 + exampleLayer.getWidth() / 2,
					y - height / 2 + exampleLayer.getHeight() / 2);
		}

		/**
		 * Gets the cell in the landscape world at the given coordinates
		 *
		 * @param x X coordinate
		 * @param y Y coordinate
		 * @return The cell at the given coordinates
		 */
		private TiledMapTileLayer.Cell getLandscapeCell(int x, int y) {
			return landscapeLayer.getCell(x, y);
		}

		/**
		 * Sets the whole given column to the given tile
		 *
		 * @param cell   The cell to fill up with
		 * @param column Index of the column to fill up
		 * @param from   Index of the row to fill from
		 * @param to     Index of the row to fill to
		 */
		private void setColumn(TiledMapTileLayer.Cell cell, int column, int from, int to) {
			for (int y = from; y < to; y++)
				landscapeLayer.setCell(column, y, cell);
		}

		/**
		 * Sets the whole given row to the given tile
		 *
		 * @param cell The cell to fill up with
		 * @param row  Index of the row to fill up
		 * @param from Index of the column to fill from
		 * @param to   Index of the column to fill to
		 */
		private void setRow(TiledMapTileLayer.Cell cell, int row, int from, int to) {
			for (int x = from; x < to; x++)
				landscapeLayer.setCell(x, row, cell);
		}


		/**
		 * @param tmx The tiled map to generate a landscape for
		 * @return The newly generated landscape tiled map
		 */
		public static TiledMap generate(TiledMap tmx) {
			return new LandscapeGenerator(tmx).landscapeMap;
		}
	}
}
