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
}
