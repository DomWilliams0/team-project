package com.b3.util;

import java.util.Random;

public class Utils {

	public static final int TILESET_RESOLUTION = 16;
	public static final float TILE_SIZE = 4f;
	public static final float WORLD_SCALE = TILESET_RESOLUTION / TILE_SIZE;

	public static final Random RANDOM = new Random();

	public static float randomRange(float min, float max) {
		return RANDOM.nextFloat() * (max - min) + min;
	}

}

