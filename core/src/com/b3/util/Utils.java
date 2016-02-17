package com.b3.util;

import com.b3.gui.NodeComparator;
import com.b3.search.Point;
import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public class Utils {

	/**
	 * A comparator to compare two
	 * {@link com.b3.search.Node Nodes} by x
	 * coordinate then y coordinate.
	 */
	public static final NodeComparator NODE_COMPARATOR = new NodeComparator();

	public static final int TILESET_RESOLUTION = 16;
	public static final float TILE_SIZE = 4f;
	public static final float WORLD_SCALE = TILESET_RESOLUTION / TILE_SIZE;

	public static final Random RANDOM = new Random();

	/**
	 * The time elapsed since the last render.
	 * This will be multiplied by the speed slider.
	 */
	public static float DELTA_TIME = 0;

	/**
	 * The time elapsed since the last render, untouched by game speed.
	 */
	public static float TRUE_DELTA_TIME = 0;

	public static float randomRange(float min, float max) {
		return RANDOM.nextFloat() * (max - min) + min;
	}

	public static Point vector2ToPoint(Vector2 vector2) {
		return new Point(Math.round(vector2.x), Math.round(vector2.y));
	}

}

