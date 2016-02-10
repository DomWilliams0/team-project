package com.b3.util;

public class Config {

	private static ConfigurationFile configFile;

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
	 * Gets a value from the loaded config file(s)
	 *
	 * @param key  Key for the desired value
	 * @param type The desired return type
	 * @return The config value. Null is never returned, an exception is thrown if the key does not exist.
	 * @see {@link ConfigurationFile#get(String, Class)}
	 */
	public static <T> T get(String key, Class<T> type) {
		return configFile.get(key, type);
	}

	// helpers
	public static String getString(String key) {
		return get(key, String.class);
	}

	public static Integer getInt(String key) {
		return get(key, Integer.class);
	}

	public static Boolean getBoolean(String key) {
		return get(key, Boolean.class);
	}

	public static Float getFloat(String key) {
		return get(key, Float.class);
	}
}
