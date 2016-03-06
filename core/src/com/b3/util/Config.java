package com.b3.util;

import java.util.EnumMap;
import java.util.Map;

public class Config {

	private static ConfigurationFile configFile;

	/**
	 * This is a local override of the user config files.
	 * If a config value is set using {@link Config#set(ConfigKey, Object)},
	 * it will override the value set in the user config, unless it is
	 * unset using {@link Config#unset(ConfigKey)}
	 */
	private static Map<ConfigKey, Object> gameConfig = new EnumMap<>(ConfigKey.class);

	private Config() {
		// no instantiation 4 u
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
	 * Unsets the given key, if it is set in the game config
	 * Has no effect if the key does not exist
	 *
	 * @param key The key to unset
	 */
	public static void unset(ConfigKey key) {
		gameConfig.remove(key);
	}

	/**
	 * Gets a value from the game config, or loaded config file(s) if not found
	 *
	 * @param key  Key for the desired value
	 * @param type The desired return type
	 * @return The config value. Null is never returned, an exception is thrown if the key does not exist.
	 * @see {@link ConfigurationFile#get(String, Class)}
	 */
	private static <T> T get(ConfigKey key, Class<T> type) {
		Object gameValue = gameConfig.get(key);
		if (gameValue != null)
			return (T) gameValue;

		return configFile.get(key.getKey(), type);
	}

	// helpers
	public static String getString(ConfigKey key) {
		return get(key, String.class);
	}

	public static Integer getInt(ConfigKey key) {
		return get(key, Integer.class);
	}

	public static Boolean getBoolean(ConfigKey key) {
		return get(key, Boolean.class);
	}

	public static Float getFloat(ConfigKey key) {
		return get(key, Float.class);
	}
}
