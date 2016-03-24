package com.b3.util;

import org.cfg4j.provider.ConfigurationProvider;
import org.cfg4j.provider.ConfigurationProviderBuilder;
import org.cfg4j.source.ConfigurationSource;
import org.cfg4j.source.compose.MergeConfigurationSource;
import org.cfg4j.source.context.filesprovider.ConfigFilesProvider;
import org.cfg4j.source.files.FilesConfigurationSource;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collections;

/**
 * A configuration file which can have a base, reference config with default values,
 * and an optional user config that overrides the base config.
 *
 * @author dxw405
 */
public class ConfigurationFile {

	private ConfigurationProvider provider;

	/**
	 * Creates a new {@link ConfigurationFile} with no user config
	 * @param referencePath
	 */
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
		if (!refFile.canRead())
			throw new IllegalArgumentException("Could not read reference config: " + referencePath);
		if (userFile != null && !userFile.exists())
			System.err.println("Could not find user config '" + userPath + "', but continuing anyway");
		if (userFile != null && userFile.exists() && !userFile.canRead())
			System.err.println("Could not read user config '" + userPath + "', but continuing anyway");


		ConfigFilesProvider refConfigProvider = () -> Collections.singletonList(Paths.get(refFile.getAbsolutePath()));
		ConfigurationSource source;

		// merge reference with user config
		if (userFile != null && userFile.exists()) {
			final File finalUserFile = userFile;
			ConfigFilesProvider userConfigProvider = () -> Collections.singletonList(Paths.get(finalUserFile.getAbsolutePath()));
			source = new MergeConfigurationSource(
					new FilesConfigurationSource(refConfigProvider),
					new FilesConfigurationSource(userConfigProvider)
			);
		} else {
			source = new FilesConfigurationSource(refConfigProvider);
		}

		provider = new ConfigurationProviderBuilder()
				.withConfigurationSource(source)
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
