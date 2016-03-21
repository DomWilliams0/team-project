package com.b3.util;

import com.b3.search.Point;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * All the methods and values that are not specific to one class,
 * that may be used by multiple classes.
 *
 * @author dxw405 bxd428 oxe410
 */
public abstract class Utils {

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

	/**
	 * Prevent instantiation.
	 * Everything should be <code>static</code>.
	 */
	private Utils() {
	}

	/**
	 * Generates a random number between a range.
	 *
	 * @param min The minimum number that may be returned.
	 * @param max The maximum number that may be returned.
	 * @return A randomly generated number between a range.
	 */
	public static float randomRange(float min, float max) {
		return RANDOM.nextFloat() * (max - min) + min;
	}

	/**
	 * Converts a {@link Point} to a {@link Vector2}.
	 *
	 * @param p The {@link Point} to convert.
	 * @return A new {@link Vector2} which has the same coordinates as {@code p}.
	 */
	public static Vector2 pointToVector2(Point p) {
		return new Vector2(p.x, p.y);
	}

	/**
	 * Generates a {@link List} of {@link Integer Integers} from one value to another.
	 *
	 * @param min The number to start at. (Inclusive in the list)
	 * @param max The number to stop at. (Exclusive in the list)
	 * @return A new {@link List} of {@link Integer Integers} from {@code min} (inclusive) to {@code max} (exclusive).
	 */
	public static List<Integer> range(int min, int max) {
		List<Integer> list = new ArrayList<>(max - min);

		for (int i = min; i < max; i++) {
			list.add(i);
		}

		return list;
	}

	/**
	 * A font cache from name and size to {@link BitmapFont}.
	 */
	private static final HashMap<Tuple<String, Integer>, BitmapFont> fontCache = new HashMap<>();

	/**
	 * Generates a {@link BitmapFont} with a specified name and size.
	 * Uses caching.
	 *
	 * @param name The name of the font with file extension.
	 * @param size The size to generate.
	 * @return The cached or newly generated {@link BitmapFont} using the arguments specified.
	 */
	public static BitmapFont getFont(String name, int size) {
		Tuple<String, Integer> tuple = new Tuple<>(name, size);
		BitmapFont font = fontCache.get(tuple);
		if (font != null)
			return font;

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/" + name));
		FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
		parameter.size = size;
		font = generator.generateFont(parameter);
		generator.dispose();

		fontCache.put(tuple, font);

		return font;
	}

}

