package com.b3.world;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

import java.util.HashMap;
import java.util.Map;

/**
 * A mapping of tile types to their IDs in the tileset
 *
 * @author dxw405 bxd428
 */
public enum TileType {


	GRASS(2, 964),

	PAVEMENT(1, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 46,
			47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 83, 84, 85,
			86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 120, 121, 122, 123,
			124, 125, 126, 127, 128, 129, 130, 131, 132, 133, 134, 135),

	ROAD(10, 713, 714, 715, 716, 717, 718, 750, 751, 752, 753, 754, 755, 787,
			788,
			789, 790, 791, 792, 793, 794, 795, 796, 797, 861, 862, 863, 864, 865,
			866, 830, 831, 832, 833, 834, 898, 899, 900, 901, 902, 903, 867, 868,
			869, 870, 871, 904, 905, 906, 907, 908),

	ZEBRA_CROSSING(1, 824, 825, 826, 827, 828, 829),
	
	RIVER(0, 212, 214),

	COBBLESTONE(1, 704, 705, 706, 707, 708, 709, 710, 711, 712, 741, 742,
			743, 744,
			745, 746, 747, 748, 749, 778, 779, 780, 781, 782, 783, 784, 785, 786,
			815, 816, 817, 818, 819, 820, 821, 822, 823, 852, 853, 854, 855, 856,
			857, 858, 859, 860),

	TREE(0, 513),

	UNKNOWN(0);

	private static final Map<Integer, TileType> TILES;

	static {
		TILES = new HashMap<>();
		for (TileType tileType : values())
			for (int id : tileType.ids) {
				TILES.put(id, tileType);
				tileType.ids = null;
			}
	}

	private final int cost;
	private int[] ids;

	TileType(int cost, int... ids) {
		this.cost = cost;
		this.ids = ids;
	}

	/**
	 * @param id The tile ID to lookup
	 * @return The TileType corresponding to this ID,
	 * or {@link #UNKNOWN} if not found.
	 */
	public static TileType getByID(int id) {
		return TILES.getOrDefault(id, UNKNOWN);
	}

	/**
	 * @param cell The cell to lookup. Can be <code>null</code>
	 * @return The TileType corresponding to this cell,
	 * or {@link #UNKNOWN} if not found.
	 * @see {@link TileType#getByID(int)}
	 */
	public static TileType getFromCell(TiledMapTileLayer.Cell cell) {
		if (cell == null)
			return UNKNOWN;
		return getByID(cell.getTile().getId());
	}

	/**
	 * @return The cost of this TileType
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * @return <code>true</code> if this TileType has a positive, non-zero cost;
	 * <code>false</code> otherwise.
	 */
	public boolean shouldHaveNode() {
		return cost > 0;
	}
}
