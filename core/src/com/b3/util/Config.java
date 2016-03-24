package com.b3.util;

import com.b3.search.util.SearchAlgorithm;
import com.badlogic.gdx.graphics.Color;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * The value of every setting should be set and got through this class.
 * Config is originally loaded from (a) file(s) and stored in a {@link Map}.
 *
 * @author dxw405
 */
public class Config {

	private static ConfigurationFile configFile;

	/**
	 * This is a local override of the user config files.
	 * If a config value is set using {@link Config#set(ConfigKey, Object)},
	 * it will override the value set in the user config, unless it is
	 * unset using {@link Config#unset(ConfigKey)}
	 */
	private static final Map<ConfigKey, Object> gameConfig = new EnumMap<>(ConfigKey.class);

	/**
	 * Prevent instantiation.
	 * Everything should be <code>static</code>.
	 */
	private Config() {
	}

	/**
	 * Loads the given config(s)
	 *
	 * @param reference Path to the base config to load
	 * @param user      Path to the (optional) user config to load, that overrides the defaults in the reference config
	 */
	public static void loadConfig(String reference, String user) {
		configFile = new ConfigurationFile(reference, user);
	}

	/**
	 * Loads the given config
	 *
	 * @param reference Path to the config to load
	 */
	public static void loadConfig(String reference) {
		configFile = new ConfigurationFile(reference);
	}

	/**
	 * Sets the given key to the given value in the game config
	 * This overrides values from the loaded user configs, unless {@link Config#unset(ConfigKey)} is called
	 *
	 * @param key   The config key to set
	 * @param value The value to set it to
	 */
	public static void set(ConfigKey key, Object value) {
		gameConfig.put(key, value);
	}

	/**
	 * Un-sets the given key, if it is set in the game config
	 * Has no effect if the key does not exist
	 *
	 * @param key The key to unset
	 */
	public static void unset(ConfigKey key) {
		gameConfig.remove(key);
	}

	/**
	 * Completely clears the current config of any changes that were
	 * made to it not specified in the config files.
	 * All values will have to be reloaded from the files when they
	 * are next needed, unless they are set again by code.
	 */
	public static void clearConfig() {
		gameConfig.clear();
	}

	/**
	 * Gets a value from the game config, or loaded config file(s) if not found.
	 *
	 * @param key  Key for the desired value
	 * @param type The desired return type
	 * @return The config value. Never <code>null</code>.
	 * @throws NoSuchElementException   When the provided {@code key} doesn't have a corresponding config value.
	 * @throws IllegalArgumentException When property can't be converted to {@code type}.
	 * @throws IllegalStateException    When provider is unable to fetch configuration value for the given {@code key}.
	 * @see {@link ConfigurationFile#get(String, Class)}
	 */
	private static <T> T get(ConfigKey key, Class<T> type) {
		Object gameValue = gameConfig.get(key);
		if (gameValue != null)
			return (T) gameValue;

		T value = configFile.get(key.getKey(), type);
		gameConfig.put(key, value);
		return value;
	}

	/**
	 * Gets a value from the game config, or loaded config file(s) if not found.
	 * Casts the value to a {@link String}.
	 *
	 * @param key Key for the desired value.
	 * @return The config value as a {@link String}. Never <code>null</code>.
	 * @throws NoSuchElementException   When the provided {@code key} doesn't have a corresponding config value.
	 * @throws IllegalArgumentException When property can't be converted to {@link String}.
	 * @throws IllegalStateException    When provider is unable to fetch configuration value for the given {@code key}.
	 * @see {@link #get(ConfigKey, Class)}
	 */
	public static String getString(ConfigKey key) {
		return get(key, String.class);
	}

	/**
	 * Gets a value from the game config, or loaded config file(s) if not found.
	 * Casts the value to an {@link Integer}.
	 *
	 * @param key Key for the desired value.
	 * @return The config value as an {@link Integer}. Never <code>null</code>.
	 * @throws NoSuchElementException   When the provided {@code key} doesn't have a corresponding config value.
	 * @throws IllegalArgumentException When property can't be converted to {@link Integer}.
	 * @throws IllegalStateException    When provider is unable to fetch configuration value for the given {@code key}.
	 * @see {@link #get(ConfigKey, Class)}
	 */
	public static Integer getInt(ConfigKey key) {
		return get(key, Integer.class);
	}

	/**
	 * Gets a value from the game config, or loaded config file(s) if not found.
	 * Casts the value to a {@link Boolean}.
	 *
	 * @param key Key for the desired value.
	 * @return The config value as a {@link Boolean}. Never <code>null</code>.
	 * @throws NoSuchElementException   When the provided {@code key} doesn't have a corresponding config value.
	 * @throws IllegalArgumentException When property can't be converted to {@link Boolean}.
	 * @throws IllegalStateException    When provider is unable to fetch configuration value for the given {@code key}.
	 * @see {@link #get(ConfigKey, Class)}
	 */
	public static Boolean getBoolean(ConfigKey key) {
		return get(key, Boolean.class);
	}

	/**
	 * Gets a value from the game config, or loaded config file(s) if not found.
	 * Casts the value to a {@link Float}.
	 *
	 * @param key Key for the desired value.
	 * @return The config value as a {@link Float}. Never <code>null</code>.
	 * @throws NoSuchElementException   When the provided {@code key} doesn't have a corresponding config value.
	 * @throws IllegalArgumentException When property can't be converted to {@link Float}.
	 * @throws IllegalStateException    When provider is unable to fetch configuration value for the given {@code key}.
	 * @see {@link #get(ConfigKey, Class)}
	 */
	public static Float getFloat(ConfigKey key) {
		return get(key, Float.class);
	}

	/**
	 * Gets a {@link SearchAlgorithm} from the config file.
	 * Defaults to def if not found or the config representation is unrecognised.
	 *
	 * @param key Key for desired Algorithm
	 * @param def Default {@link SearchAlgorithm} to return if none found
	 * @return The {@link SearchAlgorithm} referring to Key, or def if none found.
	 */
	public static SearchAlgorithm getAlgorithm(ConfigKey key, SearchAlgorithm def) {
		String algStr = getString(key).toUpperCase();
		switch (algStr) {
			case "A_STAR" : return SearchAlgorithm.A_STAR;
			case "DFS" : return SearchAlgorithm.DEPTH_FIRST;
			case "BFS" : return SearchAlgorithm.BREADTH_FIRST;
			case "DIJ" : return SearchAlgorithm.DIJKSTRA;
			default : return def;
		}
	}

	/**
	 * A {@link Map} between hex colour codes and {@link Color Colours}.
	 * The hex codes should be in the form {@code "FF00FF"} if the alpha is {@code FF},
	 * otherwise {@code "FF00FF33"}.
	 */
	private static final HashMap<String, Color> COLOUR_CACHE = new HashMap<>();

	public static Color getColor(ConfigKey key) {
		String hex = getString(key).toUpperCase();
		hex = hex.charAt(0) == '#' ? hex.substring(1) : hex;
		if (hex.length() == 8 && hex.substring(6, 8).equals("FF")) {
			hex = hex.substring(0, 6);
		}
		Color colour = COLOUR_CACHE.get(hex);
		if (colour == null) {
			colour = Color.valueOf(hex);
			COLOUR_CACHE.put(hex, colour);
		}
		return colour;
	}

}
