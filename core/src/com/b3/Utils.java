package com.b3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

	public static final int TILESET_RESOLUTION = 16;
	public static final float TILE_SIZE = 4f;
	public static final float WORLD_SCALE = TILESET_RESOLUTION / TILE_SIZE;

	public static final Random RANDOM = new Random();

	public static float randomRange(float min, float max) {
		return RANDOM.nextFloat() * (max - min) + min;
	}

	public static ArrayList<Integer> range(int min, int max) {
		ArrayList<Integer> ls = new ArrayList<>();

		for (int i = min; i < max; i++) {
			ls.add(i);
		}

		return ls;
	}

	public static ArrayList<Integer> range(int max) {
		return range(0, max);
	}
}

