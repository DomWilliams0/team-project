package com.b3.util;

public class Config {

	private static ConfigurationFile configFile;

	private Config() {
		// no instantiation 4 u
	}

	public static void loadConfig(String reference, String user) {
		configFile = new ConfigurationFile(reference, user);
	}


	public static void loadConfig(String reference) {
		configFile = new ConfigurationFile(reference);
	}

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
