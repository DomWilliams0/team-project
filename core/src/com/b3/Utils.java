package com.b3;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.util.Random;

public class Utils {

	public static final float WORLD_SCALE = 8f;
	public static final float CAMERA_HEIGHT = 60f;

	public static final Random RANDOM = new Random();

	public static Vector2 tileToPixel(Vector2 v) {
		return new Vector2(v.x * WORLD_SCALE, v.y * WORLD_SCALE);
	}

	public static Vector2 pixelToTile(Vector2 v) {
		return new Vector2(v.x / WORLD_SCALE, v.y / WORLD_SCALE);
	}

	public static Vector3 tileToPixel(Vector3 v) {
		return new Vector3(v.x * WORLD_SCALE, v.y * WORLD_SCALE, v.z * WORLD_SCALE);
	}

	public static Vector3 pixelToTile(Vector3 v) {
		return new Vector3(v.x / WORLD_SCALE, v.y / WORLD_SCALE, v.z / WORLD_SCALE);
	}

	public static int randomRange(int min, int max) {
		return RANDOM.nextInt((max - min) + 1) + min;
	}

	public static float randomRange(float min, float max) {
		return RANDOM.nextFloat() * (max - min) + min;
	}
}

