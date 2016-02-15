package com.b3.util;

import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.compose.MergeConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.files.FilesConfigurationSource;
import org.cfg4j.source.reload.ReloadStrategy;
import org.cfg4j.source.reload.strategy.ImmediateReloadStrategy;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ConfigurationFile {
	private ConfigurationProvider provider;

	public ConfigurationFile(String referencePath) {
		this(referencePath, null);
	}

	/**
	 * @param referencePath Path to the default reference config
	 * @param userPath      Optional path to the user's config, which will overwrite properties in the reference config
	 */
	public ConfigurationFile(String referencePath, String userPath) {

		File refFile = new File(referencePath);
		File userFile = null;
		if (userPath != null)
			userFile = new File(userPath);

		if (!refFile.exists())
			throw new IllegalArgumentException("Could not find reference config: " + referencePath);
		if (userFile != null && !userFile.exists())
			System.err.println("Could not find user config '" + userPath + "', but continuing anyway");


		final File finalUserFile = userFile;
		ConfigFilesProvider fileProvider = () -> {
			ArrayList<Path> configs = new ArrayList<>(2);
			configs.add(Paths.get(refFile.getAbsolutePath()));

			if (finalUserFile != null)
				configs.add(Paths.get(finalUserFile.getAbsolutePath()));

			return configs;
		};
		ConfigurationSource source = new MergeConfigurationSource(new FilesConfigurationSource(fileProvider));
		ReloadStrategy reloadStrategy = new ImmediateReloadStrategy();

		provider = new ConfigurationProviderBuilder()
				.withConfigurationSource(source)
				.withReloadStrategy(reloadStrategy)
				.build();

	}

	/**
	 * Gets a value from the loaded config file(s)
	 *
	 * @param key  Key for the desired value
	 * @param type The desired return type
	 * @return The config value. Null is never returned, an exception is thrown if the key does not exist.
	 */
	public <T> T get(String key, Class<T> type) {
		return provider.getProperty(key, type);
	}
}
